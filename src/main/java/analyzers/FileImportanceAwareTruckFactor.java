package analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dao.ProjectVersionDAO;
import dao.ProjectVersionTruckFactorDAO;
import enums.FileImportanceMetric;
import enums.KnowledgeMetric;
import enums.TruckFactorType;
import extractors.ProjectExtractor;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import model.ProjectVersion;
import model.ProjectVersionTruckFactor;
import utils.Constants;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.FileUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class FileImportanceAwareTruckFactor {
	
	private static ContributorsUtils contributorsUtils = new ContributorsUtils();
	
	public static void main(String[] args) {
		ProjectVersionDAO projectVersionDAO = new ProjectVersionDAO();
		ProjectVersionTruckFactorDAO projectVersionTruckFactorDAO = new ProjectVersionTruckFactorDAO();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		
		KnowledgeMetric metric = KnowledgeMetric.DOE;
		FileImportanceMetric fileImportanceMetric = FileImportanceMetric.COMMITS;
		
		System.out.println("=========== File importance aware truckfactor "+metric.getName()+" and "+fileImportanceMetric.getName()+"===========");
		int tf = 0;
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		contributorsUtils.removeAlias(contributors);
		LinkedHashMap<File, Double> fileValue = FileUtils.filesValues(project, 
				files, fileImportanceMetric);
		contributorsUtils.sortContributorsByMetric(contributors, fileValue, metric);
		Collections.sort(contributors, new Comparator<Contributor>() {
		    @Override
		    public int compare(Contributor c1, Contributor c2) {
		        return Double.compare(c2.getSumFileImportance(), c1.getSumFileImportance());
		    }
		});
		List<Contributor> topContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageFileImportance(contributors, fileValue, metric);
			if(covarage < 0.5) 
				break;
			topContributors.add(contributors.get(0));
			contributors.remove(0);
			tf = tf+1;
		}
		System.out.println("Truck factor: "+tf);
		System.out.println("Top contributors");
		for(Contributor contributor: topContributors) {
			System.out.println(contributor.getName() +" "+contributor.getEmail());
		}
		
		ProjectVersion projectVersion = projectVersionDAO.findByProjectVersion(project, currentVersion);
		if (projectVersion == null) {
			projectVersion = new ProjectVersion(project, currentVersion);
			projectVersionDAO.persist(projectVersion);
		}
		ProjectVersionTruckFactor projectVersionTruckFactor = 
				projectVersionTruckFactorDAO.findByProjectVersionTruckFactor(projectVersion, metric, fileImportanceMetric, TruckFactorType.FILE_IMPORTANCE_AWARE);
		if (projectVersionTruckFactor == null) {
			projectVersionTruckFactor = new ProjectVersionTruckFactor(projectVersion, topContributors, metric, fileImportanceMetric, TruckFactorType.FILE_IMPORTANCE_AWARE);
			projectVersionTruckFactorDAO.persist(projectVersionTruckFactor);
		}
	}
	
	private static double getCoverageFileImportance(List<Contributor> contributors, LinkedHashMap<File, Double> filesValues, KnowledgeMetric metric) {
		double sumImportance = 0.0;
		double sumImportanceCovarage = 0.0;
		for(Map.Entry<File, Double> fileValue: filesValues.entrySet()) {
			sumImportance = sumImportance + fileValue.getValue();
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = DoaUtils.getMantainersByFile(fileValue.getKey(), Constants.thresholdMantainer);
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = DoeUtils.getMantainersByFile(fileValue.getKey(), Constants.thresholdMantainer);
			}
			forMaintainers:for(Contributor expert: experts) {
				for(Contributor contributor: contributors) {
					if(expert.getId().equals(contributor.getId())) {
						sumImportanceCovarage = sumImportanceCovarage + fileValue.getValue();
						break forMaintainers;
					}
				}
			}
		}
		double coverage = sumImportanceCovarage/sumImportance;
		return coverage; 
	}

}
