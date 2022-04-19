package extractors;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorFileDAO;
import dao.CommitDAO;
import dao.ContributorDAO;
import model.AuthorFile;
import model.Contributor;
import model.File;
import model.Project;
import utils.RepositoryAnalyzer;

public class AuthorFileExtractor {

	private Project project;

	public AuthorFileExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runFirstAuthorAnalysis() throws GitAPIException {
		CommitDAO commitDao = new CommitDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		ContributorDAO contributorDAO = new ContributorDAO();
		List<Contributor> contributors = contributorDAO.findByProjectDevs(project);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(authorFileDao.existsByAuthorFile(contributor, file) == false) {
					if(commitDao.existsByAuthorFile(contributor, file) == true) {
						boolean firstAuthor = commitDao.findByAuthorFileAdd(contributor, file);
						AuthorFile authorFile = new AuthorFile(contributor, file, firstAuthor);
						authorFileDao.persist(authorFile);
					}
				}
			}
		}
	}

}
