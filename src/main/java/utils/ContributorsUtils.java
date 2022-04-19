package utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import dao.ContributorDAO;
import enums.KnowledgeMetric;
import model.Contributor;
import model.File;

public class ContributorsUtils {

	private ContributorDAO contributorDAO = new ContributorDAO();
	private DoaUtils doaUtils = new DoaUtils();
	private DoeUtils doeUtils = new DoeUtils();

	public void setAlias(Contributor contributor){
		Set<Contributor> alias = new HashSet<Contributor>();
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
		contributor.setAlias(alias);
		aliasIhealth(contributor);
	}

	private void aliasIhealth(Contributor contributor) {
		if (contributor.getProject().getName().toUpperCase().contains("IHEALTH")) {
			if (contributor.getName().toUpperCase().contains("JARDIEL")) {
				List<Contributor> contributors = contributorDAO.findByNameProject("jardiel", contributor.getProject());
				contributors.addAll(contributorDAO.findByNameProject("Jardiel", contributor.getProject()));
				Set<Contributor> alias = new HashSet<Contributor>();
				for (Contributor contributorAux : contributors) {
					if (contributorAux.getId().equals(contributor.getId()) == false) {
						alias.add(contributorAux);
					}
				}
				contributor.setAlias(alias);
			}
		}
	}

	public void removeAlias(List<Contributor> contributors){
		List<Contributor> removed = new ArrayList<Contributor>();
		for(Contributor contributor: contributors) {
			if(removed.contains(contributor) == false) {
				Set<Contributor> aliases = contributor.getAlias();
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
				experts = doaUtils.getMantainersByFile(file);
			}else if(metric.equals(KnowledgeMetric.DOE)){
				experts = doeUtils.getMantainersByFile(file);
			}
			for (Contributor expert: experts) {
				forMaintainers:for (Contributor contributor : contributors) {
					Set<Contributor> contributorsAlias = contributor.getAlias();
					contributorsAlias.add(contributor);
					for (Contributor alias : contributorsAlias) {
						if (expert.getId().equals(alias.getId())) {
							contributor.setNumberFilesAuthor(contributor.getNumberFilesAuthor()+1);
							break forMaintainers;
						}
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
				experts = doaUtils.getMantainersByFile(fileValue.getKey());
			}else if(metric.equals(KnowledgeMetric.DOE)){
				experts = doeUtils.getMantainersByFile(fileValue.getKey());
			}
			for (Contributor expert: experts) {
				forMaintainers:for (Contributor contributor : contributors) {
					Set<Contributor> contributorsAlias = contributor.getAlias();
					contributorsAlias.add(contributor);
					for (Contributor alias : contributorsAlias) {
						if (expert.getId().equals(alias.getId())) {
							contributor.setSumFileImportance(contributor.getSumFileImportance()+fileValue.getValue());
							break forMaintainers;
						}
					}
				}
			}
		}
	}
}
