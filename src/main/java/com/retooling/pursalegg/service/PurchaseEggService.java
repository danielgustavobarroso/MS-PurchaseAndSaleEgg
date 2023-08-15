package com.retooling.pursalegg.service;

import java.text.ParseException;
import java.util.List;

import com.retooling.pursalegg.entity.PurchaseEgg;
import com.retooling.pursalegg.exception.PurchaseEggLimitException;
import com.retooling.pursalegg.exception.PurchaseEggMoneyException;

public interface PurchaseEggService {
	
	public List<PurchaseEgg> getAllPurchaseEggs();
	
	public PurchaseEgg savePurchaseEgg(PurchaseEgg purchaseEgg);

	public PurchaseEgg generatePurchaseEgg(PurchaseEgg purchaseEgg) throws PurchaseEggLimitException,
		PurchaseEggMoneyException, ParseException;
	
}
