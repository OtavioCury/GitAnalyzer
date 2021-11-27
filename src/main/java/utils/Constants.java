package utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Constants {
	
	public static String fullPath = "/home/otavio/Área de Trabalho/Analise doutorado/projeto_ihealth/ihealth/ihealth/.git";
	public static String projectName = "IHealth";
	public static String filesFile = "/home/otavio/Área de Trabalho/Analise doutorado/projeto_ihealth/ihealth/ihealth/filelist.log";
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
	
	public static double thresholdMantainer = 0.75;
}
