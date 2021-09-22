package main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.xmlbeans.impl.common.Levenshtein;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import dao.AuthorDAO;
import dao.AuthorFileDAO;
import dao.CommitDAO;
import dao.CommitFileDAO;
import dao.FileDAO;
import dao.FileOtherPathDAO;
import dao.ProjectDAO;
import model.Author;
import model.AuthorFile;
import model.Commit;
import model.CommitFile;
import model.FileOtherPath;
import model.Project;
import utils.ConstantsProject;

public class Analyzer {

	static String fullPath = "/media/lost/e04b3034-2506-41c9-a1d4-e3d38fe04256/otavio/projetoIHealth/ihealth/ihealth/.git";
	static String projectName = "IHealth";
	static String filesFile = "/media/lost/e04b3034-2506-41c9-a1d4-e3d38fe04256/otavio/projetoIHealth/ihealth/ihealth/filelist.log";
	static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
	
	static List<String> invalidPaths = new ArrayList<String>();

	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		invalidPaths.add("/dev/null");
		ProjectDAO projectDao = new ProjectDAO();
		//List<String> arquivosAnalisados = new ArrayList<String>();
		Git git;
		Repository repository;
		System.out.println("====== Analisando projeto IHealth =======");
		Project project = projectDao.findByName(projectName);
		if(project == null) {
			project = new Project(projectName);
			projectDao.persist(project);
		}
		git = Git.open(new File(fullPath));
		repository = git.getRepository();
		List<model.File> files = getFiles(project);
		createDatabase(files, git, repository);
		System.out.println();
		//		while (!fila.isEmpty()) {
		//			ModeloOtavio modelo = fila.poll();
		//			Date dataUltima = null;
		//			Row row = sheet.createRow(rowNum++);
		//			int adds = -1;
		//			int dels = -1;
		//			int mods = -1;
		//			int conds = -1;
		//			int numDevs = -1;
		//			int montante = -1;
		//			int numCommits = -1;
		//			int diffDias = -1;
		//			int avgCommits = -1;
		//			int numeroArquivos = numeroOtherFile(commits, modelo.getEmail(), modelo.getNome());
		//			double doa = -1.0;
		//			boolean primeiroAutor = false;
		//			BlameTotal blameTotal = null;
		//			if (commits != null) {
		//				blameTotal = blameTotal(modelo.getNome(), modelo.getArquivo(), repository);
		//				dataUltima = lastModify(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				adds = somaAdd(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				dels = somaDel(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				mods = somaMod(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				conds = somaCond(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				montante = somaMontante(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				numCommits = numCommits(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				avgCommits = avgDaysCommits(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome(), numCommits);
		//				numDevs = numberModsDevelopers(commits, modelo.getEmail(), modelo.getArquivo(), dataUltima, modelo.getNome());
		//				primeiroAutor = primeiroAutor(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
		//				doa = calculaDoa(numCommits, numDevs, primeiroAutor);
		//
		//				row.createCell(0).setCellValue(modelo.getNome());
		//				row.createCell(1).setCellValue(modelo.getEmail());
		//				row.createCell(2).setCellValue(modelo.getArquivo());
		//				row.createCell(3).setCellValue(projeto);
		//				row.createCell(4).setCellValue(modelo.getFamiliaridade());
		//				row.createCell(5).setCellValue(formato.format(modelo.getData()));
		//				row.createCell(6).setCellValue(adds);
		//				row.createCell(7).setCellValue(dels);
		//				row.createCell(8).setCellValue(mods);
		//				row.createCell(9).setCellValue(conds);
		//				row.createCell(10).setCellValue(montante);
		//				if (dataUltima != null) { 
		//					String dataFormatada = formato.format(dataUltima);
		//					row.createCell(11).setCellValue(dataFormatada);
		//					long diff = modelo.getData().getTime() - dataUltima.getTime();
		//					diffDias = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		//				}else {
		//					row.createCell(11).setCellValue(dataUltima);
		//				}
		//				row.createCell(12).setCellValue(numCommits);
		//				row.createCell(13).setCellValue(diffDias);
		//				row.createCell(14).setCellValue(numDevs);
		//				if (blameTotal != null) {
		//					row.createCell(15).setCellValue(blameTotal.blame);
		//					row.createCell(16).setCellValue(blameTotal.total);
		//				}else {
		//					row.createCell(15).setCellValue(-1);
		//					row.createCell(16).setCellValue(-1);
		//				}
		//				if (primeiroAutor) {
		//					row.createCell(17).setCellValue(1);
		//				}else {
		//					row.createCell(17).setCellValue(0);
		//				}
		//				row.createCell(18).setCellValue(doa);
		//				if (modelo.getFamiliaridade() >= 3) {
		//					row.createCell(19).setCellValue(1);
		//				}else {
		//					row.createCell(19).setCellValue(0);
		//				}
		//				row.createCell(20).setCellValue(numeroArquivos);
		//				row.createCell(21).setCellValue(avgCommits);
		//
		//				if (arquivosAnalisados.contains(modelo.getArquivo()) == false) {
		//					arquivosAnalisados.add(modelo.getArquivo());
		//					outrosDesenvolvedores(modelo.getArquivo(), repository, commits, 
		//							sheet, rowNum, projeto, workbook, modelo.getEmail(), modelo.getNome());
		//				}
		//			}
		//			commits = null;
		//
		//			FileOutputStream fileOut;
		//			fileOut = new FileOutputStream(caminhoSaida);
		//			workbook.write(fileOut);
		//			fileOut.close();
		//		}
		//		try {
		//			workbook.close();
		//		} catch (IOException e) {
		//			e.printStackTrace();
		//		}
		//
		//		Instant finish = Instant.now();
		//		long minutos = Duration.between(start, finish).toMinutes();
		//		System.out.println("Quantidade de minutos: "+minutos);
	}

	private static List<model.File> getFiles(Project project) {
		FileDAO fileDao = new FileDAO();
		List<model.File> files = new ArrayList<model.File>();
		try{
			FileInputStream fstream = new FileInputStream(filesFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null){
				model.File file = fileDao.findByPath(strLine, project);
				if(file == null) {
					String extension = strLine.substring(strLine.lastIndexOf("/")+1);
					extension = extension.substring(extension.indexOf(".")+1);
					file = new model.File(strLine, project, extension);
					fileDao.persist(file);
				}
				files.add(file);
			}
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return files;
	}

	private List<String> getFilesProjects() {
		List<String> arquivos = new ArrayList<String>();
		try{
			FileInputStream fstream = new FileInputStream(filesFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null){
				arquivos.add(strLine);
			}
			fstream.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return arquivos;
	}

	//	private static void outrosDesenvolvedores(String arquivo, Repository repository, 
	//			List<Revision> commits, Sheet sheet, int rowNum, String projeto, 
	//			Workbook workbook, String email, String nome) throws GitAPIException {
	//		List<ModeloOtavio> modelos = listaDesenvolvedoresArquivo(arquivo, commits, email, nome);
	//		for (ModeloOtavio modelo: modelos) {
	//			Row row = sheet.createRow(rowNum++);
	//			Date dataUltima = null;
	//			int adds = -1;
	//			int dels = -1;
	//			int mods = -1;
	//			int conds = -1;
	//			int numDevs = -1;
	//			int montante = -1;
	//			int numCommits = -1;
	//			int diffDias = -1;
	//			int avgCommits = -1;
	//			int numeroArquivos = numeroOtherFile(commits, modelo.getEmail(), modelo.getNome());
	//			double doa = -1.0;
	//			boolean primeiroAutor = false;
	//			BlameTotal blameTotal = null;
	//			if (commits != null) {
	//				blameTotal = blameTotal(modelo.getNome(), modelo.getArquivo(), repository);
	//				dataUltima = lastModify(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				adds = somaAdd(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				dels = somaDel(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				mods = somaMod(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				conds = somaCond(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				montante = somaMontante(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				numCommits = numCommits(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				avgCommits = avgDaysCommits(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome(), numCommits);
	//				numDevs = numberModsDevelopers(commits, modelo.getEmail(), modelo.getArquivo(), dataUltima, modelo.getNome());
	//				primeiroAutor = primeiroAutor(commits, modelo.getEmail(), modelo.getArquivo(), modelo.getNome());
	//				doa = calculaDoa(numCommits, numDevs, primeiroAutor);
	//			}
	//			row.createCell(0).setCellValue(modelo.getNome());
	//			row.createCell(1).setCellValue(modelo.getEmail());
	//			row.createCell(2).setCellValue(modelo.getArquivo());
	//			row.createCell(3).setCellValue(projeto);
	//			row.createCell(4).setCellValue(0);
	//			row.createCell(5).setCellValue(-1);
	//			row.createCell(6).setCellValue(adds);
	//			row.createCell(7).setCellValue(dels);
	//			row.createCell(8).setCellValue(mods);
	//			row.createCell(9).setCellValue(conds);
	//			row.createCell(10).setCellValue(montante);
	//			if (dataUltima != null) { 
	//				String dataFormatada = formato.format(dataUltima);
	//				row.createCell(11).setCellValue(dataFormatada);
	//			}else {
	//				row.createCell(11).setCellValue(dataUltima);
	//			}
	//			row.createCell(12).setCellValue(numCommits);
	//			row.createCell(13).setCellValue(-1);
	//			row.createCell(14).setCellValue(numDevs);
	//			if (blameTotal != null) {
	//				row.createCell(15).setCellValue(blameTotal.blame);
	//				row.createCell(16).setCellValue(blameTotal.total);
	//			}else {
	//				row.createCell(15).setCellValue(-1);
	//				row.createCell(16).setCellValue(-1);
	//			}
	//			if (primeiroAutor) {
	//				row.createCell(17).setCellValue(1);
	//			}else {
	//				row.createCell(17).setCellValue(0);
	//			}
	//			row.createCell(18).setCellValue(doa);
	//			if (modelo.getFamiliaridade() >= 3) {
	//				row.createCell(19).setCellValue(1);
	//			}else {
	//				row.createCell(19).setCellValue(0);
	//			}
	//			row.createCell(20).setCellValue(numeroArquivos);
	//			row.createCell(21).setCellValue(avgCommits);
	//		}
	//
	//		try {
	//			FileOutputStream fileOut;
	//			fileOut = new FileOutputStream(caminhoSaida);
	//			workbook.write(fileOut);
	//			fileOut.close();
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	private static List<ModeloOtavio> listaDesenvolvedoresArquivo(String arquivo, 
	//			List<Revision> commits, String emailRemover, String nomeRemover) {
	//		List<String> desenvolvedores = new ArrayList<String>();
	//		List<String> desenvolvedoresAlias = new ArrayList<String>();
	//		desenvolvedores.add(emailRemover);
	//		desenvolvedoresAlias.addAll(emails(emailRemover, commits, nomeRemover));
	//		List<ModeloOtavio> modelos = new ArrayList<ModeloOtavio>();
	//		for (int i = 0; i < commits.size(); i++) {
	//			for (int j = 0; j < commits.get(i).getFiles().size(); j++) {
	//				if (commits.get(i).getFiles().get(j).getPath().equals(arquivo)) {
	//					String email = commits.get(i).getAuthor().getEmail();
	//					String nome = commits.get(i).getAuthor().getName();
	//					if(desenvolvedores.contains(email) == false &&
	//							desenvolvedoresAlias.contains(email) == false) {
	//						desenvolvedores.add(email);
	//						desenvolvedoresAlias.addAll(emails(email, commits, nome));
	//						ModeloOtavio modelo = new ModeloOtavio(email, nome, arquivo);
	//						modelos.add(modelo);
	//					}
	//				}
	//			}
	//		}
	//		return modelos;
	//	}
	//
	//	private static double calculaDoa(int numCommits, int numDevs, boolean primeiroAutor) {
	//		double primeiraParte = 3.293;
	//		double segundaParte;
	//		if (primeiroAutor) {
	//			segundaParte = 1.098;
	//		}else {
	//			segundaParte = 0.0;
	//		}
	//		double terceiraParte = 0.164 * numCommits;
	//		double quartaParte = 0.321 * Math.log(1+numDevs);
	//
	//		return (primeiraParte + segundaParte + terceiraParte - quartaParte);
	//	}
	//
	//	private static void emailsArquivo(List<Revision> commits) {
	//		for (Revision commit : commits) {
	//			for (CommitFile arquivo : commit.getFiles()) {
	//				if(arquivo.getPath().equals("src/Symfony/Bridge/PhpUnit/Legacy/CommandForV6.php")) {
	//					System.out.println(commit.getAuthor().getEmail());
	//					System.out.println(commit.getAuthor().getName());
	//					System.out.println(commit.getDate());
	//					System.out.println(commit.getExternalId());
	//					System.out.println();
	//				}
	//			}
	//		}
	//	}

	/**
	 * Returns the result of a git log --follow -- < path >
	 * @return
	 * @throws IOException
	 * @throws MissingObjectException
	 * @throws GitAPIException
	 */
	public static ArrayList<RevCommit> call(Git git, Repository repository, String path) throws IOException, MissingObjectException, GitAPIException {
		ArrayList<RevCommit> commits = new ArrayList<RevCommit>();
		git = new Git(repository);
		RevCommit start = null;
		do {
			Iterable<RevCommit> log = git.log().addPath(path).call();
			for (RevCommit commit : log) {
				if (commits.contains(commit)) {
					start = null;
				} else {
					start = commit;
					commits.add(commit);
				}
			}
			if (start == null) return commits;
		}
		while ((path = getRenamedPath(start, git, repository, path)) != null);

		return commits;
	}

	/**
	 * Checks for renames in history of a certain file. Returns null, if no rename was found.
	 * Can take some seconds, especially if nothing is found... Here might be some tweaking necessary or the LogFollowCommand must be run in a thread.
	 * @param start
	 * @return String or null
	 * @throws IOException
	 * @throws MissingObjectException
	 * @throws GitAPIException
	 */
	private static String getRenamedPath(RevCommit start, Git git, Repository repository, String path) throws IOException, MissingObjectException, GitAPIException {
		Iterable<RevCommit> allCommitsLater = git.log().add(start).call();
		for (RevCommit commit : allCommitsLater) {

			TreeWalk tw = new TreeWalk(repository);
			tw.addTree(commit.getTree());
			tw.addTree(start.getTree());
			tw.setRecursive(true);
			RenameDetector rd = new RenameDetector(repository);
			rd.addAll(DiffEntry.scan(tw));
			List<DiffEntry> files = rd.compute();
			for (DiffEntry diffEntry : files) {
				if ((diffEntry.getChangeType() == DiffEntry.ChangeType.RENAME || diffEntry.getChangeType() == DiffEntry.ChangeType.COPY) && diffEntry.getNewPath().contains(path)) {
					//System.out.println("Encontrado: " + diffEntry.toString() + " return " + diffEntry.getOldPath());
					return diffEntry.getOldPath();
				}
			}
		}
		return null;
	}

	private static void createDatabase(List<model.File> files, Git git, Repository repository) throws NoHeadException, 
	GitAPIException, AmbiguousObjectException, IncorrectObjectTypeException, IOException{
		HashMap<String, List<DiffEntry>> diffsCommits = new HashMap<String, List<DiffEntry>>();
		AuthorDAO authorDao = new AuthorDAO();
		CommitDAO commitDao = new CommitDAO();
		CommitFileDAO commitFileDao = new CommitFileDAO();
		AuthorFileDAO authorFileDao = new AuthorFileDAO();
		FileDAO fileDao = new FileDAO();
		FileOtherPathDAO fileOtherPathDAO = new FileOtherPathDAO();
		List<Commit> commits = new ArrayList<Commit>();
		HashMap<String, BlameResult> blameResults = new HashMap<String, BlameResult>();
		for (model.File file : files) {
			List<String> paths = new ArrayList<String>();
			paths.add(file.getPath());
			List<RevCommit> log = call(git, repository, file.getPath());
			for (RevCommit jgitCommit: log) { //analisa cada commit
				String authorName = null, email = null;
				if (jgitCommit.getAuthorIdent() != null) {
					if (jgitCommit.getAuthorIdent().getEmailAddress() != null) {
						email = jgitCommit.getAuthorIdent().getEmailAddress();
					}else {
						email = jgitCommit.getCommitterIdent().getEmailAddress();
					}
					if (jgitCommit.getAuthorIdent().getName() != null) {
						authorName = jgitCommit.getAuthorIdent().getName();
					}else {
						authorName = jgitCommit.getCommitterIdent().getName();
					}
				}else {
					email = jgitCommit.getCommitterIdent().getEmailAddress();
					authorName = jgitCommit.getCommitterIdent().getName();
				}
				List<DiffEntry> diffsForTheCommit = null;
				if(diffsCommits.containsKey(jgitCommit.getName())) {
					diffsForTheCommit = diffsCommits.get(jgitCommit.getName());
				}else {
					diffsForTheCommit = diffsForTheCommit(repository, jgitCommit); //obtém as diffs do commit
					diffsCommits.put(jgitCommit.getName(), diffsForTheCommit);
				}
				Author author = authorDao.findByNameEmail(authorName, email);
				if(author == null) {
					author = new Author(authorName, email);
					authorDao.persist(author);
				}
				Commit commit = commitDao.findById(jgitCommit.getName());
				if(commit == null) {
					commit = new Commit(author, jgitCommit.getAuthorIdent().getWhen(), jgitCommit.getName(), diffsForTheCommit.size());
					commitDao.persist(commit);
					commits.add(commit);
				}
				for (DiffEntry diff : diffsForTheCommit) {
					String newPath = diff.getNewPath().toString();
					String oldPath = diff.getOldPath().toString();
					if(newPath.equals(oldPath) == false && 
							(paths.contains(newPath) == false || paths.contains(oldPath) == false)
							&& (paths.contains(newPath) == true || paths.contains(oldPath) == true) 
							&& (invalidPaths.contains(newPath) == false && invalidPaths.contains(oldPath) == false)) {
						if(paths.contains(newPath)) {
							paths.add(oldPath);
							FileOtherPath fileOtherPath = fileOtherPathDAO.findByPathFileCommit(oldPath, file, commit);
							if(fileOtherPath == null) {
								fileOtherPath = new FileOtherPath(oldPath, file, commit);
								fileOtherPathDAO.persist(fileOtherPath);
							}
						}else {
							paths.add(newPath);
							FileOtherPath fileOtherPath = fileOtherPathDAO.findByPathFileCommit(newPath, file, commit);
							if(fileOtherPath == null) {
								fileOtherPath = new FileOtherPath(newPath, file, commit);
								fileOtherPathDAO.persist(fileOtherPath);
							}
						}
					}
					if((paths.contains(newPath) == true || paths.contains(oldPath) == true)) {
						CommitFile commitFile = commitFileDao.findByCommitFile(commit.getExternalId(), file.getPath());
						if(commitFile == null) {
							commitFile = new CommitFile();
							if(diff.getChangeType().name().equals(ConstantsProject.ADD)){
								commitFile.setOperation(enums.OperationType.ADD);
							}else if(diff.getChangeType().name().equals(ConstantsProject.DELETE)){
								commitFile.setOperation(enums.OperationType.DEL);
							}else if(diff.getChangeType().name().equals(ConstantsProject.MODIFY)){
								commitFile.setOperation(enums.OperationType.MOD);
							}else if(diff.getChangeType().name().equals(ConstantsProject.RENAME)) {
								commitFile.setOperation(enums.OperationType.REN);
							}else{
								continue;
							}

							commitFile.setFile(file);

							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							DiffFormatter diffFormatter = new DiffFormatter( stream );
							diffFormatter.setRepository(repository);
							diffFormatter.format(diff);

							String in = stream.toString();

							Map<String, Integer> modifications = analyze(in);
							commitFile.setAdds(modifications.get("adds"));
							commitFile.setMods(modifications.get("mods"));
							commitFile.setDels(modifications.get("dels"));
							commitFile.setAmount(commitFile.getAdds()+commitFile.getDels());
							commitFile.setCommit(commit);
							commitFileDao.persist(commitFile);

							diffFormatter.flush();
							diffFormatter.close();
						}
					}
				}
			}
			BlameCommand blameCommand = new BlameCommand(repository);
			blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
			blameCommand.setFilePath(file.getPath());
			BlameResult blameResult = blameCommand.call();
			if(blameResult == null) {
				System.out.println();
			}
			RawText rawText = blameResult.getResultContents();
			file.setNumberLines(rawText.size());
			fileDao.merge(file);
			blameResults.put(file.getPath(), blameResult);
		}
		List<Author> authors = authorDao.findAll(Author.class);
		for (Author author : authors) {
			for (model.File file : files) {
				CommitFile commitFile = commitFileDao.findByAuthorFileAdd(author, file);
				AuthorFile authorFile = authorFileDao.findByAuthorFile(author, file);
				if(authorFile == null) {
					authorFile = new AuthorFile();
					int blame = 0;
					BlameResult blameResult = blameResults.get(file.getPath());
					RawText rawText = blameResult.getResultContents();
					int length = rawText.size();
					for (int i = 0; i < length; i++) {
						PersonIdent autor = blameResult.getSourceAuthor(i);
						if (autor.getName().equals(author.getName())) {
							blame++;
						}
					}
					authorFile.setAuthor(author);
					authorFile.setFile(file);
					authorFile.setNumLines(blame);
					if(commitFile != null) {
						authorFile.setFirstAuthor(true);
					}else {
						authorFile.setFirstAuthor(false);
					}
					authorFileDao.persist(authorFile);
				}
			}
		}
	}

	private static List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException, AmbiguousObjectException, 
	IncorrectObjectTypeException { 
		AnyObjectId currentCommit = repo.resolve(commit.getName()); 
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null; 
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE); 
		df.setBinaryFileThreshold(2 * 1024); //2 MB MAX A FILE
		df.setRepository(repo); 
		df.setDiffComparator(RawTextComparator.DEFAULT); 
		df.setDetectRenames(true); 
		List<DiffEntry> diffs = null; 
		if (parentCommit == null) { 
			RevWalk rw = new RevWalk(repo); 
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree())); 
			rw.close(); 
		} else { 
			diffs = df.scan(parentCommit, currentCommit); 
		} 
		df.close();
		return diffs; 
	}

	private static Map<String, Integer> analyze(String fileDiff){
		Stack<String> additions = new Stack<String>();
		Stack<String> deletions = new Stack<String>();
		int adds = 0, mods = 0, dels = 0, conditions = 0;
		HashMap<String, Integer> modifications = new HashMap<String, Integer>();
		if(fileDiff !=null ){
			String[] lines = fileDiff.split("\n");

			for(int i = 0; i < lines.length; i++){
				if((i > 3) && (lines[i].length() > 0)){
					if((lines[i].charAt(0) == '+') && (lines[i].substring(1).trim().length() > 0)) {
						additions.push(lines[i].substring(1));
					}else if((lines[i].charAt(0) == '-') && (lines[i].substring(1).trim().length() > 0)) {
						deletions.push(lines[i].substring(1));
					}else if ((!additions.isEmpty()) || (!deletions.isEmpty())) {
						for (String temp : additions) {
							if (temp.trim().startsWith("if")) {
								conditions++;
							}
						}
						while((!additions.isEmpty()) || (!deletions.isEmpty())){
							if(additions.isEmpty()){
								deletions.pop();
								dels++;
							} else if(deletions.isEmpty()){
								additions.pop();
								adds++;
							} else {
								String add = additions.pop();
								String del = deletions.pop();
								if(isSimilar(add, del)){
									mods++;
								} else if(additions.size() > deletions.size()){
									deletions.push(del);
									adds++;
								} else {
									additions.push(add);
									dels++;
								}
							}
						}
					}
				}
			}
		}
		if (!additions.isEmpty()) {
			additions.pop();
			adds++;
		}
		if(!deletions.isEmpty()){
			deletions.pop();
			dels++;
		}
		modifications.put("adds", adds);
		modifications.put("mods", mods);
		modifications.put("dels", dels);
		modifications.put("conditions", conditions);
		return modifications;
	}

	private static boolean isSimilar(String string1, String string2){
		int result = Levenshtein.distance(string1, string2);
		if(((double)result/string1.length()) < 0.4)
			return true;
		return false;

	}
	//
	//	private static List<String> emails(String email, List<Revision> commits, String nome) {
	//		List<String> emails = new ArrayList<String>();
	//		for (Revision revision : commits) {
	//			if (revision.getAuthor().getEmail().equals(email) == false) {
	//				String revisionNome = revision.getAuthor().getName();
	//				if(revisionNome != null) {
	//					int distancia = StringUtils.getLevenshteinDistance(nome, revisionNome);
	//					if (revision.getAuthor().getName().equals(nome) || 
	//							(distancia/(double)nome.length() < 0.3)) {
	//						emails.add(revision.getAuthor().getEmail());
	//					}
	//				}
	//			}
	//		}
	//		return emails;
	//	}
	//
	//	private static int numCommits(List<Revision> revisions, String email, String filePath, String nome) {
	//		int numCommits = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						numCommits++;
	//					}
	//				}
	//			}
	//		}
	//		return numCommits;
	//	}
	//
	//	private static int avgDaysCommits(List<Revision> revisions, String email, String filePath, String nome,
	//			int numCommits) {
	//		if (numCommits == 1) {
	//			return 0;
	//		}else {
	//			Date primeiro = null;
	//			Date ultimo = null;
	//			List<String> emails = emails(email, revisions, nome);
	//			for (int i = 0; i < revisions.size(); i++) {
	//				if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//						emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//					for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//						if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//							if (primeiro == null) {
	//								primeiro = revisions.get(i).getDate();
	//							}else if(revisions.get(i).getDate().before(primeiro)) {
	//								primeiro = revisions.get(i).getDate();
	//							}
	//						}
	//					}
	//				}
	//			}
	//			for (int i = 0; i < revisions.size(); i++) {
	//				if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//						emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//					for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//						if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//							if (ultimo == null) {
	//								ultimo = revisions.get(i).getDate();
	//							}else if(revisions.get(i).getDate().after(ultimo)) {
	//								ultimo = revisions.get(i).getDate();
	//							}
	//						}
	//					}
	//				}
	//			}
	//			if (primeiro != null && ultimo != null) {
	//				long diffInMillies = Math.abs(ultimo.getTime() - primeiro.getTime());
	//				long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	//				return (int) (diff/(numCommits - 1));
	//			}else {
	//				return -1;
	//			}
	//		}
	//	}
	//
	//	private static Date lastModify(List<Revision> revisions, String email, String filePath, String nome) {
	//		Date data = null;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						if (data == null) {
	//							data = revisions.get(i).getDate();
	//						}else if(data.before(revisions.get(i).getDate())) {
	//							data = revisions.get(i).getDate();
	//						}
	//					}
	//				}
	//			}
	//		}
	//		return data;
	//	}
	//
	//	private static int somaAdd(List<Revision> revisions, String email, String filePath, String nome) {
	//		int somaAdd = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						somaAdd = somaAdd + revisions.get(i).getFiles().get(j).getLineAdd();
	//					}
	//				}
	//			}
	//		}
	//		return somaAdd;
	//	}
	//
	//		private static BlameTotal blameTotal(String nome, String filePath, Repository repository) throws GitAPIException {
	//			int blame = 0, total = 0;
	//			BlameCommand blameCommand = new BlameCommand(repository);
	//			blameCommand.setTextComparator(RawTextComparator.WS_IGNORE_ALL);
	//			blameCommand.setFilePath(filePath);
	//			BlameResult blameResult = blameCommand.call();
	//			if(blameResult == null) {
	//				System.out.println();
	//			}
	//			RawText rawText = blameResult.getResultContents();
	//			int length = rawText.size();
	//			for (int i = 0; i < length; i++) {
	//				PersonIdent autor = blameResult.getSourceAuthor(i);
	//				if (autor.getName().equals(nome)) {
	//					blame++;
	//				}
	//				total++;
	//			}
	//			ExtractorOtavio extractorOtavio = new ExtractorOtavio();
	//			BlameTotal blameTotal = extractorOtavio.new BlameTotal(blame, total);
	//			return blameTotal;
	//		}
	//
	//	private static int somaMontante(List<Revision> revisions, String email, String filePath, String nome) {
	//		int somaMontante = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						somaMontante = somaMontante + revisions.get(i).getFiles().get(j).getLinesNumber();
	//					}
	//				}
	//			}
	//		}
	//		return somaMontante;
	//	}
	//
	//	private static boolean primeiroAutor(List<Revision> revisions, String email, String filePath, String nome) {
	//		boolean primeiroAutor = false;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath) &&
	//							revisions.get(i).getFiles().get(j).getOperationType().equals(OperationType.ADD)) {
	//						primeiroAutor = true;
	//					}
	//				}
	//			}
	//		}
	//		return primeiroAutor;
	//	}
	//
	//	private static int somaDel(List<Revision> revisions, String email, String filePath, String nome) {
	//		int somaDel = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						somaDel = somaDel + revisions.get(i).getFiles().get(j).getLineDel();
	//					}
	//				}
	//			}
	//		}
	//		return somaDel;
	//	}
	//
	//	private static int somaMod(List<Revision> revisions, String email, String filePath, String nome) {
	//		int somaMod = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						somaMod = somaMod + revisions.get(i).getFiles().get(j).getLineMod();
	//					}
	//				}
	//			}
	//		}
	//		return somaMod;
	//	}
	//
	//	private static int somaCond(List<Revision> revisions, String email, String filePath, String nome) {
	//		int somaCond = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//						somaCond = somaCond + revisions.get(i).getFiles().get(j).getLineCondition();
	//					}
	//				}
	//			}
	//		}
	//		return somaCond;
	//	}
	//
	//	private static int numeroOtherFile(List<Revision> revisions, String email, String nome) {
	//		List<String> arquivos = new ArrayList<String>();
	//		List<String> emails = emails(email, revisions, nome);
	//		for (int i = 0; i < revisions.size(); i++) {
	//			if (revisions.get(i).getAuthor().getEmail().equals(email) || 
	//					emails.contains(revisions.get(i).getAuthor().getEmail())) {
	//				for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//					if (arquivos.contains(revisions.get(i).getFiles().get(j).getPath()) == false) {
	//						arquivos.add(revisions.get(i).getFiles().get(j).getPath());
	//					}
	//				}
	//			}
	//		}
	//		return arquivos.size();
	//	}
	//
	//	private static int numberModsDevelopers(List<Revision> revisions, String email, String filePath, Date last, String nome) {
	//		int numMod = 0;
	//		List<String> emails = emails(email, revisions, nome);
	//		if(last != null) {
	//			for (int i = 0; i < revisions.size(); i++) {
	//				if (revisions.get(i).getDate().after(last) && 
	//						!revisions.get(i).getAuthor().getEmail().equals(email) && 
	//						emails.contains(revisions.get(i).getAuthor().getEmail()) == false) {
	//					for (int j = 0; j < revisions.get(i).getFiles().size(); j++) {
	//						if (revisions.get(i).getFiles().get(j).getPath().equals(filePath)) {
	//							numMod++;
	//						}
	//					}
	//				}
	//			}
	//		}else {
	//			System.out.println("Problema: "+email+", "+filePath);
	//		}
	//		return numMod;
	//	}

	class BlameTotal{
		private int blame;
		private int total;

		public BlameTotal(int blame, int total) {
			this.blame = blame;
			this.total = total;
		}
	}

}
