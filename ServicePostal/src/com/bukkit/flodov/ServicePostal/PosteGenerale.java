package com.bukkit.flodov.ServicePostal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;

import com.bukkit.flodov.exceptions.NoChestException;
import com.bukkit.flodov.exceptions.PGExistanteException;
import com.bukkit.flodov.exceptions.PGNoFoundException;
import com.bukkit.flodov.exceptions.PLExistanteException;
import com.bukkit.flodov.exceptions.PLNoTrouveeException;
import com.bukkit.flodov.exceptions.ServicePostalException;

public class PosteGenerale extends Poste {

	public static boolean init = false;
	
	protected List<PosteLocale> reseau;
	
	public PosteGenerale(){}
	public PosteGenerale(NPC facteur, Chest boite, List<PosteLocale> reseau) {
		super(facteur, boite);
		this.reseau = reseau;
		// TODO Auto-generated constructor stub + load config
	}

	public void initialisation(Block block) throws Exception{
		//On initialise un coffre + npc pour la PG
		if( !init ) {
			
			
			if(block.getType() == Material.CHEST){
				boite = (Chest) block.getState();
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				//On crée le PNJ
				facteur = registry.createNPC(EntityType.PLAYER, "Intendant");
				facteur.spawn(boite.getLocation());
				reseau = new ArrayList<PosteLocale>();
				name = "PosteGénéral";
				init = true;
				
			}
			else{
				throw new NoChestException();
			}
		}else{
			throw new PGExistanteException();
		}
		
		
		
	}
	
	public void addPL(Block coffre, String name) throws Exception{
		
		if(init){
			if(coffre.getType() == Material.CHEST && !boite.equals((Chest) coffre.getState())){
				Chest chest = (Chest) coffre.getState();
				if(!reseau.isEmpty()){
					Iterator<PosteLocale> ite = reseau.iterator();
					//On vérifie que ce coffre n'est pas déjà affecté à une PL
					while(ite.hasNext()){
						Poste tmp = ite.next();
						if(tmp.getBoite().getLocation().equals(coffre.getLocation())|| tmp.getName().equalsIgnoreCase(name)){
							throw new PLExistanteException();
						}
					}
				}
				reseau.add(new PosteLocale(this,chest,name));
			}else{
				throw new NoChestException();
			}
		}
		else{
			throw new PGNoFoundException();
		}
		
		
		
	}
	public void addBAL(Block coffre, int type, String nom, String proprietaire) throws ServicePostalException{

		PosteLocale PL = null ;
		boolean trouve = false;
		Iterator<PosteLocale> ite = reseau.iterator();
		while(ite.hasNext() && !trouve){
			PL = ite.next();
			if(PL.getName().equalsIgnoreCase(proprietaire)) trouve = true;
		}
		
		if(!trouve) throw new PLNoTrouveeException();
		
		switch(type){
			case(0):PL.addBALPrivee(coffre, nom);break;
			case(1):PL.addBALPublique(coffre, nom);break;
		}
		
	}
}
