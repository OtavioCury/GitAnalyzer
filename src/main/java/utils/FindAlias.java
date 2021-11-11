package utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import dao.ContributorDAO;
import model.Contributor;

public class FindAlias {
	
	public static List<Contributor> getAlias(Contributor contributor){
		List<Contributor> alias = new ArrayList<Contributor>();
		ContributorDAO contributorDAO = new ContributorDAO();
		List<Contributor> contributors = contributorDAO.findAll(Contributor.class);
		for(Contributor contributorAux: contributors) {
			if(contributorAux.getEmail().equals(contributor.getEmail()) == false) {
				String nome = contributorAux.getName();
				if(nome != null) {
					int distancia = StringUtils.getLevenshteinDistance(contributor.getName(), nome);
					if (nome.equals(contributor.getName()) || 
							(distancia/(double)contributor.getName().length() < 0.3)) {
						alias.add(contributorAux);
					}
				}
			}
		}
		return alias;
	}

}
