package extractors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dao.FileDAO;
import model.File;
import model.Project;
import utils.FileUtils;

public class FileGraphExtractor {
	
	private Project project;

	public FileGraphExtractor(Project project) {
		super();
		this.project = project;
	}
	
	public void runExtractor() {
		FileDAO fileDAO = new FileDAO();
		try {
			HashMap<String, String> filesString = FileUtils.currentFilesWithContents();
			HashMap<String, File> filesMap = new HashMap<String, File>();
			for(Map.Entry<String, String> map: filesString.entrySet()) {
				File file = fileDAO.findByPath(map.getKey(), project);
				if(file != null) {
					file.setContent(map.getValue());
					filesMap.put(map.getKey(), file);
				}
			}
			HashMap<String, File> filesJavaMap = new HashMap<String, File>();
			for(Map.Entry<String, File> map: filesMap.entrySet()) {
				File file = map.getValue();
				if(file.getExtension().equals("java")) {
					filesJavaMap.put(map.getKey(), map.getValue());
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
