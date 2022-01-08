package service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dao.ContributorVersionDAO;
import dao.ProjectDAO;
import model.Commit;
import model.Contributor;
import model.ContributorVersion;
import model.File;
import model.Project;
import utils.Constants;
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
	@Path("/truckFactorDao")
	public int getTruckFactorDOAThreshold(@QueryParam("projectName") String projectName) {
		RepositoryAnalyzer.initRepository(projectName);
		Project project = getProjectByName(projectName);
		List<File> files = RepositoryAnalyzer.getAnalyzedFiles(project);
		int tf = 0;
		List<ContributorVersion> contributors = getActiveContributors(); 
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageDOA(contributors, files);
			if(covarage < 0.5) 
				break;
			removeTopAuthor(contributors, files);
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
		List<ContributorVersion> contributors = getActiveContributors(); 
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageDOE(contributors, files);
			if(covarage < 0.5) 
				break;
			removeTopAuthor(contributors, files);
			tf = tf+1;
		}
		RepositoryAnalyzer.git.close();
		return tf;
	}
	
	private void removeTopAuthor(List<ContributorVersion> contributors, List<File> files) {
		int top = 0;
		ContributorVersion topAuthor = null;
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils doeUtils = new DoeUtils(commit);
		for(ContributorVersion contributorVersion: contributors) {
			int numberFilesExpert = 0;
			for(File file: files) {
				List<Contributor> experts = doeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
				boolean isExpert = false;
				for(Contributor contributor: experts) {
					if(contributor.getId().equals(contributorVersion.getContributor().getId())) {
						isExpert = true;
					}
				}
				if(isExpert == true) {
					numberFilesExpert++;
				}
			}
			if(numberFilesExpert > top) {
				topAuthor = contributorVersion;
			}
		}
		contributors.remove(topAuthor);
	}

	private double getCoverageDOA(List<ContributorVersion> contributors, List<File> files) {
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoaUtils doaUtils = new DoaUtils(commit);
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> contributorsMaintainers = doaUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			boolean contains = false;
			for(Contributor contributor: contributorsMaintainers) {
				if(contains == false) {
					for(ContributorVersion contributorVersion: contributors) {
						if(contains == false && contributor.getId().equals(contributorVersion.getContributor().getId())) {
							contains = true;
							numberFilesCovarage++;
						}
					}
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}
	
	private double getCoverageDOE(List<ContributorVersion> contributors, List<File> files) {
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils doeUtils = new DoeUtils(commit);
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> contributorsMaintainers = doeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			boolean contains = false;
			for(Contributor contributor: contributorsMaintainers) {
				if(contains == false) {
					for(ContributorVersion contributorVersion: contributors) {
						if(contains == false && contributor.getId().equals(contributorVersion.getContributor().getId())) {
							contains = true;
							numberFilesCovarage++;
						}
					}
				}
			}
		}
		return numberFilesCovarage/fileSize; 
	}

	private List<ContributorVersion> getActiveContributors(){
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		ContributorVersionDAO contributorVersionDAO = new ContributorVersionDAO();
		List<ContributorVersion> contributors = contributorVersionDAO.activeContributorVersion(commit);
		return contributors;
	}

	private Project getProjectByName(String projectName) {
		ProjectDAO projectDao = new ProjectDAO();
		Project project = projectDao.findByName(projectName);
		return project;
	}
}
