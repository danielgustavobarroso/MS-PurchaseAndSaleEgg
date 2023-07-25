package com.retooling.pursalegg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.retooling.pursalegg.entity.Egg;
import com.retooling.pursalegg.entity.Farm;
import com.retooling.pursalegg.entity.PurchaseEgg;
import com.retooling.pursalegg.exception.PurchaseEggException;
import com.retooling.pursalegg.exception.PurchaseEggLimitException;
import com.retooling.pursalegg.exception.PurchaseEggMoneyException;
import com.retooling.pursalegg.repository.PurchaseEggRepository;

@Service
public class PurchaseEggServiceImpl implements PurchaseEggService {

	private static final Logger logger = LoggerFactory.getLogger(PurchaseEggServiceImpl.class);
	
	@Autowired
	PurchaseEggRepository repository;

	@Autowired
	private ApiCall apiCall;

	@Value("${api.microservice.use-date-simulator}")
	private boolean useDateSimulator;
	
	@Override
	public List<PurchaseEgg> getAllPurchaseEggs() {
		logger.info("Service - Calling method getAllEggs...");
		return repository.findAll();	
	}

	@Override
	public PurchaseEgg savePurchaseEgg(PurchaseEgg purchaseEgg) {
		logger.info("Service - Calling method saveSaleEgg...");
		return repository.save(purchaseEgg);
	}

	@Override
	public PurchaseEgg generatePurchaseEgg(PurchaseEgg purchaseEgg) throws PurchaseEggException, PurchaseEggMoneyException, PurchaseEggLimitException {
		logger.info("Service - Calling method generatePurchaseEgg...");
		
		Farm farm = null;
		try {
			farm = apiCall.getFarm(purchaseEgg.getFarmId());
		} catch (Exception ex) {
			throw new PurchaseEggException(ex.getMessage());
		}
		
		List<Egg> eggs = null;
		try {
			eggs = apiCall.getEggs(purchaseEgg.getFarmId());
		} catch (HttpClientErrorException.NotFound ex) {
			eggs = new ArrayList<>();
		} catch (Exception ex) {
			throw new PurchaseEggException(ex.getMessage());
		}
		
		if ((purchaseEgg.getUnits() + eggs.size()) > farm.getEggLimit()) {
			logger.info("La cantidad de huevos a comprar supera el límite de la granja.");
			throw new PurchaseEggLimitException("La cantidad de huevos a comprar supera el límite de la granja.");
		}	
			
		if (purchaseEgg.getTotalAmount() > farm.getMoney()) {
			logger.info("La cantidad de dinero utilizada supera el monto disponible.");
			throw new PurchaseEggMoneyException("La cantidad de dinero utilizada supera el monto disponible.");
		}
		
		Date currentDate;
		if (useDateSimulator) {
			try {
				currentDate = apiCall.getDate();
			} catch (Exception ex) {
				throw new PurchaseEggException(ex.getMessage());
			}
		} else {
			currentDate = new Date();
		}
		
		//Se agregan huevos
		for(int indice=0;indice<purchaseEgg.getUnits();indice++) {
			Egg egg = new Egg();
			egg.setFarmId(purchaseEgg.getFarmId());
			EggState eggAvailable = EggState.Available;
			egg.setState(eggAvailable.getState());
			egg.setCreationDate(currentDate);
			EggOrigin eggOrigin = EggOrigin.Bought;
			egg.setOrigin(eggOrigin.getOrigin());
			egg.setLastStateChangeDate(egg.getCreationDate());
			try {
				egg = apiCall.insertEgg(egg);
				logger.info("Compra - Se agrega huevo: [" + egg.getEggId() + "]");
			} catch (Exception ex) {
				throw new PurchaseEggException(ex.getMessage());
			}
		}

		//Se actualiza dinero de granja
		farm.setMoney(farm.getMoney() - purchaseEgg.getTotalAmount());
		try {
			apiCall.updateFarm(farm);
		} catch (Exception ex) {
			throw new PurchaseEggException(ex.getMessage());
		}
		
		//Se genera reporte en caso de alcanzar el limite
		if ((purchaseEgg.getUnits() + eggs.size()) == farm.getEggLimit()) {
			try {
				apiCall.generateReport(purchaseEgg.getFarmId());
			} catch (Exception ex) {
				throw new PurchaseEggException(ex.getMessage());
			}
		}
		
		purchaseEgg.setPurchaseDate(currentDate);
		
		return this.savePurchaseEgg(purchaseEgg);
	}
	
}
