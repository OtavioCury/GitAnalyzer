package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.ContributorDAO;
import model.AuthorFile;
import model.Contributor;
import model.File;

public class AuthorFileAnalyzer extends AnalyzerGeneric {
	
	public AuthorFileAnalyzer(List<File> files) {
		super();
		this.files = files;
	}
	
	public void runFirstAuthorAnalysis() throws GitAPIException {
		ContributorDAO authorDao = new ContributorDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					AuthorFile authorFile = authorFileDao.findByAuthorFile(contributor, file);
					if(authorFile == null) {
						boolean firstAuthor = commitFileDao.findByAuthorFileAdd(contributor, file);
						authorFile = new AuthorFile(contributor, file, firstAuthor);
						authorFileDao.persist(authorFile);
					}
				}
			}
		}
	}

}
