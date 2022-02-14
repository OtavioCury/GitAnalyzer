package extractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import dao.FileDAO;
import model.File;
import model.Project;
import model.ReferenceSet;
import utils.FileUtils;

public class FileGraphExtractor {
	
	private Project project;

	public FileGraphExtractor(Project project) {
		super();
		this.project = project;
	}
	
	public void runExtractor() {
		FileDAO fileDAO = new FileDAO();
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
			for(Map.Entry<String, File> map: filesJavaMap.entrySet()) {
				File file = map.getValue();
				file.setReferenceSet(new ReferenceSet());
				CompilationUnit unit = configure(file.getContent());
				visitAst(unit, file);
			}
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void visitAst(CompilationUnit unit, File file) {
		unit.accept(new ASTVisitor() {
			/** VISITA OS IMPORTS DA CLASSE**/
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
			
			/** VISITA AS VARIÁVEIS PASSADAS COMO PARÂMETRO EM MÉTODOS DA CLASSE, INCLUÍNDO O CONSTRUTOR **/
			@Override
			public boolean visit(SingleVariableDeclaration node) {
				file.getReferenceSet().getReferences().add(node.getType().toString());
				return true; 
			}
			
			/** VISITA AS VARIÁVEIS DECLARADAS EM MÉTODOS DA CLASSE, INCLUINDO O CONSTRUTOR **/
			@Override
			public boolean visit(VariableDeclarationStatement node) {
				file.getReferenceSet().getReferences().add(node.getType().toString());
				return true; 
			}
			
			/** VISITA OS ATRIBUTOS DA CLASSE**/
			@Override
			public boolean visit(FieldDeclaration node) {
				String type = node.getType().toString();
				file.getReferenceSet().getReferences().add(type);
				return true; 
			}
			
			/** VISITA AS CLASSES ANÔNIMAS**/
			@Override
			public boolean visit(AnonymousClassDeclaration node) {
				file.getReferenceSet().getInnerClasses().add(node.getParent().toString());
				return true; 
			}
			
			/** VISITA A SUPER CLASSE NA RELAÇÃO DE HERANÇA**/
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
			
			/** VISITA OS MÉTODOS DECLARADOS NA CLASSE **/
			@Override
			public boolean visit(MethodDeclaration node) {
				String returnTypeMethod = node.getReturnType2() != null ? node.getReturnType2().toString() : null;
				if(returnTypeMethod != null){
					file.getReferenceSet().getReferences().add(returnTypeMethod);
				}
				return true;
			}
		});
	}
	
	private CompilationUnit configure(String content){
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(content.toCharArray());
		return (CompilationUnit) parser.createAST(null); 
	}

}
