package utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import dao.ContributorDAO;
import dao.ContributorVersionDAO;
import enums.KnowledgeMetric;
import model.Commit;
import model.Contributor;
import model.File;
import model.Project;

public class ContributorsUtils {

	private ContributorDAO contributorDAO = new ContributorDAO();

	public List<Contributor> getAlias(Contributor contributor){
		List<Contributor> alias = new ArrayList<Contributor>();
		List<Contributor> contributors = contributorDAO.findAll(Contributor.class);
		for(Contributor contributorAux: contributors) {
			if(contributorAux.getId().equals(contributor.getId()) == false) {
				if(contributorAux.getEmail().equals(contributor.getEmail())) {
					alias.add(contributorAux);
				}else{
					String nome = contributorAux.getName().toUpperCase();
					if(nome != null) {
						int distancia = StringUtils.getLevenshteinDistance(contributor.getName().toUpperCase(), nome);
						if (nome.equals(contributor.getName().toUpperCase()) || 
								(distancia/(double)contributor.getName().length() < 0.1)) {
							alias.add(contributorAux);
						}
					}
				}
			}
		}
		return alias;
	}

	public List<Contributor> activeContributors(Project project){
		ContributorDAO contributorDAO = new ContributorDAO();
		ContributorVersionDAO contributorVesionDAO = new ContributorVersionDAO();
		List<Contributor> contributors = contributorDAO.findByProject(project);
		List<Contributor> contributorsExcluded = new ArrayList<Contributor>();
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		for(Contributor contributor: contributors) {
			if(contributorVesionDAO.disabledContributorVersion(contributor, currentVersion)) {
				contributorsExcluded.add(contributor);
			}
		}
		contributors.removeAll(contributorsExcluded);
		return contributors;
	}

	public void removeAlias(List<Contributor> contributors){
		List<Contributor> removed = new ArrayList<Contributor>();
		for(Contributor contributor: contributors) {
			if(removed.contains(contributor) == false) {
				List<Contributor> aliases = getAlias(contributor);
				for(Contributor alias: aliases) {
					for(Contributor contributorAux: contributors) {
						if(contributorAux.getId().equals(contributor.getId()) == false
								&& alias.getId().equals(contributorAux.getId())) {
							removed.add(contributorAux);
						}
					}
				}
			}
		}
		contributors.removeAll(removed);
	}

	public void sortContributorsByMetric(List<Contributor> contributors, List<File> files, 
			KnowledgeMetric metric) {
		for (File file : files) {
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = DoaUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			}else if(metric.equals(KnowledgeMetric.DOE)){
				experts = DoeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			}
			for (Contributor expert: experts) {
				for (Contributor contributor: contributors) {
					if (expert.getId().equals(contributor.getId())) {
						contributor.setNumberFilesAuthor(contributor.getNumberFilesAuthor()+1);
					}
				}
			}
		}
	}

	public void sortContributorsByMetric(List<Contributor> contributors, 
			LinkedHashMap<File, Double> filesValues, KnowledgeMetric metric) {
		for(Map.Entry<File, Double> fileValue: filesValues.entrySet()) {
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = DoaUtils.getMantainersByFile(fileValue.getKey(), Constants.thresholdMantainer);
			}else if(metric.equals(KnowledgeMetric.DOE)){
				experts = DoeUtils.getMantainersByFile(fileValue.getKey(), Constants.thresholdMantainer);
			}
			for (Contributor expert: experts) {
				for (Contributor contributor: contributors) {
					if (expert.getId().equals(contributor.getId())) {
						contributor.setSumFileImportance(contributor.getSumFileImportance()+fileValue.getValue());
					}
				}
			}
		}
	}
}
