package analyzers.truckfactor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Repository;

import dao.ProjectDAO;
import extractors.FileExtractor;
import extractors.ProjectExtractor;
import model.Commit;
import model.Project;

public class TruckFactorAnalyzer {

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		ProjectDAO projectDao = new ProjectDAO();
		CommitExtractor commitExtractor = new CommitExtractor();
		String pathToDir = args[0];
		File dir = new File(pathToDir);
		for (File file: dir.listFiles()) {
			if (file.isDirectory()) {
				String projectPath = file.getAbsolutePath()+"/";
				ProjectExtractor projectExtractor = new ProjectExtractor();
				projectExtractor.run(projectPath);
				String projectName = projectExtractor.extractProjectName(projectPath);
				Project project = projectDao.findByName(projectName);
				Git git;
				Repository repository;
				git = Git.open(new File(projectPath));
				repository = git.getRepository();
				FileExtractor fileExtractor = new FileExtractor(project);
				List<model.File> files = fileExtractor.run(repository);
				List<Commit> commits = commitExtractor.getCommits(files, git, repository);
				System.out.println();
				project.setCommitsExtracted(true);
				projectDao.merge(project);
			}
		}
	}

}
