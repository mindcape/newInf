package com.ipsg.inferneon.app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipsg.inferneon.app.dto.FormData;
import com.ipsg.inferneon.app.dto.FormField;

@Controller
public class DynamicFormController {
	
	
	@RequestMapping(value="/loadForm", method=RequestMethod.GET)
	@ResponseBody
	public FormData getFormDefinition(){
		
		FormData form = new FormData();
		List<FormField> formFields = new ArrayList<>();
		FormField formField1 = new FormField("firstName", FormField.Type.TEXT, "First name", true);
		formFields.add(formField1);
		FormField formField2 = new FormField("color", FormField.Type.SELECT, "Favorite color", true);
		formField2.withOption("#FF0000", "Red");
		formFields.add(formField2);
		form.setFormFields(formFields);
		return form;
	}

}
