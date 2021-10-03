package main;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;

import dao.ContributorDAO;
import dao.AuthorFileDAO;
import dao.CommitFileDAO;
import dao.FileDAO;
import model.Contributor;
import model.AuthorFile;
import model.CommitFile;
import model.File;
import utils.Constants;
import utils.RepositoryAnalyzer;

public class ContributorFileAnalyzer {

	private List<model.File> files;

	public ContributorFileAnalyzer(List<File> files) {
		super();
		this.files = files;
	}

	public void run() {
		ContributorDAO authorDao = new ContributorDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO contributorFileDao = new AuthorFileDAO();
		FileDAO fileDao = new FileDAO();
		List<Contributor> contributors = authorDao.findAll(Contributor.class);
		for (Contributor contributor : contributors) {
			for (model.File file : files) {
				if(Constants.analyzedExtensions.contains(file.getExtension())) {
					CommitFile commitFile = commitFileDao.findByAuthorFileAdd(contributor, file);
					AuthorFile authorFile = contributorFileDao.findByAuthorFile(contributor, file);
					if(authorFile == null) {
						authorFile = new AuthorFile();
						int blame = 0;
						BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
						blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
						blameCommand.setFilePath(file.getPath());
						BlameResult blameResult = null;
						try {
							blameResult = blameCommand.call();
						} catch (GitAPIException e) {
							e.printStackTrace();
						}
						if(blameResult == null) {
							System.out.println();
						}
						RawText rawText = blameResult.getResultContents();
						if(file.getNumberLines() == 0) {
							file.setNumberLines(rawText.size());
							fileDao.merge(file);
						}
						int length = rawText.size();
						for (int i = 0; i < length; i++) {
							PersonIdent autor = blameResult.getSourceAuthor(i);
							if (autor.getName().equals(contributor.getName())) {
								blame++;
							}
						}
						authorFile.setAuthor(contributor);
						authorFile.setFile(file);
						authorFile.setNumLines(blame);
						if(commitFile != null) {
							authorFile.setFirstAuthor(true);
						}else {
							authorFile.setFirstAuthor(false);
						}
						contributorFileDao.persist(authorFile);
					}
				}
			}
		}
	}
}
