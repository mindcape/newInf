package com.ipsg.inferneon.app.services;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipsg.inferneon.app.dao.FileRepository;
import com.ipsg.inferneon.app.model.BigFile;
import com.ipsg.inferneon.app.model.Project;

@Service
public class FileService {
	@Resource
	private FileRepository fileRepository;

	@Transactional(readOnly = true)
	public List<BigFile> findFiles(String projectName,Long pageNumber) {

		List<BigFile> files = fileRepository.findFilesByProjectName(projectName,pageNumber);

		return files;
	}

	public BigFile findFileById(String projectName, Long fileId) {
		return fileRepository.findFileById(projectName, fileId);
	}
}