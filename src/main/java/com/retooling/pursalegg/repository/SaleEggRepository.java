package com.retooling.pursalegg.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.retooling.pursalegg.entity.SaleEgg;

public interface SaleEggRepository extends MongoRepository<SaleEgg, String>{

}
