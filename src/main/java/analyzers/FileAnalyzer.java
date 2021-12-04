package analyzers;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;

import dao.FileCommitDAO;
import model.Commit;
import model.File;
import model.FileCommit;
import model.Project;
import utils.FileUtils;
import utils.RepositoryAnalyzer;

public class FileAnalyzer extends AnalyzerGeneric{

	public FileAnalyzer(Project project) {
		super();
		this.project = project;
	}

	public void run() {
		FileCommitDAO fileCommitDAO = new FileCommitDAO(); 
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		List<File> files = FileUtils.filesToBeAnalyzed(project);
		for (model.File file : files) {
			if(fileCommitDAO.existsByFileCommit(file, currentVersion) == false) {
				try {
					BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
					blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
					blameCommand.setFilePath(file.getPath());
					BlameResult blameResult = blameCommand.call();
					RawText rawText = blameResult.getResultContents();
					int fileSize = rawText.size();
					FileCommit fileCommit = new FileCommit(file, currentVersion, fileSize);
					fileCommitDAO.persist(fileCommit);
				} catch (GitAPIException | java.lang.NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
