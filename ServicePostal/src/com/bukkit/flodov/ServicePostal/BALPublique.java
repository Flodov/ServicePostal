package com.bukkit.flodov.ServicePostal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Chest;

public class BALPublique extends BAL{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4573273163484626480L;
	protected PosteLocale poste;
	
	public BALPublique(String nom, Chest coffre,List<Location> chemin){
		super(nom, coffre,chemin);
	}
	public BALPublique(BALPublique BAL){
		nom = BAL.getNom();
		boite = BAL.getBoite();
	}
	public BALPublique(String nom, Chest c) {
		super(nom,c);
	}
	
	
	
}
