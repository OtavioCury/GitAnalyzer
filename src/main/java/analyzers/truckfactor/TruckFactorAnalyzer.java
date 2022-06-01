package analyzers.truckfactor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;

import com.opencsv.CSVWriter;

import enums.OperationType;
import extractors.FileExtractor;
import extractors.ProjectExtractor;
import model.AuthorFile;
import model.Commit;
import model.CommitFile;
import model.Contributor;
import model.Project;
import utils.Constants;
import utils.DoeUtils;

public class TruckFactorAnalyzer {

	private static String filePath = "/home/otavio/projetos/truck_factor.csv";

	private static List<String> invalidsProjects = new ArrayList<String>(Arrays.asList("/home/otavio/projetos/sass/", "/home/otavio/projetos/metrics/",
			"/home/otavio/projetos/ionic/", "/home/otavio/projetos/linux/", "/home/otavio/projetos/dropwizard/", "/home/otavio/projetos/cucumber/"));

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		DoeUtils doeUtils = new DoeUtils();
		TruckFactorAnalyzer truckFactorAnalyzer = new TruckFactorAnalyzer();
		CommitExtractor commitExtractor = new CommitExtractor();
		String pathToDir = args[0];
		File dir = new File(pathToDir);
		for (File fileDir: dir.listFiles()) {
			if (fileDir.isDirectory()) {
				String projectPath = fileDir.getAbsolutePath()+"/";
				if (invalidsProjects.contains(projectPath) == false) {
					TruckFactorVO truckFactorVO = new TruckFactorVO();
					ProjectExtractor projectExtractor = new ProjectExtractor();
					String projectName = projectExtractor.extractProjectName(projectPath);
					truckFactorVO.setProjectName(projectName);
					Project project = new Project(projectName);
					Git git;
					Repository repository;
					git = Git.open(new File(projectPath));
					repository = git.getRepository();
					FileExtractor fileExtractor = new FileExtractor(project);
					System.out.println("EXTRACTING DATA FROM "+projectPath);
					List<model.File> files = fileExtractor.extractFromFileList(projectPath, Constants.linguistFileName, Constants.clocFileName, repository);
					truckFactorVO.setNumberOfFiles(files.size());
					List<Commit> commits = commitExtractor.getCommits(files, git, repository);
					truckFactorVO.setNumberOfCommits(commits.size());
					List<Contributor> contributors = truckFactorAnalyzer.extractContributorFromCommits(commits);
					truckFactorVO.setNumberOfDevs(contributors.size());
					contributors = truckFactorAnalyzer.setAlias(contributors);
					truckFactorVO.setNumberOfDevsAlias(contributors.size());
					List<AuthorFile> authorFiles = new ArrayList<AuthorFile>();
					for(Contributor contributor: contributors) {
						for (model.File file : files) {
							boolean existsContributorFile = truckFactorAnalyzer.existsContributorFile(contributor, file, commits);
							if (existsContributorFile) {
								int adds = truckFactorAnalyzer.linesAddedContributorFile(contributor, file, commits);
								int firstAuthor = truckFactorAnalyzer.firstAuthorContributorFile(contributor, file, commits);
								int numDays = truckFactorAnalyzer.numDaysContributorFile(contributor, file, commits);
								int fileSize = file.getFileSize();
								double doe = doeUtils.getDOE(adds, firstAuthor, numDays, fileSize);
								authorFiles.add(new AuthorFile(contributor, file, doe));
							}
						}
					}
					truckFactorAnalyzer.setNumberAuthor(contributors, authorFiles, files);
					Collections.sort(contributors, new Comparator<Contributor>() {
						@Override
						public int compare(Contributor c1, Contributor c2) {
							return Integer.compare(c2.getNumberFilesAuthor(), c1.getNumberFilesAuthor());
						}
					});
					contributors.removeIf(contributor -> contributor.getNumberFilesAuthor() == 0);
					List<Contributor> topContributors = new ArrayList<Contributor>();
					int tf = 0;
					while(contributors.isEmpty() == false) {
						double covarage = truckFactorAnalyzer.getCoverage(authorFiles, contributors, files);
						if(covarage < 0.5) 
							break;
						topContributors.add(contributors.get(0));
						contributors.remove(0);
						tf = tf+1;
					}
					truckFactorVO.setTruckfactor(tf);
					String[] record = {String.valueOf(truckFactorVO.getNumberOfDevs()), String.valueOf(truckFactorVO.getNumberOfDevsAlias()), 
							String.valueOf(truckFactorVO.getNumberOfFiles()), String.valueOf(truckFactorVO.getNumberOfCommits()), 
							String.valueOf(truckFactorVO.getTruckfactor()), truckFactorVO.getProjectName()};
					try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
						writer.writeNext(record);
					}
				}
			}
		}
	}

	private double getCoverage(List<AuthorFile> authorsFiles, List<Contributor> contributors, 
			List<model.File> files) {
		int fileSize = files.size();
		int numberFilesCovarage = 0;
		forFiles:for(model.File file: files) {
			List<AuthorFile> authorsFilesAux = authorsFiles.stream().
					filter(authorFile -> authorFile.getFile().getPath().equals(file.getPath())).collect(Collectors.toList());
			if (authorsFilesAux != null && authorsFilesAux.size() > 0) {
				List<Contributor> maintainers = new ArrayList<Contributor>();
				AuthorFile max = authorsFilesAux.stream().max(Comparator.comparing(AuthorFile::getDoe)).get();
				for (AuthorFile authorFile : authorsFilesAux) {
					double normalized = authorFile.getDoe()/max.getDoe();
					if (normalized > 0.7) {
						maintainers.add(authorFile.getAuthor());
					}
				}
				if (maintainers.size() > 0) {
					for (Contributor contributor : contributors) {
						for (Contributor maintainer : maintainers) {
							if (maintainer.equals(contributor)) {
								numberFilesCovarage++;
								continue forFiles;
							}
						}
					}
				}
			}
		}
		double coverage = (double)numberFilesCovarage/(double)fileSize;
		return coverage; 
	}

	private void setNumberAuthor(List<Contributor> contributors, 
			List<AuthorFile> authorsFiles, List<model.File> files) {
		for (model.File file: files) {
			List<AuthorFile> authorsFilesAux = authorsFiles.stream().
					filter(authorFile -> authorFile.getFile().getPath().equals(file.getPath())).collect(Collectors.toList());
			if (authorsFilesAux != null && authorsFilesAux.size() > 0) {
				List<Contributor> maintainers = new ArrayList<Contributor>();
				AuthorFile max = authorsFilesAux.stream().max(Comparator.comparing(AuthorFile::getDoe)).get();
				for (AuthorFile authorFile : authorsFilesAux) {
					double normalized = authorFile.getDoe()/max.getDoe();
					if (normalized > 0.7) {
						maintainers.add(authorFile.getAuthor());
					}
				}
				for (Contributor maintainer : maintainers) {
					for (Contributor contributor: contributors) {
						if (maintainer.equals(contributor)) {
							contributor.setNumberFilesAuthor(contributor.getNumberFilesAuthor()+1);
						}
					}
				}
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
					break;
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
		List<Contributor> contributors = new ArrayList<Contributor>();
		contributors.add(contributor);
		contributors.addAll(contributor.getAlias());
		for (Commit commit : commits) {
			boolean present = false;
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
					break;
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
				if (dateLastCommit != null) {
					long diff = currentDate.getTime() - dateLastCommit.getTime();
					int diffDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
					return diffDays;
				}
			}
		}
		return 0;
	}

	private int firstAuthorContributorFile(Contributor contributor, model.File file, List<Commit> commits) {
		List<Contributor> contributors = new ArrayList<Contributor>();
		contributors.add(contributor);
		contributors.addAll(contributor.getAlias());
		for (Commit commit : commits) {
			boolean present = false;
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
					break;
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
		List<Contributor> contributors = new ArrayList<Contributor>();
		contributors.add(contributor);
		contributors.addAll(contributor.getAlias());
		for (Commit commit : commits) {
			boolean present = false;
			for (Contributor contributorAux : contributors) {
				if (contributorAux.equals(commit.getAuthor())) {
					present = true;
					break;
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
						if(contributorAux.equals(contributor) == false) {
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
