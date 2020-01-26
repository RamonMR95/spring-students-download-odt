package com.ramonmr95.tiky.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase que implementa WebMvcConfigurer que contiene una serie de métodos de
 * configuración de nuestra aplicación de Spring Mvc
 * 
 * @author Ramón Moñino Rubio - Antonio Ruiz Marín 2º DAM
 *
 */
@Configuration
public class MVCConfiguration implements WebMvcConfigurer {

	/**
	 * Método que le asigna un endpoint a una vista sin tener así que crear un
	 * controlador para dicha vista
	 */
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/index").setViewName("index");
		registry.addViewController("/").setViewName("index");
		registry.addViewController("").setViewName("index");
		registry.addViewController("/about").setViewName("about");
	}

}
