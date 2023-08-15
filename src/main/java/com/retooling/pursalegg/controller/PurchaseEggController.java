package com.retooling.pursalegg.controller;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.retooling.pursalegg.entity.PurchaseEgg;
import com.retooling.pursalegg.exception.PurchaseEggLimitException;
import com.retooling.pursalegg.exception.PurchaseEggMoneyException;
import com.retooling.pursalegg.exception.PurchaseValidationErrorException;
import com.retooling.pursalegg.service.PurchaseEggService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PurchaseEggController {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseEggController.class);

	@Autowired
	PurchaseEggService service;

	//Obtener todas las compras de huevos
	@GetMapping("purchase-egg")
	public ResponseEntity<List<PurchaseEgg>> getAllPurchaseEggs() {
		logger.info("Controller - Calling method getAllPurchaseEggs...");
		return new ResponseEntity<>(service.getAllPurchaseEggs(), HttpStatus.OK);
	}

	//Guardar una compra de huevos
	@PostMapping("purchase-egg")
	public ResponseEntity<PurchaseEgg> generatePurchaseEgg(@Valid @RequestBody PurchaseEgg purchaseEgg,
			BindingResult bindingResult) throws PurchaseValidationErrorException, PurchaseEggLimitException,
			PurchaseEggMoneyException, ParseException {		
		logger.info("Controller - Calling method generatePurchaseEgg...");
		if (bindingResult.hasErrors()) {
			String message = new String();
			for(FieldError error : bindingResult.getFieldErrors()) {
				if (message.isEmpty()) {
					message = message + error.getField() + " : " + error.getDefaultMessage();
				} else {
					message = message + ", " + error.getField() + " : " + error.getDefaultMessage();
				}
			}
			throw new PurchaseValidationErrorException(message);
		}
		return new ResponseEntity<>(service.generatePurchaseEgg(purchaseEgg), HttpStatus.OK);
	}

}