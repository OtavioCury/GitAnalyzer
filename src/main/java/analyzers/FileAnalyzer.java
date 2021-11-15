package analyzers;

import java.util.List;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;

import dao.FileDAO;
import model.File;
import utils.RepositoryAnalyzer;

public class FileAnalyzer extends AnalyzerGeneric{

	public FileAnalyzer(List<File> files) {
		super();
		this.files = files;
	}

	public void run() throws GitAPIException {
		FileDAO fileDao = new FileDAO();
		for (model.File file : files) {
			if(file.getNumberLines() == 0) {
				BlameCommand blameCommand = new BlameCommand(RepositoryAnalyzer.repository);
				blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
				blameCommand.setFilePath(file.getPath());
				BlameResult blameResult = blameCommand.call();
				RawText rawText = blameResult.getResultContents();
				int fileSize = rawText.size();
				file.setNumberLines(fileSize);
				fileDao.merge(file);
			}
		}
	}
}
