package analyzers;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;

import dao.FileVersionDAO;
import model.Commit;
import model.File;
import model.FileVersion;
import model.Project;
import utils.RepositoryAnalyzer;

public class FileAnalyzer {

	private Project project;

	public FileAnalyzer(Project project) {
		super();
		this.project = project;
	}

	public void run() {
		FileVersionDAO FileVersionDAO = new FileVersionDAO(); 
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		for (model.File file : files) {
			if(FileVersionDAO.existsByFileVersion(file, currentVersion) == false) {
				try {
					BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
					blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
					blameCommand.setFilePath(file.getPath());
					BlameResult blameResult = blameCommand.call();
					RawText rawText = blameResult.getResultContents();
					int fileSize = rawText.size();
					FileVersion FileVersion = new FileVersion(file, currentVersion, fileSize);
					FileVersionDAO.persist(FileVersion);
				} catch (GitAPIException | java.lang.NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
