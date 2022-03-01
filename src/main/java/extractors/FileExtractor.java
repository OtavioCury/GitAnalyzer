package extractors;

import java.io.IOException;
import java.util.List;

import dao.FileDAO;
import model.File;
import model.Project;
import utils.FileUtils;

public class FileExtractor {

	private Project project;

	public FileExtractor(Project project) {
		super();
		this.project = project;
	}

	public void run() {
		FileDAO fileDao = new FileDAO();
		try {
			List<String> filesPath = FileUtils.currentFiles();
			for (String path: filesPath) {
				if (fileDao.existsByFilePathProject(path, project) == false) {
					File file = new File(path, project, FileUtils.returnFileExtension(path));
					fileDao.persist(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
