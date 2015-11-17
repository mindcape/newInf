package com.ipsg.inferneon.app.controllers;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.ipsg.inferneon.app.dto.NewUserDTO;
import com.ipsg.inferneon.app.dto.UserInfoDTO;
import com.ipsg.inferneon.app.model.User;
import com.ipsg.inferneon.app.services.UserService;

import java.security.Principal;

/**
 *
 *  REST service for users.
 *
 */

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class);

    @Autowired
    UserService userService;

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.GET)
    public UserInfoDTO getUserInfo(Principal principal) {

        User user = userService.findUserByUsername(principal.getName());
        Long totalProjects = userService.findUserProjects(principal.getName());

        return user != null ? new UserInfoDTO(user.getUsername(), user.getMaxNoOfProjectsPerPage(), totalProjects) : null;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.PUT)
    public void updateUserMaxNoOfProjectsPerDay(Principal principal, @RequestBody Long newMaxNoOfProjects) {
        userService.updateUserMaxNoOfProjectsPerDay(principal.getName(), newMaxNoOfProjects);
    }


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(method = RequestMethod.POST)
    public void createUser(@RequestBody NewUserDTO user) {
        userService.createUser(user.getUsername(), user.getEmail(), user.getPlainTextPassword());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> errorHandler(Exception exc) {
        LOGGER.error(exc.getMessage(), exc);
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
