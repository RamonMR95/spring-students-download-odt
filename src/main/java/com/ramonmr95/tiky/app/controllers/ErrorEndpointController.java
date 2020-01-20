package com.ramonmr95.tiky.app.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;

public class ErrorEndpointController implements ErrorController {
	
	@Override
	public String getErrorPath() {
		return "error/404";
	}

	
}
