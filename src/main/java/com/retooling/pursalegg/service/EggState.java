package com.retooling.pursalegg.service;

public enum EggState {
	Available("A"), Discarded("D"), Sold("S"), ConvertToChicken("C");
	
	private EggState(String state) {
		this.state=state;
	}
	
	public String getState() {
		return state;
	}
	
	private String state;
	
}
