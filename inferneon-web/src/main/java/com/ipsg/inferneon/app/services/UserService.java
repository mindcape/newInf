package com.ipsg.inferneon.app.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipsg.inferneon.app.dao.UserRepository;
import com.ipsg.inferneon.app.model.User;

import static com.ipsg.inferneon.app.services.ValidationUtils.*;

import java.util.regex.Pattern;

/**
 *
 * Business service for User entity related operations
 *
 */
@Service
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class);


    private static final Pattern PASSWORD_REGEX = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}");

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    @Autowired
    private UserRepository userRepository;

    /**
     *
     * updates the maximum noOfProjects of a given user
     *
     * @param username - the currently logged in user
     * @param newMaxNoOfProjects - the new max daily noOfProjects for the user
     */
    @Transactional
    public void updateUserMaxNoOfProjectsPerDay(String username, Long newMaxNoOfProjects) {
        User user = userRepository.findUserByUsername(username);

        if (user != null) {
            user.setMaxNoOfProjectsPerPage(newMaxNoOfProjects);
        } else {
            LOGGER.info("User with username " + username + " could not have the max noOfProjects updated.");
        }
    }

    /**
     *
     * creates a new user in the database
     *
     * @param username - the username of the new user
     * @param email - the user email
     * @param password - the user plain text password
     */
    @Transactional
    public void createUser(String username, String email, String password) {

        assertNotBlank(username, "Username cannot be empty.");
        assertMinimumLength(username, 6, "Username must have at least 6 characters.");
        assertNotBlank(email, "Email cannot be empty.");
        assertMatches(email, EMAIL_REGEX, "Invalid email.");
        assertNotBlank(password, "Password cannot be empty.");
        assertMatches(password, PASSWORD_REGEX, "Password must have at least 6 characters, with 1 numeric and 1 uppercase character.");

        if (!userRepository.isUsernameAvailable(username)) {
            throw new IllegalArgumentException("The username is not available.");
        }

        User user = new User(username, new BCryptPasswordEncoder().encode(password), email, 2000L);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Transactional(readOnly = true)
    public Long findUserProjects(String username) {
        return userRepository.findUserProjects(username);
    }

}
