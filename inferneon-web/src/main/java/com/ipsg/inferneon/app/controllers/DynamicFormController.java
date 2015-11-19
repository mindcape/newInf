package com.ipsg.inferneon.app.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipsg.inferneon.app.dto.FieldValues;
import com.ipsg.inferneon.app.dto.FormData;
import com.ipsg.inferneon.app.dto.FormField;

@Controller
public class DynamicFormController {
	
	
	@RequestMapping(value="/loadForm", method=RequestMethod.GET)
	@ResponseBody
	public FormData getFormDefinition(){
		
		FormData form = new FormData();
		List<FormField> formFields = new ArrayList<>();
		
		FormField formTextField = new FormField("firstName", FormField.Type.TEXT, "First name", true);
		formFields.add(formTextField);
		
		FormField formSelectField = new FormField("color", FormField.Type.SELECT, "Select color", true);
		
		FieldValues selectVal1 = new FieldValues();
		selectVal1.setKey("red");
		selectVal1.setDisplayValue("RED");;
		formSelectField.getFieldValues().add(selectVal1);
		
		FieldValues selectVal2 = new FieldValues();
		selectVal2.setKey("green");
		selectVal2.setDisplayValue("GREEN");;
		formSelectField.getFieldValues().add(selectVal2);
		
		formFields.add(formSelectField);
		
		FormField formRadioField = new FormField("color", FormField.Type.RADIO, "Option color", true);
		
		FieldValues radioVal1 = new FieldValues();
		radioVal1.setKey("yellow");
		radioVal1.setDisplayValue("Yellow");
		formRadioField.getFieldValues().add(radioVal1);
		
		FieldValues radioVal2 = new FieldValues();
		radioVal2.setKey("BLUE");
		radioVal2.setDisplayValue("BLUE");
		formRadioField.getFieldValues().add(radioVal2);
		
		formFields.add(formRadioField);
		
		
		form.setFormFields(formFields);
		return form;
	}

}
