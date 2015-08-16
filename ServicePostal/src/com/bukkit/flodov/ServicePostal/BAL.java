package com.bukkit.flodov.ServicePostal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Chest;

public abstract class BAL implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Chest boite;
	protected String nom;
	protected List<Location> chemin = new ArrayList<Location>();
	protected boolean pret = false;
	
	public BAL(){
		
	}
	
	public BAL(String nom, Chest coffre,List<Location> chemin){
		boite = coffre;
		this.nom = nom;
		this.chemin = new ArrayList<Location>(chemin);
		if(!chemin.isEmpty()) pret = true;
	}

	public BAL(String nom, Chest c) {
		boite = c;
		this.nom = nom;
	}

	public Chest getBoite(){
		return boite;
	}
	public String getNom(){
		return nom;
	}
	public List<Integer> getCoord(){
		List<Integer> tmp = new ArrayList<Integer>();
		tmp.add(boite.getX());
		tmp.add(boite.getY());
		tmp.add(boite.getZ());
		return tmp;
	}
	public void setChemin(List<Location> tmp){
		chemin = new ArrayList<Location>(tmp);
	}
	public void clearChemin(){
		chemin.clear();
	}
	public List<Location> getChemin(){
		return chemin;
	}
	public boolean estPret(){
		return pret;
	}
	public List<ArrayList<Integer>> getCoords(){
		List<ArrayList<Integer>> liste = new ArrayList<ArrayList<Integer>>();
		for(Location loc : chemin){
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			tmp.add(loc.getBlockX());
			tmp.add(loc.getBlockY());
			tmp.add(loc.getBlockZ());
			
			liste.add(tmp);
		}
		
		return liste;
	}
	
}
