package model;

public class DownloadProjectInfo {
	
	private String fullName;
	private String default_branch;
	private String cloneUrl;
	
	public DownloadProjectInfo(String fullName, String default_branch, String cloneUrl) {
		super();
		this.fullName = fullName;
		this.default_branch = default_branch;
		this.cloneUrl = cloneUrl;
	}
	public DownloadProjectInfo() {
		super();
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getDefault_branch() {
		return default_branch;
	}
	public void setDefault_branch(String default_branch) {
		this.default_branch = default_branch;
	}
	public String getCloneUrl() {
		return cloneUrl;
	}
	public void setCloneUrl(String cloneUrl) {
		this.cloneUrl = cloneUrl;
	}

}
