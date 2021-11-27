package analyzers;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;

import dao.FileCommitDAO;
import dao.FileDAO;
import model.Commit;
import model.File;
import model.FileCommit;
import utils.CommitsUtils;
import utils.RepositoryAnalyzer;

public class FileAnalyzer extends AnalyzerGeneric{

	public FileAnalyzer(List<File> files) {
		super();
		this.files = files;
	}

	public void run() throws GitAPIException {
		FileCommitDAO fileCommitDAO = new FileCommitDAO(); 
		Commit currentVersion = CommitsUtils.getCurrentVersion();
		for (model.File file : files) {
			if(fileCommitDAO.findByFileCommit(file, currentVersion) == false) {
				BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
				blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
				blameCommand.setFilePath(file.getPath());
				BlameResult blameResult = blameCommand.call();
				RawText rawText = blameResult.getResultContents();
				int fileSize = rawText.size();
				FileCommit fileCommit = new FileCommit(file, currentVersion, fileSize);
				fileCommitDAO.persist(fileCommit);
			}
		}
	}
}
