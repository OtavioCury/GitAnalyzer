package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import dao.AuthorDoaDAO;
import model.AuthorDOA;
import model.Commit;
import model.Contributor;
import model.File;

public class DoaUtils extends MetricsUtils{

	private AuthorDoaDAO authorDoaDAO = new AuthorDoaDAO(); 

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
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		Set<Contributor> contributors = contributor.getAlias();
		contributors.add(contributor);
		Set<File> files = getFilesRenames(file);
		int numberCommits = commitDAO.numberCommitsFileOthersAuthorsVersion(contributorAndAliasIds(contributor), fileAndRenamesIds(files), currentCommit);
		return numberCommits;
	}

	private int getDl(Contributor contributor, File file) {
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		Set<File> files = getFilesRenames(file);
		int numberCommits = commitDAO.numberCommitsFileAuthorsVersion(contributorAndAliasIds(contributor), fileAndRenamesIds(files), currentCommit);
		return numberCommits;
	}

	public List<Contributor> getMantainersByFile(File file){
		Commit currentCommit = RepositoryAnalyzer.getCurrentCommit();
		List<Contributor> mantainers = new ArrayList<Contributor>();
		List<AuthorDOA> doas = authorDoaDAO.findByFileVersion(file, currentCommit);
		if(doas != null && doas.size() > 0) {
			AuthorDOA maxDoa = doas.stream().max(Comparator.comparing(AuthorDOA::getDegreeOfAuthorship)).get();
			for(AuthorDOA doa: doas) {
				double normalizedDoa = doa.getDegreeOfAuthorship()/maxDoa.getDegreeOfAuthorship();
				if(normalizedDoa > Constants.normalizedThresholdMantainerDOA && 
						doa.getDegreeOfAuthorship() >= Constants.thresholdMantainerDOA) {
					mantainers.add(doa.getAuthorFile().getAuthor());
				}
			}
		}
		return mantainers;
	}

}
