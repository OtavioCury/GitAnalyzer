package analyzers;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import model.AuthorFile;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.FileUtils;

public class AuthorFileAnalyzer {
	
	private Project project;
	
	public AuthorFileAnalyzer(Project project) {
		super();
		this.project = project;
	}
	
	public void runFirstAuthorAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		List<Contributor> contributors = ContributorsUtils.activeContributors(project);
		List<File> files = FileUtils.filesToBeAnalyzed(project);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(commitFileDao.existsByAuthorFile(contributor, file) == true) {
					if(authorFileDao.existsByAuthorFile(contributor, file) == false) {
						boolean firstAuthor = commitFileDao.findByAuthorFileAdd(contributor, file);
						AuthorFile authorFile = new AuthorFile(contributor, file, firstAuthor);
						authorFileDao.persist(authorFile);
					}
				}
			}
		}
	}

}
