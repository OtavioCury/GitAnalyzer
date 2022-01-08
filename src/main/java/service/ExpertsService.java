package service;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dto.ContributorDTO;
import model.Commit;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.RepositoryAnalyzer;


@Path("/expertsService")
@Produces(MediaType.APPLICATION_JSON)
public class ExpertsService {
	
	@GET
	public HashMap<String, List<ContributorDTO>> getExpertsDoeDoa(@QueryParam("projectName") String projectName, 
			@QueryParam("filePath") String filePath) {
		HashMap<String, List<ContributorDTO>> map = new HashMap<String, List<ContributorDTO>>(); 
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils modelDOE = new DoeUtils(commit);
		DoaUtils modelDOA = new DoaUtils(commit);
		List<ContributorDTO> contributorsDOE = modelDOE.getMostKnowledgedByFile(filePath, projectName);
		List<ContributorDTO> contributorsDOA = modelDOA.getMostKnowledgedByFile(filePath, projectName);
		map.put("doe", contributorsDOE);
		map.put("doa", contributorsDOA);
		return map;
	}
	
	@GET
	@Path("/doe")
	public List<ContributorDTO> getExpertsDOE(@QueryParam("projectName") String projectName, 
			@QueryParam("filePath") String filePath) {
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoeUtils modelDOE = new DoeUtils(commit);
		List<ContributorDTO> contributors = modelDOE.getMostKnowledgedByFile(filePath, projectName);
		return contributors;
	}
	
	@GET
	@Path("/doa")
	public List<ContributorDTO> getExpertsDOA(@QueryParam("projectName") String projectName, 
			@QueryParam("filePath") String filePath) {
		Commit commit = RepositoryAnalyzer.getCurrentCommit();
		DoaUtils modelDOA = new DoaUtils(commit);
		List<ContributorDTO> contributors = modelDOA.getMostKnowledgedByFile(filePath, projectName);
		return contributors;
	}
	
}
