package analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dao.ContributorDAO;
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
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.FileUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class FileImportanceAwareTruckFactor {

	private ContributorsUtils contributorsUtils = new ContributorsUtils();
	private DoaUtils doaUtils = new DoaUtils();
	private DoeUtils doeUtils = new DoeUtils();
	private ProjectVersionTruckFactorDAO projectVersionTruckFactorDAO = new ProjectVersionTruckFactorDAO();
	private Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
	private ProjectVersionDAO projectVersionDAO = new ProjectVersionDAO();

	public static void main(String[] args) {
		FileImportanceAwareTruckFactor fileImportanceAwareTruckFactor = new FileImportanceAwareTruckFactor();
		ContributorDAO contributorDAO = new ContributorDAO();

		ProjectExtractor projectExtractor = new ProjectExtractor();
		projectExtractor.run(args[0]);
		String projectName = projectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);

		//		for (KnowledgeMetric knowledgeMetric : KnowledgeMetric.values()) {
		//			for (FileImportanceMetric fileImportanceMetric : FileImportanceMetric.values()) {
		//				List<Contributor> contributors = contributorDAO.findByProjectDevs(project);
		//				LinkedHashMap<File, Double> fileValue = FileUtils.filesValues(project, 
		//						files, fileImportanceMetric);
		//				fileImportanceAwareTruckFactor.run(fileValue, knowledgeMetric, project, contributors, fileImportanceMetric);
		//			}
		//		}
		List<Contributor> contributors = contributorDAO.findByProjectDevs(project);
		LinkedHashMap<File, Double> fileValue = FileUtils.filesValues(project, 
				files, FileImportanceMetric.DEGREE_IN_OUT);
		fileImportanceAwareTruckFactor.run(fileValue, KnowledgeMetric.DOE, project, contributors, FileImportanceMetric.DEGREE_IN_OUT);
	}

	public void run(LinkedHashMap<File, Double> fileValue, KnowledgeMetric metric, Project project, 
			List<Contributor> contributors, FileImportanceMetric fileImportanceMetric) {

		for (Contributor contributor : contributors) {
			contributorsUtils.setAlias(contributor);
		}

		System.out.println("=========== File importance aware truckfactor "+metric.getName()+" and "+fileImportanceMetric.getName()+"===========");
		int tf = 0;

		contributorsUtils.removeAlias(contributors);
		contributorsUtils.sortContributorsByMetric(contributors, fileValue, metric);
		Collections.sort(contributors, new Comparator<Contributor>() {
			@Override
			public int compare(Contributor c1, Contributor c2) {
				return Double.compare(c2.getSumFileImportance(), c1.getSumFileImportance());
			}
		});
		List<Contributor> topContributors = new ArrayList<Contributor>();
		List<File> abandonedFiles = new ArrayList<File>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageFileImportance(contributors, fileValue, metric, abandonedFiles);
			//double covarage = getCoverage(contributors, files, metric);
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
			projectVersionTruckFactor = new ProjectVersionTruckFactor(projectVersion, topContributors, metric,
					fileImportanceMetric, TruckFactorType.FILE_IMPORTANCE_AWARE, abandonedFiles);
			projectVersionTruckFactorDAO.persist(projectVersionTruckFactor);
		}
		System.out.println("================ End of Analysis ===========");
	}

	private double getCoverageFileImportance(List<Contributor> contributors, LinkedHashMap<File, Double> filesValues, 
			KnowledgeMetric metric, List<File> abandonedFiles) {
		double sumImportance = 0.0;
		double sumImportanceCovarage = 0.0;
		for(Map.Entry<File, Double> fileValue: filesValues.entrySet()) {
			sumImportance = sumImportance + fileValue.getValue();
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = doaUtils.getMantainersByFile(fileValue.getKey());
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = doeUtils.getMantainersByFile(fileValue.getKey());
			}
			boolean isAbandoned = true;
			forMaintainers:for(Contributor expert: experts) {
				for (Contributor contributor : contributors) {
					Set<Contributor> contributorsAlias = contributor.getAlias();
					contributorsAlias.add(contributor);
					for (Contributor alias : contributorsAlias) {
						if(expert.getId().equals(alias.getId())) {
							sumImportanceCovarage = sumImportanceCovarage + fileValue.getValue();
							break forMaintainers;
						}
					}
				}
			}
			if (isAbandoned) {
				boolean present = false;
				for (File fileAbandoned : abandonedFiles) {
					if (fileValue.getKey().getId().equals(fileAbandoned.getId())) {
						present = true;
					}
				}
				if (present == false) {
					abandonedFiles.add(fileValue.getKey());
				}
			}
		}
		double coverage = sumImportanceCovarage/sumImportance;
		return coverage; 
	}

	private double getCoverage(List<Contributor> contributors, List<File> files, KnowledgeMetric metric) {
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = doaUtils.getMantainersByFile(file);
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = doeUtils.getMantainersByFile(file);
			}
			forMaintainers:for(Contributor expert: experts) {
				for (Contributor contributor : contributors) {
					Set<Contributor> contributorsAlias = contributor.getAlias();
					contributorsAlias.add(contributor);
					for (Contributor alias : contributorsAlias) {
						if(expert.getId().equals(alias.getId())) {
							numberFilesCovarage++;
							break forMaintainers;
						}
					}

				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}

}
