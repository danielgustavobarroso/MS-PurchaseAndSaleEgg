package com.retooling.pursalegg.service;

public enum EggOrigin {
	Bought("B"), Deposited("D"), Load("L");
	
	private EggOrigin(String origin) {
		this.origin=origin;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	private String origin;
	
}
