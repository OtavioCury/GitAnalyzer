package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.AuthorDoaDAO;
import dao.CommitFileDAO;
import dto.ContributorDTO;
import model.AuthorDOA;
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
		List<Contributor> contributors = contributorsUtils.getAlias(contributor);
		contributors.add(contributor);
		Set<File> files = getFilesRenames(file);
		int numberCommits = commitFileDAO.numberCommitsFileOthersAuthorsVersion(contributors, files, currentCommit);
		return numberCommits;
	}

	private int getDl(Contributor contributor, File file) {
		List<Contributor> contributors = contributorsUtils.getAlias(contributor);
		contributors.add(contributor);
		Set<File> files = getFilesRenames(file);
		int numberCommits = commitFileDAO.numberCommitsFileAuthorsVersion(contributors, files, currentCommit);
		return numberCommits;
	}
	
	public List<Contributor> getMantainersByFile(File file, double threshold){
		List<Contributor> mantainers = new ArrayList<Contributor>();
		List<AuthorDOA> doas = authorDoaDAO.findByFileVersion(file, currentCommit);
		if(doas != null && doas.size() > 0) {
			AuthorDOA maxDoa = doas.stream().max(Comparator.comparing(AuthorDOA::getDegreeOfAuthorship)).get();
			for(AuthorDOA doa: doas) {
				double normalizedDoe = doa.getDegreeOfAuthorship()/maxDoa.getDegreeOfAuthorship();
				if(normalizedDoe > threshold) {
					mantainers.add(doa.getAuthorFile().getAuthor());
				}
			}
		}
		return mantainers;
	}
	
	public List<ContributorDTO> getMostKnowledgedByFile(String filePath, String projectName){
		Project project = projectDAO.findByName(projectName);
		File file = fileDAO.findByPath(filePath, project);
		Set<File> files = getFilesRenames(file);
		List<ContributorDTO> contributors = new ArrayList<ContributorDTO>();
		Set<Contributor> contributorsAndAlias = new HashSet<Contributor>();
		while(contributors.size() < Constants.quantKnowledgedDevsByFile) {
			Contributor contributor = authorDoaDAO.maxDoaByFileVersion(files, currentCommit, contributorsAndAlias);
			contributors.add(new ContributorDTO(contributor.getName(), contributor.getEmail()));
			contributorsAndAlias.add(contributor);
			contributorsAndAlias.addAll(contributorsUtils.getAlias(contributor));
		}
		return contributors;
	}

}
