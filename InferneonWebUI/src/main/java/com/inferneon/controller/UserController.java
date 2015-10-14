package com.inferneon.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.inferneon.dao.UserDataAccessObject;
import com.inferneon.dao.UserDataAccessObjectImplementation;
import com.inferneon.model.Activities;
import com.inferneon.model.Attributes;
import com.inferneon.model.FileUpload;
import com.inferneon.model.Project;
import com.inferneon.model.UploadedFile;
import com.inferneon.model.User;
import com.inferneon.model.attributeNames;
import com.inferneon.model.attributeNominalValues;
import com.inferneon.validator.FileValidator;

@Controller
public class UserController {
	@Autowired
	FileValidator fileValidator;

	@RequestMapping(value = "/login.html", method = RequestMethod.GET)
	public String setupForm(Model model, HttpServletRequest request) {
		User users = new User();
		model.addAttribute("userlogin", users);
		return "index";

	}

	@RequestMapping(value = "/signup.html", method = RequestMethod.GET)
	public String setupForms(Model model, HttpServletRequest request) {
		User users = new User();
		model.addAttribute("register", users);
		return "index";
	}

	@RequestMapping(value = "signup.html", method = RequestMethod.POST)
	public String NewRegistration(@ModelAttribute("register") User users,
			BindingResult result, SessionStatus status,
			HttpServletRequest request) {
		User user = new User();
		user.setFirstname(request.getParameter("Firstname"));
		user.setLastname(request.getParameter("Lastname"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("Email"));

		UserDataAccessObject userdataccessobject = new UserDataAccessObjectImplementation();
		userdataccessobject.NewRegistration(user);
		status.setComplete();
		return "redirect:/";
	}

	@RequestMapping(value = "login.html", method = RequestMethod.POST)
	public String login(@ModelAttribute("userlogin") User users,
			BindingResult result, HttpServletRequest request,
			HttpSession session) {
		users.setEmail(request.getParameter("email"));
		users.setPassword(request.getParameter("password"));
		UserDataAccessObject userdataccessobject = new UserDataAccessObjectImplementation();
		int userId = userdataccessobject.login(users);
		String userResp = null;
		if (userId != 0) {
			session.setAttribute("userId", userId);
			userResp = "getallprojects.html";
		} else {
			userResp = "error.html";
		}
		return "redirect:/" + userResp;
	}

	@RequestMapping(value = "addNewProjects", method = RequestMethod.POST)
	public String Newprojects(Project project, BindingResult result,
			SessionStatus status, HttpSession session,
			HttpServletRequest request) {
		int userId = (int) session.getAttribute("userId");
		project.setproject_name(request.getParameter("project_name"));
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd");
		project.setdate_created(dNow);
		project.setId(userId);
		UserDataAccessObject userdataccessobject = new UserDataAccessObjectImplementation();
		userdataccessobject.newProject(project);
		int projectId = project.getprojectid();
		Enumeration en = request.getParameterNames();
		int i = 1;
		while (en.hasMoreElements()) {
			Attributes attribute = new Attributes();
			attributeNames attributenames = new attributeNames();
			attributenames.setAttributeid(attribute.getAttributeid());
			Object objOri = en.nextElement();
			String param = (String) objOri;
			System.out.println("=======" + param);
			String delims = ",";
			String nominal_values = request.getParameter("attribute_value" + i);
			if (param.contains("numeric")) {

				attribute.setProjectid(projectId);
				attribute.setProject(project);
				userdataccessobject.newAttribute(attribute);
				attributenames.setAttributeid(attribute.getAttributeid());
				String value = request.getParameter(param);
				attributenames.setAttribute_name(value);
				String attribute_type = "attribute_numeric";
				attributenames.setAttribute_type(attribute_type);
				userdataccessobject.newAttributeName(attributenames);
			}

			else if (param.contains("nominal")) {
				attribute.setProjectid(projectId);
				attribute.setProject(project);
				userdataccessobject.newAttribute(attribute);
				attributenames.setAttributeid(attribute.getAttributeid());
				String value = request.getParameter(param);
				attributenames.setAttribute_name(value);
				String attribute_type = "attribute_nominal";
				attributenames.setAttribute_type(attribute_type);
				userdataccessobject.newAttributeName(attributenames);
				String[] tokens = nominal_values.split(delims);
				for (String Nominal_value : tokens) {

					attributeNominalValues attributeNominalValues = new attributeNominalValues();
					attributeNominalValues.setAttribute_name_id(attributenames
							.getAttribute_name_id());

					attributeNominalValues.setNominal_values(Nominal_value);
					userdataccessobject
							.newNominalValues(attributeNominalValues);

				}
				i++;

			}
		}
		return "redirect:/getallprojects.html";
	}

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(Model model) {
		return "error";
	}

	@RequestMapping(value = "/addNewProjects", method = RequestMethod.GET)
	public String addProjects(Model model) {
		return "addNewProjects";
	}

	@RequestMapping(value = "/getallprojects.html")
	public ModelAndView getProjects(HttpServletRequest hsr,
			HttpServletResponse hsr1, HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("showProjects");
		String out = "All User Details: ";
		try {
			int userId = (int) session.getAttribute("userId");
			UserDataAccessObject userDataAccessObject = new UserDataAccessObjectImplementation();

			List<Project> result = new ArrayList<Project>();
			result = userDataAccessObject.getProjects(userId);
			List<User> results = new ArrayList<User>();
			results = userDataAccessObject.getUsername(userId);
			hsr.setAttribute("projects", result);
			hsr.setAttribute("users", results);

		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("message", out);
		return mv;

	}

	@RequestMapping("/fileUploadForm.html")
	public ModelAndView getUploadForm(
			@ModelAttribute("uploadedFile") UploadedFile uploadedFile,
			BindingResult result) {
		return new ModelAndView("uploadForm");
	}

	@RequestMapping("/fileUpload.html")
	public ModelAndView fileUploaded(
			@ModelAttribute("uploadedFile") UploadedFile uploadedFile,
			BindingResult result, HttpSession session) {
		InputStream inputStream = null;
		OutputStream outputStream = null;

		MultipartFile file = uploadedFile.getFile();
		fileValidator.validate(uploadedFile, result);

		String fileName = file.getOriginalFilename();

		if (result.hasErrors()) {
			return new ModelAndView("uploadForm");
		}

		try {
			inputStream = file.getInputStream();
			int userId = (int) session.getAttribute("userId");
			int projectId = (int) session.getAttribute("projectId");

			System.out.println("userId of the File=" + userId);
			File file1 = new File("/opt//Inferneon/User"+userId+"/Project"+projectId+"");
			if (!file1.exists()) {
				System.out.println("dir before mk created");

				file1.mkdirs();
					System.out.println("dir created");

			}
			File file2 = new File("/opt//Inferneon/User" + userId	+"/Project" + projectId	+"/" + fileName);
			if (!file2.exists()) {
				file2.createNewFile();
			}
			outputStream = new FileOutputStream(file2);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ModelAndView("showFile", "message", fileName);
	}

	@RequestMapping(value = "/projectDetails.html", method = RequestMethod.GET)
	public ModelAndView getActivities(
			@ModelAttribute("activity") Activities activities,
			SessionStatus status, HttpServletRequest request,
			HttpServletResponse hsr, HttpSession session) {
		ModelAndView mv = new ModelAndView("projectDetails");
		String out = "All User Details: ";
		try {

			UserDataAccessObject userDataAccessObject = new UserDataAccessObjectImplementation();
			String project = request.getParameter("projectid");
			int projectsid = Integer.parseInt(project);
			Activities activities1 = new Activities();
			session.setAttribute("projectId", projectsid);

			activities1.setActivities_name("Lenior regression");
			Date date = new Date();
			activities1.setRun_date(date);
			activities1.setProjectid(projectsid);
			userDataAccessObject.newActivities(activities1);
			List<Activities> result = new ArrayList<Activities>();
			result = userDataAccessObject.getActivities(projectsid);
			request.setAttribute("activiti", result);

		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("message", out);
		return mv;
	}

}
