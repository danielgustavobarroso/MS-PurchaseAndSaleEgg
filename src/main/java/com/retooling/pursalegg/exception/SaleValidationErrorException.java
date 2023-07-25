package com.retooling.pursalegg.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SaleValidationErrorException extends Exception {

	public SaleValidationErrorException() {
		super();
	}
	
	public SaleValidationErrorException(String message) {
		super(message);
	}
	
}
