package com.ipsg.inferneon.app.controllers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ipsg.inferneon.app.dto.FileUploadDTO;
import com.ipsg.inferneon.app.model.BigFile;
import com.ipsg.inferneon.app.services.FileService;
import com.ipsg.inferneon.app.services.ProjectService;



/**
 * REST service for File Upload - Allows to upload, delete, modify file uploads for a project.
 *
 */
@Controller
@RequestMapping("fileupload")
public class FileUploadController {
	
    Logger LOGGER = Logger.getLogger(FileUploadController.class);

    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private FileService fileService;
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public List<FileUploadDTO> loadFilesByProject(
            Principal principal,           
            @RequestParam(value = "pageNumber") Integer pageNumber,HttpSession session) {
    	Long  project_id = (Long)session.getAttribute("object");
        List<BigFile> result = fileService.findFiles(principal.getName(),project_id);
        System.out.println("result:"+result.toString());
        return result.stream()
                .map(FileUploadDTO::mapFromBigFileEntity)
                .collect(Collectors.toList());
    }
    
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value="/loadFileById", method = RequestMethod.GET)
    public FileUploadDTO findFileById( Principal principal,           
            @RequestParam(value = "fileId") Long fileId) {
    	BigFile result = fileService.findFileById(principal.getName(),fileId);
    	return FileUploadDTO.mapFromBigFileEntity(result);
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public List<BigFile> uploadFile(Principal principal, MultipartHttpServletRequest request, @RequestParam(value = "projectId") Long projectId ){
    	List<BigFile> files = new ArrayList<>();
    	Iterator<String> iterator = request.getFileNames();
    	while(iterator.hasNext()) {
	        MultipartFile multiFile = request.getFile(iterator.next());
	        System.out.println(multiFile.getName());
	        BigFile file = null;
	    	try {
				byte[] bytes = multiFile.getBytes();
				String fileName = "C:/temp/"+multiFile.getOriginalFilename();
				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
				file = new BigFile();
				file.setFileName(multiFile.getOriginalFilename());
				file.setFileLoc("C:/temp");
				files.add(file);
				bos.write(bytes);
				bos.close();
			} catch (IOException ioe) {
				LOGGER.error(ioe.getMessage(),ioe);
				ioe.printStackTrace();
			}
    	}
    	if(!files.isEmpty()) {
    		projectService.addFiles(principal.getName(),files,projectId); 
    	} else {
    		//Send message no files to upload
    	}
    	return files;
    }

}
