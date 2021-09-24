package utils;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

public class RepositoryAnalyzer {
	
	public static Git git;
	public static Repository repository;
	public static HashMap<String, List<DiffEntry>> diffsCommits = new HashMap<String, List<DiffEntry>>();
	
}
