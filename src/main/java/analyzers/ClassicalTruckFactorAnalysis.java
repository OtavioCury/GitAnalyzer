package analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import enums.KnowledgeMetric;
import model.Commit;
import model.Contributor;
import model.File;
import utils.Constants;
import utils.ContributorsUtils;
import utils.DoaUtils;
import utils.DoeUtils;
import utils.RepositoryAnalyzer;

public class ClassicalTruckFactorAnalysis extends Analyzer{
	
	private static Commit commit = RepositoryAnalyzer.getCurrentCommit();
	private static DoaUtils doaUtils = new DoaUtils(commit);
	private static DoeUtils doeUtils = new DoeUtils(commit);
	private static ContributorsUtils contributorsUtils = new ContributorsUtils();
	
	public static void main(String[] args) {
		AnalyzerDTO analyzerDTO = getFiles(args[0]);
		KnowledgeMetric metric = KnowledgeMetric.DOA;
		
		System.out.println("=========== Analysis avelino's truckfactor "+metric.getName()+" ===========");
		int tf = 0;
		List<Contributor> contributors = contributorsUtils.activeContributors(analyzerDTO.getProject());
		contributorsUtils.removeAlias(contributors);
		contributorsUtils.sortContributorsByMetric(contributors, analyzerDTO.getAnalyzedFiles(), metric);
		Collections.sort(contributors, new Comparator<Contributor>() {
		    @Override
		    public int compare(Contributor c1, Contributor c2) {
		        return Integer.compare(c2.getNumberFilesAuthor(), c1.getNumberFilesAuthor());
		    }
		});
		List<Contributor> removedContributors = new ArrayList<Contributor>();
		while(contributors.isEmpty() == false) {
			double covarage = getCoverage(contributors, analyzerDTO.getAnalyzedFiles(), metric);
			if(covarage < 0.5) 
				break;
			removedContributors.add(contributors.get(0));
			contributors.remove(0);
			tf = tf+1;
		}
		System.out.println("Top contributors");
		for(Contributor contributor: removedContributors) {
			System.out.println(contributor.getName());
		}
	}
	
	private static double getCoverage(List<Contributor> contributors, List<File> files, KnowledgeMetric metric) {
		
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		for(File file: files) {
			List<Contributor> experts = null;
			if (metric.equals(KnowledgeMetric.DOA)) {
				experts = doaUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			}else if(metric.equals(KnowledgeMetric.DOE)) {
				experts = doeUtils.getMantainersByFile(file, Constants.thresholdMantainer);
			}
			forMaintainers:for(Contributor expert: experts) {
				for(Contributor contributor: contributors) {
					if(expert.getId().equals(contributor.getId())) {
						numberFilesCovarage++;
						break forMaintainers;
					}
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}
}
