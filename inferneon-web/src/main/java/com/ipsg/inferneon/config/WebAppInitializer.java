package com.ipsg.inferneon.config;


import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.ipsg.inferneon.config.root.AppSecurityConfig;
import com.ipsg.inferneon.config.root.DevelopmentConfiguration;
import com.ipsg.inferneon.config.root.RootContextConfig;
import com.ipsg.inferneon.config.root.TestConfiguration;
import com.ipsg.inferneon.config.servlet.ServletContextConfig;

/**
 *
 * Replacement for most of the content of web.xml, sets up the root and the servlet context config.
 *
 */
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootContextConfig.class, DevelopmentConfiguration.class, TestConfiguration.class,
                AppSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {ServletContextConfig.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(getMultipartConfigElement());
    }
 
    private MultipartConfigElement getMultipartConfigElement(){
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
        return multipartConfigElement;
    }
     
    /*Set these variables for your project needs*/
     
    private static final String LOCATION = "C:/temp/";
 
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 1024;//1GB
     
    private static final long MAX_REQUEST_SIZE = 1024 * 1024 * 1024;//1GB
 
    private static final int FILE_SIZE_THRESHOLD = 0;




}


