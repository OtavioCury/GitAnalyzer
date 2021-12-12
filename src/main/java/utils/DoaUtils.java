package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.AuthorDoaDAO;
import dao.CommitFileDAO;
import dto.ContributorDTO;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;

public class DoaUtils extends MetricsUtils{
	
	private CommitFileDAO commitFileDAO = new CommitFileDAO();
	private AuthorDoaDAO authorDoaDAO = new AuthorDoaDAO(); 

	public DoaUtils(Commit currentCommit) {
		super();
		this.currentCommit = currentCommit;
	}
	
	public double getDOA(int fa, int dl, int ac) {
		double faModel = Constants.faCoefDoa*fa;
		double dlModel = Constants.dlCoefDoa*dl;
		double acModel = Constants.acCoefDoa*Math.log(ac + 1);
		return Constants.interceptDoa + faModel + dlModel + acModel;
	}
	
	public double getContributorFileDOA(Contributor contributor, File file) {
		return getDOA(getFA(contributor, file), getDl(contributor, file),
				getAc(contributor, file));	
	}

	private int getAc(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		int numberCommits = commitFileDAO.numberCommitsFileOthersAuthorsVersion(contributors, file, currentCommit);
		return numberCommits;
	}

	private int getDl(Contributor contributor, File file) {
		List<Contributor> contributors = findAlias.getAlias(contributor);
		contributors.add(contributor);
		int numberCommits = commitFileDAO.numberCommitsFileAuthorsVersion(contributors, file, currentCommit);
		return numberCommits;
	}
	
	public List<ContributorDTO> getMostKnowledgedByFile(String filePath, String projectName){
		Project project = projectDAO.findByName(projectName);
		File file = fileDAO.findByPath(filePath, project);
		List<ContributorDTO> contributors = new ArrayList<ContributorDTO>();
		Set<Contributor> contributorsAndAlias = new HashSet<Contributor>();
		while(contributors.size() < Constants.quantKnowledgedDevsByFile) {
			Contributor contributor = authorDoaDAO.maxDoaByFileVersion(file, currentCommit, contributorsAndAlias);
			contributors.add(new ContributorDTO(contributor.getName(), contributor.getEmail()));
			contributorsAndAlias.add(contributor);
			contributorsAndAlias.addAll(findAlias.getAlias(contributor));
		}
		return contributors;
	}

}
