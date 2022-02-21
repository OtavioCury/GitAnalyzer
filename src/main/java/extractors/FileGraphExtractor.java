package extractors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dao.FileDAO;
import dao.FileVersionDAO;
import model.Commit;
import model.File;
import model.FileVersion;
import model.Project;
import model.ReferenceSet;
import utils.Constants;
import utils.FileUtils;
import utils.RepositoryAnalyzer;

public class FileGraphExtractor {

	private Project project;
	private FileVersionDAO fileVersionDAO = new FileVersionDAO();
	private FileDAO fileDAO = new FileDAO();

	public FileGraphExtractor(Project project) {
		super();
		this.project = project;
	}

	public void runExtractor() {
		//javaFilesGraph();
		jhmXmlFilesGraph();
	}

	private void jhmXmlFilesGraph() {
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		try {
			HashMap<String, String> filesString = FileUtils.currentFilesWithContents();
			HashMap<String, File> filesMap = new HashMap<String, File>();
			for(Map.Entry<String, String> map: filesString.entrySet()) {
				File file = fileDAO.findByPath(map.getKey(), project);
				if(file != null) {
					file.setContent(map.getValue());
					filesMap.put(map.getKey(), file);
				}
			}
			List<File> filesJhm = new ArrayList<File>();
			List<File> filesJava = new ArrayList<File>();
			for(Map.Entry<String, File> map: filesMap.entrySet()) {
				File file = map.getValue();
				if(file.getExtension().equals("jhm.xml")) {
					filesJhm.add(file);
				}
				if(file.getExtension().equals("java")) {
					filesJava.add(file);
				}
			}
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				dbf.setValidating(false);
				dbf.setNamespaceAware(true);
				dbf.setFeature("http://xml.org/sax/features/namespaces", false);
				dbf.setFeature("http://xml.org/sax/features/validation", false);
				dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
				dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				DocumentBuilder db = dbf.newDocumentBuilder();
				for (File file : filesJhm) {
					FileVersion fileVersion = fileVersionDAO.findByFileVersion(file, currentVersion);
					if (fileVersion != null) {
						fileVersion.setFilesReferencesGraphOut(new ArrayList<File>());
						ByteArrayInputStream input = new ByteArrayInputStream(
								   file.getContent().getBytes("UTF-8"));
						Document doc = db.parse(input);
						doc.getDocumentElement().normalize();
						Element docElement = doc.getDocumentElement();
						NodeList nl = docElement.getChildNodes();
						for(int k = 0; k < nl.getLength(); k++){
							visitNode((Node) nl.item(k), file, filesJava, fileVersion);
						}
						removeDuplicateReferencesFileVersion(fileVersion);
						fileVersionDAO.merge(fileVersion);
					}
				}
				System.out.println();
			}catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void removeDuplicateReferencesFileVersion(FileVersion fileVersion) {
		List<File> files = new ArrayList<File>();
		for (File file : fileVersion.getFilesReferencesGraphOut()) {
			boolean contains = false;
			innerFor:for (File fileAux : files) {
				if (fileAux.getId().equals(file.getId())) {
					contains = true;
					break innerFor;
				}
			}
			if (contains == false) {
				files.add(file);
			}
		}
		fileVersion.getFilesReferencesGraphOut().clear();
		fileVersion.setFilesReferencesGraphOut(files);
	}

	private void visitNode(Node item, File file, List<File> javaFiles, FileVersion fileVersion) {
		if (item.getNodeValue() != null && item.getNodeValue().contains("<infoway:")) {
			System.out.println(item.getNodeValue());
		}
		if (item.hasChildNodes()) {
			if (item.getAttributes() != null && item.getAttributes().getNamedItem("class") != null) {
				String path = item.getAttributes().getNamedItem("class").getNodeValue();
				path = convertPathString(path);
				File reference = searchFileFromClassFullName(javaFiles, path);
				if (reference != null) {
					fileVersion.getFilesReferencesGraphOut().add(reference);
				}
			}
			if (item.getAttributes() != null && item.getAttributes().getNamedItem("type") != null) {
				String path = item.getAttributes().getNamedItem("type").getNodeValue();
				path = convertPathString(path);
				File reference = searchFileFromClassFullName(javaFiles, path);
				if (reference != null) {
					fileVersion.getFilesReferencesGraphOut().add(reference);
				}
			}
			NodeList nl = item.getChildNodes();
			for(int j=0;j<nl.getLength();j++)
				visitNode(nl.item(j), file, javaFiles, fileVersion);
		}
	}
	
	private String convertPathString(String path) {
		path = path.replace(Constants.DOT, Constants.FILE_SEPARATOR);
		path = path.concat(Constants.JAVA_EXTENSION);
		return path;
	}
	
	private void javaFilesGraph() {
		try {
			HashMap<String, String> filesString = FileUtils.currentFilesWithContents();
			HashMap<String, File> filesMap = new HashMap<String, File>();
			for(Map.Entry<String, String> map: filesString.entrySet()) {
				File file = fileDAO.findByPath(map.getKey(), project);
				if(file != null) {
					file.setContent(map.getValue());
					filesMap.put(map.getKey(), file);
				}
			}
			HashMap<String, File> filesJavaMap = new HashMap<String, File>();
			for(Map.Entry<String, File> map: filesMap.entrySet()) {
				File file = map.getValue();
				if(file.getExtension().equals("java")) {
					filesJavaMap.put(map.getKey(), map.getValue());
				}
			}
			List<File> filesJava = new ArrayList<File>();
			for(Map.Entry<String, File> map: filesJavaMap.entrySet()) {
				File file = map.getValue();
				file.setReferenceSet(new ReferenceSet());
				CompilationUnit unit = configure(file.getContent());
				visitAst(unit, file);
				filesJava.add(file);
			}

			createReferences(filesJava);
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void createReferences(List<File> files) {
		Commit currentVersion = RepositoryAnalyzer.getCurrentCommit();
		preProcessReferences(files);
		for (File file : files) {
			Set<File> filesReferences = new HashSet<File>();
			for (String s : file.getReferenceSet().getReferences()) {
				boolean contens = false;
				if(!s.contains(Constants.DOT)){ 
					insideFor:for (String r : file.getReferenceSet().getImports()) {
						if(s.equals(r.substring(r.lastIndexOf(Constants.DOT)+1, r.length()))){
							contens = true;
							break insideFor;
						}
					}
					if(!contens){
						String a = file.getPackageName().concat(Constants.FILE_SEPARATOR).concat(s).concat(Constants.JAVA_EXTENSION);
						File fileReference = searchFileFromClassFullName(files, a);
						if(fileReference != null){
							filesReferences.add(fileReference);
						}
					}
				}else{
					String a = s.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
					File fileReference = searchFileFromClassFullName(files, a);
					if(fileReference != null){
						filesReferences.add(fileReference);
					}
				}
			}

			for (String s: file.getReferenceSet().getImports()) {
				String r = s.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
				File fileReference = searchFileFromClassFullName(files, r);
				if(fileReference != null){
					filesReferences.add(fileReference);
				}
			}

			for (String s: file.getReferenceSet().getSimpleNames()){
				if(classSimpleNameVerifify(s, files)) {
					String r = file.getPackageName().replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.FILE_SEPARATOR).concat(s);
					for (String _import : file.getReferenceSet().getImports()) {
						if(_import.contains(s)){
							r = _import.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
						}
					}
					File fileReference = searchFileFromClassFullName(files, r);
					if(fileReference != null && file.getId() != fileReference.getId()){
						filesReferences.add(fileReference);
					}
				}
			}
			List<File> references = new ArrayList<File>();
			for (File fileAux : filesReferences) {
				references.add(fileAux);
			}
			FileVersion fileVersion = fileVersionDAO.findByFileVersion(file, currentVersion);
			if (fileVersion != null) {
				if (references.size() == 0) {
					System.out.println();
				}
				fileVersion.setFilesReferencesGraphOut(references);
				fileVersionDAO.merge(fileVersion);
			}else {
				System.out.println();
			}
		}
	}

	private boolean classSimpleNameVerifify(String simpleName, List<File> files) {
		if(simpleName.substring(0,1).equals(simpleName.substring(0,1).toUpperCase())) {
			for (File file: files) {
				if(simpleName.equals(FileUtils.returnFileName(file.getPath()))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isReferenceInPackagePattern(String reference, List<File> files) {
		if(reference.contains(Constants.DOT)){
			String s = reference.replace(Constants.DOT, Constants.FILE_SEPARATOR).concat(Constants.JAVA_EXTENSION);
			File file = searchFileFromClassFullName(files, s);
			if (file != null) {
				return true;
			}
		}
		return false;
	}

	private void preProcessReferences(List<File> files) {
		for (File file : files) {
			List<String> referencesToAdd = new ArrayList<String>();
			List<String> referencesToRemove = new ArrayList<String>();
			for (String r : file.getReferenceSet().getReferences()) {
				if(r.contains(Constants.DOT) || r.contains(Constants.LESS_THEN) || r.contains(Constants.OPEN_BRACKET)){
					if(!isReferenceInPackagePattern(r, files)){
						referencesToRemove.add(r);
						if(r.contains(Constants.DOT) || r.contains(Constants.LESS_THEN)){
							referencesToAdd.addAll(processDeclarationSimbol(r));
						}else if(r.contains(Constants.OPEN_BRACKET)){
							referencesToAdd.add(r.substring(0, r.indexOf(Constants.OPEN_BRACKET)));
						}
					}
				}
			}
			file.getReferenceSet().getReferences().addAll(referencesToAdd);
			file.getReferenceSet().getReferences().removeAll(referencesToRemove);
			processInnerClassReferences(file.getReferenceSet());
			removeDuplicatedReferences(file.getReferenceSet());
			refineClassesSystemReferences(file.getReferenceSet(), files);
		}
	}

	private void refineClassesSystemReferences(ReferenceSet referenceSet, List<File> files) {
		List<String> referencesToRemove = new ArrayList<String>();

		for (String s : referenceSet.getReferences()) {
			boolean contens = false;
			insideFor:for (File file : files) {
				String fileName = FileUtils.returnFileName(file.getPath()); 
				if(s.equals(fileName)){
					contens = true;
					break insideFor;
				}
			}
			if(contens == false)
				referencesToRemove.add(s);
		}

		referenceSet.getReferences().removeAll(referencesToRemove);
	}
	
	private void removeDuplicatedReferences(ReferenceSet referenceSet){
		Map<String,String> map = new HashMap<String, String>();

		for (String r : referenceSet.getReferences()) {
			map.put(r, r);
		}
		referenceSet.getReferences().clear();
		referenceSet.getReferences().addAll(map.values());
	}

	private void processInnerClassReferences(ReferenceSet referenceSet){
		String innerClass = new String();
		for (String s : referenceSet.getInnerClasses()) {
			innerClass = s.substring(s.indexOf(Constants.NEW) + Constants.NEW.length(), s.indexOf(Constants.OPEN_PARENTHESE));
			innerClass = innerClass.replace(Constants.WHITESPACE, Constants.EMPTY);
			referenceSet.getReferences().add(innerClass);
		}
	}

	private List<String> processDeclarationSimbol(String declaration) {
		List<String> splitedReferences = new ArrayList<String>();
		String[] splitedStr;

		splitedStr = declaration.replace(Constants.LESS_THEN, Constants.NUMBER_SIGN)
				.replace(Constants.DOT, Constants.NUMBER_SIGN)
				.replace(Constants.BIGGER_THEN, Constants.NUMBER_SIGN)
				.replace(Constants.COMMA, Constants.NUMBER_SIGN)
				.replace(Constants.WHITESPACE, Constants.NUMBER_SIGN)
				.replace(Constants.OPEN_BRACKET, Constants.NUMBER_SIGN)
				.replace(Constants.CLOSE_BRACKET, Constants.NUMBER_SIGN)
				.split(Constants.NUMBER_SIGN);

		for (int i = 0; i < splitedStr.length; i++) {
			if (!splitedStr[i].equals(Constants.EMPTY)) {
				splitedReferences.add(splitedStr[i]);
			}
		}
		return splitedReferences;
	}

	private File searchFileFromClassFullName(List<File> files, String s) {
		for (File f : files) {
			if (f.getPath().contains(s)) {
				return f;
			}
		}
		return null;
	}

	private void visitAst(CompilationUnit unit, File file) {
		unit.accept(new ASTVisitor() {
			/**
			 * Visiting imports of file
			 */
			@Override
			public boolean visit(ImportDeclaration node) {
				file.getReferenceSet().getImports().add(node.getName().toString());
				return true; 
			}

			@Override
			public boolean visit(SimpleName node) {
				file.getReferenceSet().getSimpleNames().add(node.toString());
				return true;
			}

			/**
			 * Visiting variables passed as parameters on methods
			 */
			@Override
			public boolean visit(SingleVariableDeclaration node) {
				file.getReferenceSet().getReferences().add(node.getType().toString());
				return true; 
			}

			/**
			 * Visiting declared on methods
			 */
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				file.getReferenceSet().getReferences().add(node.getType().toString());
				return true; 
			}

			/**
			 * Visiting classes attributes
			 */
			@Override
			public boolean visit(FieldDeclaration node) {
				String type = node.getType().toString();
				file.getReferenceSet().getReferences().add(type);
				return true; 
			}

			/**
			 * Visiting anonimous classes
			 */
			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				file.getReferenceSet().getInnerClasses().add(node.getParent().toString());
				return true; 
			}

			/**
			 * Visiting super classes
			 */
			@Override
			public boolean visit(CompilationUnit node) {
				for (Object type : unit.types()){
					TypeDeclaration typeDec = (TypeDeclaration) type;
					Type superClassType = typeDec.getSuperclassType();
					if(superClassType != null){
						String name = superClassType.toString();
						file.getReferenceSet().getReferences().add(name);
					}
				}
				return true; 
			}

			/**
			 * Visiting methods on classes
			 */
			@Override
			public boolean visit(MethodDeclaration node) {
				String returnTypeMethod = node.getReturnType2() != null ? node.getReturnType2().toString() : null;
				if(returnTypeMethod != null){
					file.getReferenceSet().getReferences().add(returnTypeMethod);
				}
				return true;
			}
		});
		if(unit.getPackage() != null) {
			file.setPackageName(unit.getPackage().getName().toString());
		}else{
			file.setPackageName(Constants.EMPTY);
		}
	}

	/**
	 * Configure compilation unit to generate ast
	 * @param content
	 * @return
	 */
	private CompilationUnit configure(String content){
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(content.toCharArray());
		return (CompilationUnit) parser.createAST(null); 
	}

}
