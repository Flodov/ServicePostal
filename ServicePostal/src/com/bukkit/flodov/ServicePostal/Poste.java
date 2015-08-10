package com.bukkit.flodov.ServicePostal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Chest;

import net.citizensnpcs.api.npc.NPC;

public abstract class Poste implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7974151466769501496L;
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
	public List<Integer> getCoord(){
		List<Integer> tmp = new ArrayList<Integer>();
		tmp.add(boite.getX());
		tmp.add(boite.getY());
		tmp.add(boite.getZ());
		return tmp;
	}
	
	public UUID getIDNPC(){
		return facteur.getUniqueId();
	}
	
}
