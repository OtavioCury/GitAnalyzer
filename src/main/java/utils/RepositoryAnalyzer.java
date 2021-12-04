package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import dao.CommitDAO;
import model.Commit;

public class RepositoryAnalyzer {
	
	public static Git git;
	public static Repository repository;
	public static HashMap<String, List<DiffEntry>> diffsCommits;
	
	public static Commit getCurrentCommit() {
		CommitDAO commitDao = new CommitDAO();
		try {
			Iterable<RevCommit> commits = RepositoryAnalyzer.git.log().all().call();
			List<RevCommit> commitsList = new ArrayList<RevCommit>();
			commits.forEach(commitsList::add);
			RevCommit maxDateCommit = commitsList.get(0);
			for(RevCommit commit: commitsList) {
				if(commit.getAuthorIdent().getWhen().after(maxDateCommit.getAuthorIdent().getWhen())) {
					maxDateCommit = commit;
				}
			}
			String latestCommitHash = maxDateCommit.getName();
			Commit commit = commitDao.findById(latestCommitHash);
			return commit;
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
