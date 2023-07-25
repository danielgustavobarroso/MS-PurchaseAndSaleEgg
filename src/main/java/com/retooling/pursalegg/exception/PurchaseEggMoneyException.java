package com.retooling.pursalegg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.MULTI_STATUS)
public class PurchaseEggMoneyException extends Exception {

	public PurchaseEggMoneyException() {
		super();
	}
	
	public PurchaseEggMoneyException(String message) {
		super(message);
	}
	
}
