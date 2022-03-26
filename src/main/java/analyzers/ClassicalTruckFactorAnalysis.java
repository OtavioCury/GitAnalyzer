package analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dao.ContributorDAO;
import dao.ProjectVersionDAO;
import dao.ProjectVersionTruckFactorDAO;
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
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class ClassicalTruckFactorAnalysis {
	
	private static ContributorsUtils contributorsUtils = new ContributorsUtils();
	
	public static void main(String[] args) {
		ContributorDAO contributorDAO = new ContributorDAO();
		ProjectVersionDAO projectVersionDAO = new ProjectVersionDAO();
		ProjectVersionTruckFactorDAO projectVersionTruckFactorDAO = new ProjectVersionTruckFactorDAO();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		
		ProjectExtractor.init(args[0]);
		String projectName = ProjectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		
		KnowledgeMetric metric = KnowledgeMetric.DOE;
		
		System.out.println("=========== Analysis avelino's truckfactor "+metric.getName()+" ===========");
		int tf = 0;
		List<Contributor> contributors = contributorDAO.findByProject(project);
		contributorsUtils.removeAlias(contributors);
		contributorsUtils.sortContributorsByMetric(contributors, files, metric);
		Collections.sort(contributors, new Comparator<Contributor>() {
		    @Override
		    public int compare(Contributor c1, Contributor c2) {
		        return Integer.compare(c2.getNumberFilesAuthor(), c1.getNumberFilesAuthor());
		    }
		});
		List<Contributor> topContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverage(contributors, files, metric);
			if(covarage < 0.5) 
				break;
			topContributors.add(contributors.get(0));
			contributors.remove(0);
			tf = tf+1;
		}
		System.out.println("Top contributors");
		for(Contributor contributor: topContributors) {
			System.out.println(contributor.getName());
		}
		
		ProjectVersion projectVersion = projectVersionDAO.findByProjectVersion(project, currentVersion);
		if (projectVersion == null) {
			projectVersion = new ProjectVersion(project, currentVersion);
			projectVersionDAO.persist(projectVersion);
		}
		ProjectVersionTruckFactor projectVersionTruckFactor = 
				projectVersionTruckFactorDAO.findByProjectVersionTruckFactor(projectVersion, metric, null, TruckFactorType.CLASSICAL);
		if (projectVersionTruckFactor == null) {
			projectVersionTruckFactor = new ProjectVersionTruckFactor(projectVersion, topContributors, metric, null, TruckFactorType.CLASSICAL);
			projectVersionTruckFactorDAO.persist(projectVersionTruckFactor);
		}
		System.out.println("================ End of Analysis ===========");
	}
	
	private static double getCoverage(List<Contributor> contributors, List<File> files, KnowledgeMetric metric) {
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = DoaUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = DoeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			}
			forMaintainers:for(Contributor expert: experts) {
				for(Contributor contributor: contributors) {
					if(expert.getId().equals(contributor.getId())) {
						numberFilesCovarage++;
						break forMaintainers;
					}
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}
}
