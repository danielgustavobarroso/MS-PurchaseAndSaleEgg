package com.retooling.pursalegg.service;

import java.util.List;

import com.retooling.pursalegg.entity.SaleEgg;
import com.retooling.pursalegg.exception.SaleEggAmountException;
import com.retooling.pursalegg.exception.SaleEggException;

public interface SaleEggService {
	
	public List<SaleEgg> getAllSaleEggs();

	public SaleEgg saveSaleEgg(SaleEgg saleEgg);
	
	public SaleEgg generateSaleEgg(SaleEgg saleEgg) throws SaleEggException, SaleEggAmountException;
	
}
