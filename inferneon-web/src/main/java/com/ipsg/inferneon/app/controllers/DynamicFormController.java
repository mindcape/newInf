package com.ipsg.inferneon.app.controllers;

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
	public FormInput getFormDefinition(){
		FormInput form = new FormInput();
		Algorithm algorithm = projectService.getAlgorithmByName("DecisionTree");
		form.setFormFields(algorithm.getFields());
		/*form.setAlgorithmId(algorithmId);
		form.setProjectId(projectId);*/
		return form;
	}
	
	@RequestMapping(value="/saveForm", method=RequestMethod.POST)
	@ResponseBody
	public void saveForm(@RequestBody FormInput formData) {
		System.out.println("In Request");
	}

}
