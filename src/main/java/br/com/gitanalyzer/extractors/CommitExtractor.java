package br.com.gitanalyzer.extractors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import br.com.gitanalyzer.enums.OperationType;
import br.com.gitanalyzer.model.Commit;
import br.com.gitanalyzer.model.CommitFile;
import br.com.gitanalyzer.model.Contributor;
import br.com.gitanalyzer.model.File;
import br.com.gitanalyzer.model.Project;
import br.com.gitanalyzer.utils.Constants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommitExtractor {

	public List<Commit> getCommitsDatesAndHashes(String projectPath){
		List<Commit> commits = new ArrayList<Commit>();
		try {
			FileInputStream fstream = new FileInputStream(projectPath+Constants.commitFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				try {
					String[] commitSplited = strLine.split(";");
					String idCommit = commitSplited[0];
					String time = commitSplited[3];
					Integer timeInt = Integer.parseInt(time);
					Instant instant = Instant.ofEpochSecond(timeInt);
					Date commitDate = Date.from(instant);
					Commit commit = new Commit(commitDate, idCommit);
					commits.add(commit);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			br.close();
			return commits;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public List<Commit> getCommits(String projectPath, Project project) {
		List<Commit> commits = new ArrayList<Commit>();
		try {
			FileInputStream fstream = new FileInputStream(projectPath+Constants.commitFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				try {
					String[] commitSplited = strLine.split(";");
					String idCommit = commitSplited[0];
					String authorName = commitSplited[1];
					String authorEmail = commitSplited[2];
					String time = commitSplited[3];
					Contributor contributor = new Contributor(authorName, authorEmail);
					Integer timeInt = Integer.parseInt(time);
					Instant instant = Instant.ofEpochSecond(timeInt);
					Date commitDate = Date.from(instant);
					Commit commit = new Commit(contributor, project, commitDate, idCommit);
					commits.add(commit);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			br.close();
			return commits;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}


	public void extractCommitsFileAndDiffsOfCommits(String projectPath, List<Commit> commits, List<File> files) {
		try {
			FileInputStream fstream = new FileInputStream(projectPath+Constants.commitFileFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				String[] splited = strLine.split(";");
				String id = splited[0];
				commitFor:for (Commit commit : commits) {
					if (id.equals(commit.getExternalId())) {
						commit.setNumberOfFilesTouched(commit.getNumberOfFilesTouched()+1);
						String operation = splited[1];
						String filePath = splited[3];
						File file = null;
						for (File fileCommitFile : files) {
							if (fileCommitFile.isFile(filePath)) {
								file = fileCommitFile;
								break;
							}
						}
						if(file != null) {
							CommitFile commitFile = new CommitFile(file, commit, 
									OperationType.getEnumByType(operation));
							commit.getCommitFiles().add(commitFile);
						}
						break commitFor;
					}
				}
			}
			br.close();
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		commits = commits.stream().filter(c -> c.getCommitFiles().size() != 0).collect(Collectors.toList());
		try {
			FileInputStream fstream = new FileInputStream(projectPath+Constants.diffFileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			Commit commitAnalyzed = null;
			whileFile:while ((strLine = br.readLine()) != null) {
				if (strLine.trim().isEmpty()) {
					continue whileFile;
				}
				String[] splited1 = strLine.split(" ");
				String string1 = splited1[0];
				if (string1.equals("commit")) {
					String idCommitString = splited1[1];
					for (Commit commit : commits) {
						if (idCommitString.equals(commit.getExternalId())) {
							commitAnalyzed = commit;
							continue whileFile;
						}
					}
					commitAnalyzed = null;
					continue whileFile;
				}
				if (commitAnalyzed != null) {
					String[] splited2 = strLine.split("\t");
					String path = splited2[2];
					if (path.contains("=>")) {
						String commonString1 = "";
						String commonString2 = "";
						if (path.contains("{") && path.contains("}")) {
							String[] commonString =  path.split("\\{");
							commonString1 = commonString[0];
							commonString = path.split("}");
							if (commonString.length > 1) {
								commonString2 = commonString[1];
							}
							String stringAux = path.substring(path.indexOf("{") + 1);
							path = stringAux.substring(0, stringAux.indexOf("}"));
						}

						String[] splited3 = path.split("=>");
						String path1 = splited3[0];
						path1 = path1.trim();
						String path2 = splited3[1];
						path2 = path2.trim();

						String file1 = commonString1+path1+commonString2;
						String file2 = commonString1+path2+commonString2;
						for (CommitFile commitFile : commitAnalyzed.getCommitFiles()) {
							if(commitFile.getFile().isFile(file1) || commitFile.getFile().isFile(file2)) {
								try {
									int linesAdded = Integer.parseInt(splited2[0]);
									commitFile.setAdds(linesAdded);
								} catch (Exception e) {
									log.error("Error reading lines added on file "+commitFile.getFile()+" on commit "+commitAnalyzed.getExternalId());
								}
								continue whileFile;
							}
						}
					}else {
						for (CommitFile commitFile : commitAnalyzed.getCommitFiles()) {
							if(commitFile.getFile().isFile(path)) {
								try {
									int linesAdded = Integer.parseInt(splited2[0]);
									commitFile.setAdds(linesAdded);
								} catch (Exception e) {
									log.error("Error reading lines added on file "+commitFile.getFile()+" on commit "+commitAnalyzed.getExternalId());
								}
								continue whileFile;
							}
						}
					}	
				}
			}
			br.close();
		}catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
