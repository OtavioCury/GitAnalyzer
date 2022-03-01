package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dao.ProjectDAO;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;
import utils.Constants;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.RepositoryAnalyzer;

@Path("/truckFactorService")
@Produces(MediaType.APPLICATION_JSON)
public class TruckFactorService {

	@GET
	public int getTruckFactor(@QueryParam("projectName") String projectName) {
		return 0;
	}

	@GET
	@Path("/truckFactorDoa")
	public int getTruckFactorDOAThreshold(@QueryParam("projectName") String projectName) {
		RepositoryAnalyzer.initRepository(projectName);
		Project project = getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		int tf = 0;
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		contributorsUtils.removeAlias(contributors);
		List<Contributor> removedContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageDOA(contributors, files);
			if(covarage < 0.5) 
				break;
			removedContributors.add(removeTopAuthor(contributors, files));
			tf = tf+1;
		}
		RepositoryAnalyzer.git.close();
		return tf;
	}

	@GET
	@Path("/truckFactorDoe")
	public int getTruckFactorDOEThreshold(@QueryParam("projectName") String projectName) {
		RepositoryAnalyzer.initRepository(projectName);
		Project project = getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		int tf = 0;
		ContributorsUtils contributorsUtils = new ContributorsUtils();
		List<Contributor> contributors = contributorsUtils.activeContributors(project);
		contributorsUtils.removeAlias(contributors);
		List<Contributor> removedContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageDOE(contributors, files);
			if(covarage < 0.5) 
				break;
			removedContributors.add(removeTopAuthor(contributors, files));
			tf = tf+1;
		}
		RepositoryAnalyzer.git.close();
		return tf;
	}

	private Contributor removeTopAuthor(List<Contributor> contributors, List<File> files) {
		int top = 0;
		Contributor topAuthor = null;
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils doeUtils = new DoeUtils(commit);
		HashMap<File, List<Contributor>> mapFileMaintainers = new HashMap<File, List<Contributor>>(); 
		for(Contributor contributorVersion: contributors) {
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
				for(Contributor contributor: experts) {
					if(contributor.getId().equals(contributorVersion.getId())) {
						isExpert = true;
						break;
					}
				}
				if(isExpert == true) {
					numberFilesExpert++;
				}
			}
			if(numberFilesExpert > top) {
				topAuthor = contributorVersion;
				top = numberFilesExpert;
			}
		}
		contributors.remove(topAuthor);
		return topAuthor;
	}

	private double getCoverageDOA(List<Contributor> contributors, List<File> files) {
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

	private double getCoverageDOE(List<Contributor> contributors, List<File> files) {
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

	private Project getProjectByName(String projectName) {
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		return project;
	}
	
}
