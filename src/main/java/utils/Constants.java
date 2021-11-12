package utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Constants {
	
	static public String fullPath = "/media/lost/e04b3034-2506-41c9-a1d4-e3d38fe04256/otavio/projetoIHealth/ihealth/ihealth/.git";
	static public String projectName = "IHealth";
	static public String filesFile = "/media/lost/e04b3034-2506-41c9-a1d4-e3d38fe04256/otavio/projetoIHealth/ihealth/ihealth/filelist.log";
	static public SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	
	static public List<String> invalidPaths = Arrays.asList(new String[]{"/dev/null"});
	static public List<String> analyzedExtensions = Arrays.asList(new String[]{"jhm.xml", "java"});
	
	static public double intercept = 5.28223;
	static public double addsCoef = 0.23173;
	static public double faCoef = 0.36151;
	static public double numDaysCoef = -0.19421;
	static public double sizeCoef = -0.28761;

}
