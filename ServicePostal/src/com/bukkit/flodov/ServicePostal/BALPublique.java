package com.bukkit.flodov.ServicePostal;

import org.bukkit.block.Chest;

public class BALPublique extends BAL{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4573273163484626480L;
	protected PosteLocale poste;
	
	public BALPublique(String nom, Chest coffre){
		super(nom, coffre);
	}
	
	
	
}
