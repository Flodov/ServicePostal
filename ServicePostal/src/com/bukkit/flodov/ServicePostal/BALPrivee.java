package com.bukkit.flodov.ServicePostal;

import org.bukkit.block.Chest;

public class BALPrivee extends BAL {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6735051194368243324L;

	public BALPrivee(String nom, Chest c) {
		this.nom = nom;
		this.boite = c;
	}

}
