package com.ipsg.inferneon.app.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipsg.inferneon.app.dto.FormData;
import com.ipsg.inferneon.app.model.Algorithm;
import com.ipsg.inferneon.app.services.ProjectService;

@Controller
public class DynamicFormController {
	
	
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(value="/loadForm", method=RequestMethod.GET)
	@ResponseBody
	public FormData getFormDefinition(){
		FormData form = new FormData();
		Algorithm algorithm = projectService.getAlgorithmByName("DecisionTree");
		form.setFormFields(algorithm.getFields());		
		return form;
	}
	
	@RequestMapping(value="/saveForm", method=RequestMethod.POST)
	@ResponseBody
	public void saveForm(HttpServletRequest request) {
		System.out.println("In Request");
		
	}

}
