package com.retooling.pursalegg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PurchaseEggException extends Exception {

	public PurchaseEggException() {
		super();
	}
	
	public PurchaseEggException(String message) {
		super(message);
	}
	
}
