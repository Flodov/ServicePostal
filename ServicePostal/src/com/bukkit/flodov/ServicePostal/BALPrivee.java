package com.bukkit.flodov.ServicePostal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Chest;

public class BALPrivee extends BAL {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6735051194368243324L;

	public BALPrivee(String nom, Chest c,List<Location> chemin) {
		super(nom,c,chemin);
	}
	
	public BALPrivee(BALPrivee BAL){
		nom = BAL.getNom();
		boite = BAL.getBoite();
		
	}

	public BALPrivee(String nom, Chest c) {
		super(nom,c);
	}

}
