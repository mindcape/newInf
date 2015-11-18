package com.ipsg.inferneon.config.servlet;


import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * Spring MVC config for the servlet context in the application.
 *
 * The beans of this context are only visible inside the servlet context.
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.ipsg.inferneon.app.controllers")
public class ServletContextConfig extends WebMvcConfigurerAdapter {
	
	 private static final Logger LOGGER = Logger.getLogger(ServletContextConfig.class);


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
    
    @Bean(name="multipartResolver")
    public StandardServletMultipartResolver resolver(){
        return new StandardServletMultipartResolver();
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        // http
        HttpMessageConverter converter = new StringHttpMessageConverter();
        converters.add(converter);
        LOGGER.info("HttpMessageConverter added");

        // string
        converter = new FormHttpMessageConverter();
        converters.add(converter);
        LOGGER.info("FormHttpMessageConverter added");

        // json
        converter = new MappingJackson2HttpMessageConverter();
        converters.add(converter);
        LOGGER.info("MappingJackson2HttpMessageConverter added");

    }
    
}
