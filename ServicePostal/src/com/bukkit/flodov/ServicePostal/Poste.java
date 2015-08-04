package com.bukkit.flodov.ServicePostal;

import org.bukkit.Location;
import org.bukkit.block.Chest;

import net.citizensnpcs.api.npc.NPC;

public abstract class Poste {
	
	protected NPC facteur;
	protected  Chest boite;
	protected String name;
	
	public Poste(){}
	
	public Poste(NPC facteur, Chest boite){
		this.facteur = facteur;
		this.boite = boite;
	}
	
	public Chest getBoite(){
		return boite;
	}
	
	
	public String getName(){
		return name;
	}
	

	public boolean arrive(Location loc){
		
		return facteur.getEntity().getLocation().distance(loc) <= 1;
	}
	
}
