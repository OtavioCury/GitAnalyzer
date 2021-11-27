package utils;

public class FileUtils {
	
	public static String returnFileExtension(String path) {
		String extension = path.substring(path.lastIndexOf("/")+1);
		extension = extension.substring(extension.indexOf(".")+1);
		return extension;
	}

}
