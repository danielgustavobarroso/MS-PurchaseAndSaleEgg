package com.retooling.pursalegg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.MULTI_STATUS)
public class PurchaseEggLimitException extends Exception {

	public PurchaseEggLimitException() {
		super();
	}
	
	public PurchaseEggLimitException(String message) {
		super(message);
	}
	
}
