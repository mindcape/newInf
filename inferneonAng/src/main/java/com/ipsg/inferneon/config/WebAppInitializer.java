package com.ipsg.inferneon.config;


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




}


