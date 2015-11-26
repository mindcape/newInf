package com.ipsg.inferneon.app.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ipsg.inferneon.app.model.BigFile;
import com.ipsg.inferneon.app.model.Project;
	    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "CET")

public class FileUploadDTO {
	 private Long id;

	    private Date dateCreated;
	    
	    private String fileName;

		public FileUploadDTO(Long id, Date dateCreated, String fileName) {
			this.id = id;
			this.dateCreated = dateCreated;
			this.fileName = fileName;
		}

		public FileUploadDTO() {
			// TODO Auto-generated constructor stub
		}
		 public static FileUploadDTO mapFromBigFileEntity(BigFile file) {
		        return new FileUploadDTO(file.getId(), file.getDateCreated(),file.getFileName());
		    }

		    public static List<FileUploadDTO> mapFromBigFilesEntities(List<BigFile> files) {
		        return files.stream().map((file) -> mapFromBigFileEntity(file)).collect(Collectors.toList());
		    }
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Date getDateCreated() {
			return dateCreated;
		}

		public void setDatecreated(Date dateCreated) {
			this.dateCreated = dateCreated;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

}
