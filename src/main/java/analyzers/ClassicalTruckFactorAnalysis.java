package analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

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
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.ProjectUtils;
import utils.RepositoryAnalyzer;

public class ClassicalTruckFactorAnalysis {

	private ContributorsUtils contributorsUtils = new ContributorsUtils();
	private DoaUtils doaUtils = new DoaUtils();
	private DoeUtils doeUtils = new DoeUtils();
	private ProjectVersionTruckFactorDAO projectVersionTruckFactorDAO = new ProjectVersionTruckFactorDAO();
	private Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
	private ProjectVersionDAO projectVersionDAO = new ProjectVersionDAO();

	public static void main(String[] args) {
		ClassicalTruckFactorAnalysis classicalTruckFactorAnalysis = new ClassicalTruckFactorAnalysis();
		ContributorDAO contributorDAO = new ContributorDAO();

		ProjectExtractor projectExtractor = new ProjectExtractor();
		projectExtractor.run(args[0]);
		String projectName = projectExtractor.extractProjectName(args[0]);
		RepositoryAnalyzer.initRepository(projectName);
		Project project = ProjectUtils.getProjectByName(projectName);

		for (KnowledgeMetric metric : KnowledgeMetric.values()) {
			List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
			List<Contributor> contributors = contributorDAO.findByProjectDevs(project);
			classicalTruckFactorAnalysis.run(files, metric, project, contributors);
		}
	}

	public void run(List<File> files, KnowledgeMetric metric, Project project, List<Contributor> contributors) {

		for (Contributor contributor : contributors) {
			contributorsUtils.setAlias(contributor);
		}

		System.out.println("=========== Analysis avelino's truckfactor "+metric.getName()+" ===========");
		int tf = 0;

		contributorsUtils.removeAlias(contributors);
		contributorsUtils.sortContributorsByMetric(contributors, files, metric);
		Collections.sort(contributors, new Comparator<Contributor>() {
			@Override
			public int compare(Contributor c1, Contributor c2) {
				return Integer.compare(c2.getNumberFilesAuthor(), c1.getNumberFilesAuthor());
			}
		});
		List<Contributor> topContributors = new ArrayList<Contributor>();
		List<File> abandonedFiles = new ArrayList<File>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverage(contributors, files, metric, abandonedFiles);
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
			projectVersionTruckFactor = new ProjectVersionTruckFactor(projectVersion, topContributors, metric, null, TruckFactorType.CLASSICAL, abandonedFiles);
			projectVersionTruckFactorDAO.persist(projectVersionTruckFactor);
		}
		System.out.println("================ End of Analysis ===========");
	}

	private double getCoverage(List<Contributor> contributors, List<File> files, 
			KnowledgeMetric metric, List<File> abandonedFiles) {
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = doaUtils.getMantainersByFile(file);
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = doeUtils.getMantainersByFile(file);
			}
			boolean isAbandoned = true;
			forMaintainers:for(Contributor expert: experts) {
				for (Contributor contributor : contributors) {
					Set<Contributor> contributorsAlias = contributor.getAlias();
					contributorsAlias.add(contributor);
					for (Contributor alias : contributorsAlias) {
						if(expert.getId().equals(alias.getId())) {
							numberFilesCovarage++;
							isAbandoned = false;
							break forMaintainers;
						}
					}
					
				}
			}
			if (isAbandoned) {
				boolean present = false;
				for (File fileAbandoned : abandonedFiles) {
					if (file.getId().equals(fileAbandoned.getId())) {
						present = true;
					}
				}
				if (present == false) {
					abandonedFiles.add(file);
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}
}
