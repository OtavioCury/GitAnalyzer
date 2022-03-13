package analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import extractors.ProjectExtractor;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.Constants;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.FileUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class TruckFactorAnalyzer {
	
	private static Commit commit = RepositoryAnalyzer.getCurrentCommit();
	private static DoaUtils doaUtils = new DoaUtils(commit);
	private static ContributorsUtils contributorsUtils = new ContributorsUtils();
	
	public static void main(String[] args){
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		calculateClassifcalTruckFactor(project, files);
		//calculateFileAwareTruckFactor(project, files);
		RepositoryAnalyzer.git.close();
	}
		
	private static void calculateFileAwareTruckFactor(Project project, List<File> files) {
		System.out.println("=========== Analysis file value aware truckfactor DOA ===========");
		int tf = 0;
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		contributorsUtils.removeAlias(contributors);
		List<Contributor> removedContributors = new ArrayList<Contributor>();
		double sumFileValue = FileUtils.sumValueFilesCommit(project, files);
		double halfSumFileValue = sumFileValue/2;
		LinkedHashMap<File, Double> fileValue = FileUtils.filesCommitValues(project, files);
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageDOAFileValue(contributors, fileValue, sumFileValue);
			if(covarage < halfSumFileValue) 
				break;
			removedContributors.add(removeTopAuthorFileValueDOA(contributors, fileValue));
			tf = tf+1;
		}
		System.out.println("Top contributors file value aware truckfactor DOA");
		for(Contributor contributor: removedContributors) {
			System.out.println(contributor.getName());
		}
	}
	
	private static Contributor removeTopAuthorFileValueDOA(List<Contributor> contributors,
			LinkedHashMap<File, Double> fileValue) {
		double top = 0;
		Contributor topAuthor = null;
		HashMap<File, List<Contributor>> mapFileMaintainers = new HashMap<File, List<Contributor>>(); 
		for(Contributor contributor: contributors) {
			double valueFileExpert = 0;
			for(Map.Entry<File, Double> fileCommit: fileValue.entrySet()) {
				List<Contributor> experts = null;
				if(mapFileMaintainers.containsKey(fileCommit.getKey())) {
					experts = mapFileMaintainers.get(fileCommit.getKey()); 
				}else {
					experts = doaUtils.getMantainersByFile(fileCommit.getKey(), Constants.thresholdMantainer);
					mapFileMaintainers.put(fileCommit.getKey(), experts);
				}
				boolean isExpert = false;
				for(Contributor aux: experts) {
					if(aux.getId().equals(contributor.getId())) {
						isExpert = true;
						break;
					}
				}
				if(isExpert == true) {
					valueFileExpert = valueFileExpert + fileCommit.getValue();
				}
			}
			if(valueFileExpert > top) {
				topAuthor = contributor;
				top = valueFileExpert;
			}
		}
		contributors.remove(topAuthor);
		return topAuthor;
	}

	private static void calculateClassifcalTruckFactor(Project project, List<File> files) {
		System.out.println("=========== Analysis avelino's truckfactor DOA ===========");
		int tf = 0;
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		contributorsUtils.removeAlias(contributors);
		List<Contributor> removedContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageDOA(contributors, files);
			if(covarage < 0.5) 
				break;
			removedContributors.add(removeTopAuthorDOA(contributors, files));
			tf = tf+1;
		}
		System.out.println("Top contributors truckfactor DOA");
		for(Contributor contributor: removedContributors) {
			System.out.println(contributor.getName());
		}
//		System.out.println("=========== Analysis avelino's truckfactor DOE ===========");
//		tf = 0;
//		contributors = contributorsUtils.activeContributors(project);
//		contributorsUtils.removeAlias(contributors);
//		removedContributors = new ArrayList<Contributor>();
//		while(contributors.isEmpty() == false) {
//			double covarage = getCoverageDOE(contributors, files);
//			if(covarage < 0.5) 
//				break;
//			removedContributors.add(removeTopAuthorDOE(contributors, files));
//			tf = tf+1;
//		}
//		System.out.println("Top contributors truckfactor DOE");
//		for(Contributor contributor: removedContributors) {
//			System.out.println(contributor.getName());
//		}
	}
	
	private static double getCoverageDOE(List<Contributor> contributors, List<File> files) {
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils doeUtils = new DoeUtils(commit);
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> contributorsMaintainers = doeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			forMaintainers:for(Contributor contributor: contributorsMaintainers) {
				for(Contributor contributorVersion: contributors) {
					if(contributor.getId().equals(contributorVersion.getId())) {
						numberFilesCovarage++;
						break forMaintainers;
					}
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}
	
	private static double getCoverageDOAFileValue(List<Contributor> contributors, LinkedHashMap<File, Double> fileValue, 
			double sumFileValue) {
		double sumFileValueCoverage = 0;
		for(Map.Entry<File, Double> fileCommit: fileValue.entrySet()) {
			List<Contributor> contributorsMaintainers = doaUtils.getMantainersByFile(fileCommit.getKey(), 
					Constants.thresholdMantainer);
			forMaintainers:for(Contributor contributor: contributorsMaintainers) {
				for(Contributor contributorVersion: contributors) {
					if(contributor.getId().equals(contributorVersion.getId())) {
						sumFileValueCoverage = sumFileValueCoverage + fileCommit.getValue();
						break forMaintainers;
					}
				}
			}
		}
		return sumFileValueCoverage; 
	}
	
	private static double getCoverageDOA(List<Contributor> contributors, List<File> files) {
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoaUtils doaUtils = new DoaUtils(commit);
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> contributorsMaintainers = doaUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			forMaintainers:for(Contributor contributor: contributorsMaintainers) {
				for(Contributor contributorVersion: contributors) {
					if(contributor.getId().equals(contributorVersion.getId())) {
						numberFilesCovarage++;
						break forMaintainers;
					}
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}
	
	private static Contributor removeTopAuthorDOE(List<Contributor> contributors, List<File> files) {
		int top = 0;
		Contributor topAuthor = null;
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils doeUtils = new DoeUtils(commit);
		HashMap<File, List<Contributor>> mapFileMaintainers = new HashMap<File, List<Contributor>>(); 
		for(Contributor contributor: contributors) {
			int numberFilesExpert = 0;
			for(File file: files) {
				List<Contributor> experts = null;
				if(mapFileMaintainers.containsKey(file)) {
					experts = mapFileMaintainers.get(file); 
				}else {
					experts = doeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
					mapFileMaintainers.put(file, experts);
				}
				boolean isExpert = false;
				for(Contributor aux: experts) {
					if(aux.getId().equals(contributor.getId())) {
						isExpert = true;
						break;
					}
				}
				if(isExpert == true) {
					numberFilesExpert++;
				}
			}
			if(numberFilesExpert > top) {
				topAuthor = contributor;
				top = numberFilesExpert;
			}
		}
		contributors.remove(topAuthor);
		return topAuthor;
	}
	
	private static Contributor removeTopAuthorDOA(List<Contributor> contributors, List<File> files) {
		int top = 0;
		Contributor topAuthor = null;
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoaUtils doaUtils = new DoaUtils(commit);
		HashMap<File, List<Contributor>> mapFileMaintainers = new HashMap<File, List<Contributor>>(); 
		for(Contributor contributor: contributors) {
			int numberFilesExpert = 0;
			for(File file: files) {
				List<Contributor> experts = null;
				if(mapFileMaintainers.containsKey(file)) {
					experts = mapFileMaintainers.get(file); 
				}else {
					experts = doaUtils.getMantainersByFile(file, Constants.thresholdMantainer);
					mapFileMaintainers.put(file, experts);
				}
				boolean isExpert = false;
				for(Contributor aux: experts) {
					if(aux.getId().equals(contributor.getId())) {
						isExpert = true;
						break;
					}
				}
				if(isExpert == true) {
					numberFilesExpert++;
				}
			}
			if(numberFilesExpert > top) {
				topAuthor = contributor;
				top = numberFilesExpert;
			}
		}
		contributors.remove(topAuthor);
		return topAuthor;
	}
}
