package com.retooling.pursalegg.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.retooling.pursalegg.entity.PurchaseEgg;

public interface PurchaseEggRepository extends MongoRepository<PurchaseEgg, String>{

}
