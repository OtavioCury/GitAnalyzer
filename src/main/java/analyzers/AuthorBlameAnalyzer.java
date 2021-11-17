package analyzers;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;

import dao.AuthorBlameDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import model.AuthorBlame;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import utils.RepositoryAnalyzer;

public class AuthorBlameAnalyzer extends AnalyzerGeneric {

	public AuthorBlameAnalyzer(List<File> files) {
		super();
		this.files = files;
	}

	public void runBlameAnalysis() throws GitAPIException {
		ContributorDAO authorDao = new ContributorDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorBlameDAO authorBlameDao = new AuthorBlameDAO();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		Commit lastCommit = RepositoryAnalyzer.getLastCommit();
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorBlameDao.existsByAuthorVersion(authorFile, lastCommit) == false) {
						AuthorBlame authorBlame = authorBlameDao.findByAuthorVersion(authorFile, lastCommit);
						int blame = 0;
						BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
						blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
						blameCommand.setFilePath(file.getPath());
						BlameResult blameResult = blameCommand.call();
						for (int i = 0; i < file.getNumberLines(); i++) {
							PersonIdent autor = blameResult.getSourceAuthor(i);
							if (autor.getName().equals(contributor.getName())) {
								blame++;
							}
						}
						authorBlame = new AuthorBlame(authorFile, lastCommit, blame);
						authorBlameDao.persist(authorBlame);
					}
				}
			}
		}
	}
}
