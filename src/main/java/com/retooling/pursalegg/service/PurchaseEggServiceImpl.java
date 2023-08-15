package com.retooling.pursalegg.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.retooling.pursalegg.entity.Egg;
import com.retooling.pursalegg.entity.Farm;
import com.retooling.pursalegg.entity.PurchaseEgg;
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
	public PurchaseEgg generatePurchaseEgg(PurchaseEgg purchaseEgg) throws PurchaseEggLimitException, PurchaseEggMoneyException, ParseException {
		logger.info("Service - Calling method generatePurchaseEgg...");
		
		Farm farm = apiCall.getFarm(purchaseEgg.getFarmId());
		List<Egg> eggs = apiCall.getEggs(purchaseEgg.getFarmId());
		
		validateEggLimit(purchaseEgg.getUnits(), eggs.size(), farm.getEggLimit());
		validateFarmMoney(purchaseEgg.getTotalAmount(), farm.getMoney());
			
		Date currentDate = apiCall.getDate();
		
		//Se agregan huevos
		for(int indice=0;indice<purchaseEgg.getUnits();indice++) {
			Egg egg = new Egg();
			copyEggData(egg, purchaseEgg, currentDate);
			egg = apiCall.insertEgg(egg);
			logger.info("Compra - Se agrega huevo: [" + egg.getEggId() + "]");
		}

		//Se actualiza dinero de granja
		farm.setMoney(farm.getMoney() - purchaseEgg.getTotalAmount());
		apiCall.updateFarm(farm);
		
		//Se genera reporte en caso de alcanzar el limite
		if ((purchaseEgg.getUnits() + eggs.size()) == farm.getEggLimit()) {
			apiCall.generateReport(purchaseEgg.getFarmId());
		}
		
		purchaseEgg.setPurchaseDate(currentDate);
		
		return this.savePurchaseEgg(purchaseEgg);
	}
	
	private void validateEggLimit(long units, int eggsCount, long eggLimit) throws PurchaseEggLimitException {
		if ((units + eggsCount) > eggLimit) {
			logger.info("La cantidad de huevos a comprar supera el límite de la granja.");
			throw new PurchaseEggLimitException("La cantidad de huevos a comprar supera el límite de la granja.");
		}	
	}
	
	private void validateFarmMoney(double totalAmount, double money) throws PurchaseEggMoneyException {
		if (totalAmount > money) {
			logger.info("La cantidad de dinero utilizada supera el monto disponible.");
			throw new PurchaseEggMoneyException("La cantidad de dinero utilizada supera el monto disponible.");
		}
	}
	
	private void copyEggData(Egg egg, PurchaseEgg purchaseEgg, Date currentDate) {
		egg.setFarmId(purchaseEgg.getFarmId());
		EggState eggAvailable = EggState.Available;
		egg.setState(eggAvailable.getState());
		egg.setCreationDate(currentDate);
		EggOrigin eggOrigin = EggOrigin.Bought;
		egg.setOrigin(eggOrigin.getOrigin());
		egg.setLastStateChangeDate(egg.getCreationDate());
	}
	
}
