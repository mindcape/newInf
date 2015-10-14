package com.inferneon.model;

import org.springframework.web.multipart.MultipartFile;

public class FileUpload {
	private int fileupload_id;
	private String file_name;
	private int projectid;
	private byte[] file_data;
	private MultipartFile file;
	public FileUpload() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FileUpload(int fileupload_id, String file_name, int projectid,
			byte[] file_data) {
		super();
		this.fileupload_id = fileupload_id;
		this.file_name = file_name;
		this.projectid = projectid;
		this.file_data = file_data;
	}
	public int getFileupload_id() {
		return fileupload_id;
	}
	public void setFileupload_id(int fileupload_id) {
		this.fileupload_id = fileupload_id;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public int getProjectid() {
		return projectid;
	}
	public void setProjectid(int projectid) {
		this.projectid = projectid;
	}
	public byte[] getFile_data() {
		return file_data;
	}
	public void setFile_data(byte[] file_data) {
		this.file_data = file_data;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	

}
