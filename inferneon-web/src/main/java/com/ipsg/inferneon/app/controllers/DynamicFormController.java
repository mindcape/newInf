package com.ipsg.inferneon.app.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipsg.inferneon.app.dto.FormInput;
import com.ipsg.inferneon.app.model.Algorithm;
import com.ipsg.inferneon.app.services.ProjectService;

@Controller
public class DynamicFormController {
	
	
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(value="/loadForm", method=RequestMethod.GET)
	@ResponseBody
	public FormInput getFormDefinition(String algorithmName){
		FormInput form = new FormInput();
		//Algorithm algorithm = projectService.getAlgorithmByName("DecisionTree");
		Algorithm algorithm = projectService.getAlgorithmByName(algorithmName);
		form.setFormFields(algorithm.getFields());
		/*form.setAlgorithmId(algorithmId);
		form.setProjectId(projectId);*/
		return form;
	}
	
	@RequestMapping(value="/saveForm", method=RequestMethod.POST)
	@ResponseBody
	public void saveForm( Principal principal,@RequestBody FormInput formData) {
		formData.setAlgorithmId(1l);//TODO Remove this once it is sent from ui
		formData.setProjectId(1l);//TODO Remove this once it is sent from ui
		projectService.saveAndRunAnalysis(formData,principal.getName());		
	}
	
	@RequestMapping(value="/loadAllAlgorithms", method=RequestMethod.GET)
	@ResponseBody
	public List<Algorithm> getAllAlgorithms(){
		FormInput form = new FormInput();
		List<Algorithm> algorithmList = projectService.getAllAlgorithm();
		return algorithmList;
	}
	

}
