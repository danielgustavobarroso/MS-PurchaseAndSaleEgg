package com.retooling.pursalegg.controller;

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

import com.retooling.pursalegg.entity.SaleEgg;
import com.retooling.pursalegg.exception.SaleEggAmountException;
import com.retooling.pursalegg.exception.SaleEggException;
import com.retooling.pursalegg.exception.SaleValidationErrorException;
import com.retooling.pursalegg.service.SaleEggService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SaleEggController {

	private static final Logger logger = LoggerFactory.getLogger(SaleEggController.class);
	
	@Autowired
	SaleEggService service;

	//Obtener todas las ventas de huevos
	@GetMapping("sale-egg")
	public ResponseEntity<List<SaleEgg>> getAllSaleEggs() {
		logger.info("Controller - Calling method getAllSaleEggs...");
		return new ResponseEntity<>(service.getAllSaleEggs(), HttpStatus.OK);
	}

	//Guardar una venta de huevos
	@PostMapping("sale-egg")
	public ResponseEntity<SaleEgg> generateSaleEgg(@Valid @RequestBody SaleEgg saleEgg,
			BindingResult bindingResult) throws SaleEggException, SaleEggAmountException, SaleValidationErrorException {		
		logger.info("Controller - Calling method generateSaleEgg...");
		if (bindingResult.hasErrors()) {
			String message = new String();
			for(FieldError error : bindingResult.getFieldErrors()) {
				if (message.isEmpty()) {
					message = message + error.getField() + " : " + error.getDefaultMessage();
				} else {
					message = message + ", " + error.getField() + " : " + error.getDefaultMessage();
				}
			}
			throw new SaleValidationErrorException(message);
		}
		return new ResponseEntity<>(service.generateSaleEgg(saleEgg), HttpStatus.OK);
	}

}