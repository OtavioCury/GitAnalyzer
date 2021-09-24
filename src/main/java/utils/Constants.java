package utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Constants {
	
	static public String fullPath = "/home/otavio/analiseihealth/ihealth/ihealth/.git";
	static public String projectName = "IHealth";
	static public String filesFile = "/home/otavio/analiseihealth/ihealth/ihealth/filelist.log";
	static public SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	
	static public List<String> invalidPaths = Arrays.asList(new String[]{"/dev/null"});
	static public List<String> analyzedExtensions = Arrays.asList(new String[]{"jhm.xml", "java"});

}
