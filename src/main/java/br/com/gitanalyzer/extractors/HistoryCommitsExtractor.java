package br.com.gitanalyzer.extractors;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.gitanalyzer.main.dto.HashNumberYears;
import br.com.gitanalyzer.model.Commit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HistoryCommitsExtractor {

	public void commitsHashs(HashNumberYears form) {
		java.io.File dir = new java.io.File(form.getPath());
		for (java.io.File fileDir: dir.listFiles()) {
			if (fileDir.isDirectory()) {
				String projectPath = fileDir.getAbsolutePath()+"/";
				saveCommitsHashs(projectPath, form.getNumberYears());
			}
		}
	}

	public void saveCommitsHashs(String path, int numberYears) {
		CommitExtractor commitExtractor = new CommitExtractor();
		List<Commit> commits = commitExtractor.getCommitsDatesAndHashes(path);
		String[] hashes = new String[numberYears];
		int index = 0;
		Date date = commits.get(0).getDate();
		hashes[index] = commits.get(0).getExternalId();
		index++;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -1);
		date = calendar.getTime();
		for (Commit commit: commits) {
			if (commit.getDate().before(date)) {
				date = commit.getDate();
				hashes[index] = commit.getExternalId();
				calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.YEAR, -1);
				date = calendar.getTime();
				index++;
				if (index > hashes.length - 1) {
					break;
				}
			}
		}
		String fullPath = path+"commitsHistory.log";
		FileWriter writer = null;
		try {
			writer = new FileWriter(fullPath);
			PrintWriter printWriter = new PrintWriter(writer);
			for(String hash: hashes) {
				printWriter.println(hash);
			}
			printWriter.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}