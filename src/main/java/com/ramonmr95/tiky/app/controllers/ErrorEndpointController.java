package com.ramonmr95.tiky.app.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;

/**
 * Clase que implementa la interfaz ErrorController que proporciona métodos para
 * poder controlar errores de acceso a endpoints inexistentes o a los cuales no
 * tenemos permisos de acceso, entre otros.
 * 
 * @author Ramón Moñino Rubio - Antonio Ruiz Marín 2º DAM
 *
 */
public class ErrorEndpointController implements ErrorController {

	/**
	 * Método que devuelve la vista que se mostrará en caso de acceso a un endpoint
	 * inexistente
	 */
	@Override
	public String getErrorPath() {
		return "error/404";
	}

}
