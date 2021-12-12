package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Constants {
	
	public static List<String> invalidPaths = Arrays.asList(new String[]{"/dev/null"});
	public static List<String> analyzedExtensions = Arrays.asList(new String[]{"jhm.xml", "java"});
	
	/**
	 * DOE coefficients
	 */
	public static double interceptDoe = 5.28223;
	public static double addsCoefDoe = 0.23173;
	public static double faCoefDoe = 0.36151;
	public static double numDaysCoefDoe = -0.19421;
	public static double sizeCoefDoe = -0.28761;
	
	/**
	 * DOA coefficients
	 */
	public static double interceptDoa = 3.293;
	public static double faCoefDoa = 1.098;
	public static double dlCoefDoa = 0.164;
	public static double acCoefDoa = -0.321;
	
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
	
	public static int quantKnowledgedDevsByFile = 3;
	
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
