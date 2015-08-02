package com.bukkit.flodov.ServicePostal;

import org.bukkit.block.Chest;

public abstract class BAL {
	protected Chest boite;
	protected String nom;
	
	public BAL(){
		
	}
	
	public BAL(String nom, Chest coffre){
		boite = coffre;
		this.nom = nom;
	}

	public Chest getBoite(){
		return boite;
	}
	public String getNom(){
		return nom;
	}
}
