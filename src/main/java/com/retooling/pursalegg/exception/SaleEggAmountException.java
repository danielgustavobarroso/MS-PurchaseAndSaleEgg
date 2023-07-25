package com.retooling.pursalegg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.MULTI_STATUS)
public class SaleEggAmountException extends Exception {

	public SaleEggAmountException() {
		super();
	}
	
	public SaleEggAmountException(String message) {
		super(message);
	}
	
}
