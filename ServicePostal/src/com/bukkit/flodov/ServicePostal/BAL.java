package com.bukkit.flodov.ServicePostal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Chest;

public abstract class BAL implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	public List<Integer> getCoord(){
		List<Integer> tmp = new ArrayList<Integer>();
		tmp.add(boite.getX());
		tmp.add(boite.getY());
		tmp.add(boite.getZ());
		return tmp;
	}
}
