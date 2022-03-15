package analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import enums.FileImportanceMetric;
import enums.KnowledgeMetric;
import model.Commit;
import model.Contributor;
import model.File;
import utils.Constants;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.FileUtils;
import utils.RepositoryAnalyzer;

public class FileImportanceAwareTruckFactor extends Analyzer{
	
	private static Commit commit = RepositoryAnalyzer.getCurrentCommit();
	private static DoaUtils doaUtils = new DoaUtils(commit);
	private static DoeUtils doeUtils = new DoeUtils(commit);
	private static ContributorsUtils contributorsUtils = new ContributorsUtils();
	
	public static void main(String[] args) {
		AnalyzerDTO analyzerDTO = getFiles(args[0]);
		KnowledgeMetric metric = KnowledgeMetric.DOA;
		FileImportanceMetric fileImportanceMetric = FileImportanceMetric.SIZE;
		
		System.out.println("=========== File importance aware truckfactor "+metric.getName()+" and "+fileImportanceMetric.getName()+"===========");
		int tf = 0;
		List<Contributor> contributors = contributorsUtils.activeContributors(analyzerDTO.getProject());
		contributorsUtils.removeAlias(contributors);
		LinkedHashMap<File, Double> fileValue = FileUtils.filesValues(analyzerDTO.getProject(), 
				analyzerDTO.getAnalyzedFiles(), fileImportanceMetric);
		contributorsUtils.sortContributorsByMetric(contributors, fileValue, metric);
		Collections.sort(contributors, new Comparator<Contributor>() {
		    @Override
		    public int compare(Contributor c1, Contributor c2) {
		        return Double.compare(c2.getSumFileImportance(), c1.getSumFileImportance());
		    }
		});
		List<Contributor> removedContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverageFileImportance(contributors, fileValue, metric);
			if(covarage < 0.5) 
				break;
			removedContributors.add(contributors.get(0));
			contributors.remove(0);
			tf = tf+1;
		}
		System.out.println("Truck factor: "+tf);
		System.out.println("Top contributors");
		for(Contributor contributor: removedContributors) {
			System.out.println(contributor.getName() +" "+contributor.getEmail());
		}
	}
	
	private static double getCoverageFileImportance(List<Contributor> contributors, LinkedHashMap<File, Double> filesValues, KnowledgeMetric metric) {
		double sumImportance = 0.0;
		double sumImportanceCovarage = 0.0;
		for(Map.Entry<File, Double> fileValue: filesValues.entrySet()) {
			sumImportance = sumImportance + fileValue.getValue();
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = doaUtils.getMantainersByFile(fileValue.getKey(), Constants.thresholdMantainer);
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = doeUtils.getMantainersByFile(fileValue.getKey(), Constants.thresholdMantainer);
			}
			forMaintainers:for(Contributor expert: experts) {
				for(Contributor contributor: contributors) {
					if(expert.getId().equals(contributor.getId())) {
						sumImportanceCovarage = sumImportanceCovarage + fileValue.getValue();
						break forMaintainers;
					}
				}
			}
		}
		double coverage = sumImportanceCovarage/sumImportance;
		return coverage; 
	}

}
