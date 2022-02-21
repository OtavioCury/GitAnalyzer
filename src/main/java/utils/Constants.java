package utils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Constants {
	
	public static String absPath = "/home/otavio/Desktop/GitAnalyzer/projetos/ihealth/";

	public static List<String> invalidPaths = Arrays.asList(new String[]{"/dev/null"});

	/**
	 * DOE coefficients
	 */
	public static final double interceptDoe = 5.28223;
	public static final double addsCoefDoe = 0.23173;
	public static final double faCoefDoe = 0.36151;
	public static final double numDaysCoefDoe = -0.19421;
	public static final double sizeCoefDoe = -0.28761;

	/**
	 * DOA coefficients
	 */
	public static final double interceptDoa = 3.293;
	public static final double faCoefDoa = 1.098;
	public static final double dlCoefDoa = 0.164;
	public static final double acCoefDoa = -0.321;

	public static final String ADD = new String("ADD");
	public static final String MODIFY = new String("MODIFY");
	public static final String DELETE = new String("DELETE");
	public static final String RENAME = new String ("RENAME");
	
	public static final String DOT = new String(".");
	public static final String LESS_THEN = new String("<");
	public static final String BIGGER_THEN = new String(">");
	public static final String OPEN_BRACKET = new String("[");
	public static final String FILE_SEPARATOR = new String("/");
	public static final String NUMBER_SIGN = new String("#");
	public static final String COMMA = new String(",");
	public static final String WHITESPACE = new String(" ");
	public static final String CLOSE_BRACKET = new String("]");
	public static final String NEW = new String("new");
	public static final String OPEN_PARENTHESE = new String("(");
	public static final String EMPTY = new String("");
	
	public static final String JAVA_EXTENSION = new String(".java");

	public static final double thresholdMantainer = 0.75;

	public static final int quantKnowledgedDevsByFile = 3;

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
		c.add(Calendar.MONTH, -3);
		return c.getTime();
	}
}
