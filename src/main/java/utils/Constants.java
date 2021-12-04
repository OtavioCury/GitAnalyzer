package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Constants {
	
	public static String fullPath = "C:\\Users\\OTAVIO\\Desktop\\GitAnalyzer\\projetos\\ihealth\\.git";
	public static String projectName = "IHealth";
	public static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	
	public static List<String> invalidPaths = Arrays.asList(new String[]{"/dev/null"});
	public static List<String> analyzedExtensions = Arrays.asList(new String[]{"jhm.xml", "java"});
	
	public static double intercept = 5.28223;
	public static double addsCoef = 0.23173;
	public static double faCoef = 0.36151;
	public static double numDaysCoef = -0.19421;
	public static double sizeCoef = -0.28761;
	
	public static final String ADD = new String("ADD");
	public static final String MODIFY = new String("MODIFY");
	public static final String DELETE = new String("DELETE");
	public static final String RENAME = new String ("RENAME");
	public static ArrayList<String> extensions = new ArrayList<String>(
            Arrays.asList("java",
                          "jhm.xml",
                          "hbm.xml",
                          "tag"));
	
	public static double thresholdMantainer = 0.75;
	
	public static Date analisysDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2017, 12, 31, 59, 59, 59);
		Date date = calendar.getTime();
		return date;
	}
	
	public static Date thresholdDateDisable() {
		Date referenceDate = new Date();
		Calendar c = Calendar.getInstance(); 
		c.setTime(referenceDate); 
		c.add(Calendar.MONTH, -5);
		return c.getTime();
	}
}
