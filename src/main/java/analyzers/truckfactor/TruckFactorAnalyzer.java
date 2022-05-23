package analyzers.truckfactor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;

import dao.ProjectDAO;
import enums.OperationType;
import extractors.FileExtractor;
import extractors.ProjectExtractor;
import model.Commit;
import model.CommitFile;
import model.Contributor;
import model.Project;
import utils.Constants;
import utils.DoeUtils;

public class TruckFactorAnalyzer {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		DoeUtils doeUtils = new DoeUtils();
		TruckFactorAnalyzer truckFactorAnalyzer = new TruckFactorAnalyzer();
		CommitExtractor commitExtractor = new CommitExtractor();
		String pathToDir = args[0];
		File dir = new File(pathToDir);
		for (File fileDir: dir.listFiles()) {
			if (fileDir.isDirectory()) {
				String projectPath = fileDir.getAbsolutePath()+"/";
				ProjectExtractor projectExtractor = new ProjectExtractor();
				String projectName = projectExtractor.extractProjectName(projectPath);
				Project project = new Project(projectName);
				Git git;
				Repository repository;
				git = Git.open(new File(projectPath));
				repository = git.getRepository();
				FileExtractor fileExtractor = new FileExtractor(project);
				System.out.println("EXTRACTING DATA FROM "+projectPath);
				List<model.File> files = fileExtractor.extractFromFileList(projectPath, Constants.linguistFileName, Constants.clocFileName, repository);
				List<Commit> commits = commitExtractor.getCommits(files, git, repository);
				System.out.println();
//				List<Contributor> contributors = truckFactorAnalyzer.extractContributorFromCommits(commits);
//				contributors = truckFactorAnalyzer.setAlias(contributors);
//				for(Contributor contributor: contributors) {
//					for (model.File file : files) {
//						boolean existsContributorFile = truckFactorAnalyzer.existsContributorFile(contributor, file, commits);
//						if (existsContributorFile) {
//							int adds = truckFactorAnalyzer.linesAddedContributorFile(contributor, file, commits);
//							int firstAuthor = truckFactorAnalyzer.firstAuthorContributorFile(contributor, file, commits);
//							int numDays = truckFactorAnalyzer.numDaysContributorFile(contributor, file, commits);
//							int fileSize = file.getFileSize();
//							double doe = doeUtils.getDOE(adds, firstAuthor, numDays, fileSize);
//						}
//					}
//				}
//				System.out.println("Nº FILES: "+files.size()+" Nº COMMITS: "+commits.size()+" Nº CONTRIBUTORS: "+contributors.size());
//				project.setCommitsExtracted(true);
//				projectDao.merge(project);
			}
		}
	}

	private boolean existsContributorFile(Contributor contributor, model.File file, List<Commit> commits) {
		for (Commit commit : commits) {
			boolean present = false;
			List<Contributor> contributors = new ArrayList<Contributor>();
			contributors.add(contributor);
			contributors.addAll(contributor.getAlias());
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
				}
			}
			if (present == true) {
				for (CommitFile commitFile: commit.getCommitFiles()) {
					if (commitFile.getFile().getPath().equals(file.getPath())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private int numDaysContributorFile(Contributor contributor, model.File file, List<Commit> commits) {
		Date currentDate = commits.get(0).getDate();
		for (Commit commit : commits) {
			boolean present = false;
			List<Contributor> contributors = new ArrayList<Contributor>();
			contributors.add(contributor);
			contributors.addAll(contributor.getAlias());
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
				}
			}
			if (present == true) {
				Date dateLastCommit = null;
				for (CommitFile commitFile: commit.getCommitFiles()) {
					if (commitFile.getFile().getPath().equals(file.getPath())) {
						dateLastCommit = commit.getDate();
						break;
					}
				}
				long diff = currentDate.getTime() - dateLastCommit.getTime();
				int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				return diffDays;
			}
		}
		return 0;
	}

	private int firstAuthorContributorFile(Contributor contributor, model.File file, List<Commit> commits) {
		for (Commit commit : commits) {
			boolean present = false;
			List<Contributor> contributors = new ArrayList<Contributor>();
			contributors.add(contributor);
			contributors.addAll(contributor.getAlias());
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
				}
			}
			if (present == true) {
				for (CommitFile commitFile: commit.getCommitFiles()) {
					if (commitFile.getFile().getPath().equals(file.getPath()) && commitFile.getOperation().equals(OperationType.ADD)) {
						return 1;
					}
				}
			}
		}
		return 0;
	}

	private int linesAddedContributorFile(Contributor contributor, 
			model.File file, List<Commit> commits) {
		int adds = 0;
		for (Commit commit : commits) {
			boolean present = false;
			List<Contributor> contributors = new ArrayList<Contributor>();
			contributors.add(contributor);
			contributors.addAll(contributor.getAlias());
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
				}
			}
			if (present == true) {
				for (CommitFile commitFile: commit.getCommitFiles()) {
					if (commitFile.getFile().getPath().equals(file.getPath())) {
						adds = commitFile.getAdds() + adds;
					}
				}
			}
		}
		return adds;
	}

	private List<Contributor> extractContributorFromCommits(List<Commit> commits){
		List<Contributor> contributors = new ArrayList<Contributor>();
		for (Commit commit : commits) {
			Contributor contributor = commit.getAuthor();
			boolean present = false;
			for (Contributor contributor2 : contributors) {
				if (contributor2.equals(contributor)) {
					present = true;
				}
			}
			if (present == false) {
				contributors.add(contributor);
			}
		}
		return contributors;
	}

	public List<Contributor> setAlias(List<Contributor> contributors){
		List<Contributor> contributorsAliases = new ArrayList<Contributor>();
		for (Contributor contributor : contributors) {
			boolean present = false;
			for (Contributor contributorAlias : contributorsAliases) {
				List<Contributor> contributorsAliasesAux = new ArrayList<Contributor>();
				contributorsAliasesAux.add(contributorAlias);
				contributorsAliasesAux.addAll(contributorAlias.getAlias());
				for (Contributor contributorAliasAux : contributorsAliasesAux) {
					if (contributor.equals(contributorAliasAux)) {
						present = true;
					}
				}
			}
			if (present == false) {
				Set<Contributor> alias = new HashSet<Contributor>();
				for(Contributor contributorAux: contributors) {
					if (contributorAux.equals(contributor) == false) {
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
				}
				contributor.setAlias(alias);
				contributorsAliases.add(contributor);
			}
		}
		return contributorsAliases;
	}
}
