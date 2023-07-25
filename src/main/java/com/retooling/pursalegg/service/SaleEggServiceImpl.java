package com.retooling.pursalegg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.retooling.pursalegg.entity.Egg;
import com.retooling.pursalegg.entity.Farm;
import com.retooling.pursalegg.entity.SaleEgg;
import com.retooling.pursalegg.exception.SaleEggAmountException;
import com.retooling.pursalegg.exception.SaleEggException;
import com.retooling.pursalegg.repository.SaleEggRepository;

@Service
public class SaleEggServiceImpl implements SaleEggService {

	private static final Logger logger = LoggerFactory.getLogger(SaleEggServiceImpl.class);
	
	@Autowired
	SaleEggRepository repository;

	@Autowired
	private ApiCall apiCall;

	@Value("${api.microservice.use-date-simulator}")
	private boolean useDateSimulator;
	
	@Override
	public List<SaleEgg> getAllSaleEggs() {
		logger.info("Service - Calling method getSaleAllEggs...");
		return repository.findAll();			
	}

	@Override
	public SaleEgg saveSaleEgg(SaleEgg saleEgg) {
		logger.info("Service - Calling method saveSaleEgg...");
		return repository.save(saleEgg);
	}

	@Override
	public SaleEgg generateSaleEgg(SaleEgg saleEgg) throws SaleEggException, SaleEggAmountException {
		logger.info("Service - Calling method generateSaleEgg...");
		
		List<Egg> eggs = new ArrayList<Egg>();
		eggs = apiCall.getEggs(saleEgg.getFarmId());
		
		if (saleEgg.getUnits() > eggs.size()) {
			logger.info("La cantidad de huevos que se desea vender es mayor a la disponible");
			throw new SaleEggAmountException("La cantidad de huevos que se desea vender es mayor a la disponible.");
		}
		
		Date currentDate;
		if (useDateSimulator) {
			try {
				currentDate = apiCall.getDate();
			} catch (Exception ex) {
				throw new SaleEggException(ex.getMessage());
			}
		} else {
			currentDate = new Date();
		}
		
		for(int indice=0;indice<saleEgg.getUnits();indice++) {
			EggState eggSold = EggState.Sold;
			eggs.get(indice).setState(eggSold.getState());
			eggs.get(indice).setLastStateChangeDate(currentDate);
			apiCall.updateEgg(eggs.get(indice));		
			logger.info("Venta - Se actualiza estado del huevo con id [" + eggs.get(indice).getEggId() + "] a Vendido");
		}
		
		Farm farm = null;
		try {
			farm = apiCall.getFarm(saleEgg.getFarmId());
		} catch (Exception ex) {
			throw new SaleEggException(ex.getMessage());
		}

		farm.setMoney(farm.getMoney() + saleEgg.getTotalAmount());
		
		try {
			apiCall.updateFarm(farm);
		} catch (Exception ex) {
			throw new SaleEggException(ex.getMessage());
		}

		if (useDateSimulator) {
			try {
				saleEgg.setSaleDate(apiCall.getDate());
			} catch (Exception ex) {
				throw new SaleEggException(ex.getMessage());
			}
		}
		
		return this.saveSaleEgg(saleEgg);
	}
	
}
