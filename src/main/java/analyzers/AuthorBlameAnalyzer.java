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
import dao.FileCommitDAO;
import model.AuthorBlame;
import model.AuthorFile;
import model.Commit;
import model.Contributor;
import model.File;
import model.FileCommit;
import model.Project;
import utils.ContributorsUtils;
import utils.FileUtils;
import utils.RepositoryAnalyzer;

public class AuthorBlameAnalyzer extends AnalyzerGeneric {

	public AuthorBlameAnalyzer(Project project) {
		super();
		this.project = project;
	}

	public void runBlameAnalysis() {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		AuthorBlameDAO authorBlameDao = new AuthorBlameDAO();
		FileCommitDAO fileCommitDAO = new FileCommitDAO(); 
		List<File> files = FileUtils.filesToBeAnalyzed(project);
		List<Contributor> contributors = ContributorsUtils.activeContributors(project);
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorBlameDao.existsByAuthorVersion(authorFile, currentCommit) == false
							&& fileCommitDAO.existsByFileCommit(file, currentCommit)) {
						try {
							FileCommit fileCommit = fileCommitDAO.findByFileCommit(file, currentCommit); 
							AuthorBlame authorBlame = authorBlameDao.findByAuthorVersion(authorFile, currentCommit);
							int blame = 0;
							BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
							blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
							blameCommand.setFilePath(file.getPath());
							BlameResult blameResult = blameCommand.call();
							for (int i = 0; i < fileCommit.getNumberLines(); i++) {
								PersonIdent autor = blameResult.getSourceAuthor(i);
								if (autor.getName().equals(contributor.getName())) {
									blame++;
								}
							}
							authorBlame = new AuthorBlame(authorFile, currentCommit, blame);
							authorBlameDao.persist(authorBlame);
						} catch (GitAPIException | java.lang.NullPointerException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
