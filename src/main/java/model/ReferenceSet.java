package model;

import java.util.ArrayList;
import java.util.List;

public class ReferenceSet {

	private List<String> imports;
	private List<String> references;
	private List<String> innerClasses;
	private List<String> simpleNames;

	/**
	 * 
	 */
	public ReferenceSet() {
		this.imports = new ArrayList<String>();
		this.references = new ArrayList<String>();
		this.innerClasses = new ArrayList<String>();
		this.simpleNames = new ArrayList<String>();
	}

	/**
	 * @return the imports
	 */
	public List<String> getImports() {
		return imports;
	}

	/**
	 * @param imports the imports to set
	 */
	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	/**
	 * @return the references
	 */
	public List<String> getReferences() {
		return references;
	}

	/**
	 * @param references the references to set
	 */
	public void setReferences(List<String> references) {
		this.references = references;
	}

	/**
	 * @return the InnerClasses
	 */
	public List<String> getInnerClasses() {
		return innerClasses;
	}

	/**
	 * @param innerClasses to InnerClasses to set
	 */
	public void setInnerClasses(List<String> innerClasses) {
		this.innerClasses = innerClasses;
	}

	/**
	 * @return the simpleNames
	 */
	public List<String> getSimpleNames() {
		return simpleNames;
	}

	/**
	 * @param simpleNames to simpleNames to set
	 */
	public void setSimpleNames(List<String> simpleNames) {
		this.simpleNames = simpleNames;
	}

}
