package extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.lib.Repository;

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

	public List<File> run(Repository repository) {
		FileDAO fileDao = new FileDAO();
		List<File> files = new ArrayList<File>();
		try {
			List<String> filesPath = FileUtils.currentFiles(repository);
			for (String path: filesPath) {
				if (fileDao.existsByFilePathProject(path, project) == false) {
					File file = new File(path, project, FileUtils.returnFileExtension(path));
					fileDao.persist(file);
				}
			}
			for (String path : filesPath) {
				File file = fileDao.findByPath(path, project);
				files.add(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

}
