package extractors;

import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;

import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import model.AuthorFile;
import model.Contributor;
import model.File;
import model.Project;
import utils.ContributorsUtils;
import utils.RepositoryAnalyzer;

public class AuthorFileExtractor {

	private Project project;

	public AuthorFileExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runFirstAuthorAnalysis() throws GitAPIException {
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
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
