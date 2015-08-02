package com.bukkit.flodov.ServicePostal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;

import com.bukkit.flodov.exceptions.BALPubliqueExistanteException;
import com.bukkit.flodov.exceptions.NoChestException;
import com.bukkit.flodov.exceptions.ServicePostalException;

public class PosteLocale extends Poste {
	private PosteGenerale PG;
	protected List<BALPublique> reseau_publique;
	protected List<BALPrivee>  reseau_prive;
	
	public PosteLocale(){
		
	}



	public PosteLocale(PosteGenerale PG,Chest coffre, String name) {
		
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		facteur = registry.createNPC(EntityType.PLAYER, "Facteur");
		this.boite = coffre;
		facteur.spawn(boite.getLocation());
		this.name = name;
		this.PG = PG;
		reseau_publique = new ArrayList<BALPublique>();
		reseau_prive = new ArrayList<BALPrivee>();

	}
	
	public void addBALPublique(Block coffre, String nom) throws ServicePostalException{
		
		if(coffre.getType() != Material.CHEST) throw new NoChestException();

		//On controle si elle n'est pas une PG
		if(PG.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
		
		
		Iterator<PosteLocale> ite = PG.reseau.listIterator();
		while(ite.hasNext()){
			PosteLocale tmp = ite.next();
			//On controle si elle n'est pas une PL
			if(tmp.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
			
			//On controle si le coffre n'est pas déjà pris
			Iterator<BALPublique> ite2 = tmp.reseau_publique.iterator();
			while(ite2.hasNext()){
				BALPublique BAL = ite2.next();
				if(BAL.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
			}
			Iterator<BALPrivee> ite3 = tmp.reseau_prive.iterator();
			while(ite3.hasNext()){
				BALPrivee BAL = ite3.next();
				if(BAL.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
			}
			
		}
		//On vérifie si le nom n'est pas déjà pris
		Iterator<BALPublique> ite2 = reseau_publique.iterator();
		while(ite2.hasNext()){
			BALPublique BAL = ite2.next();
			if(BAL.getNom().equalsIgnoreCase(nom)) throw new BALPubliqueExistanteException();
		}
		
		
		Chest c = (Chest) coffre.getState();
		reseau_publique.add(new BALPublique(nom,c));
	}
public void addBALPrivee(Block coffre, String nom) throws ServicePostalException{
		
		if(coffre.getType() != Material.CHEST) throw new NoChestException();

		//On controle si elle n'est pas une PG
		if(PG.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
		
		
		Iterator<PosteLocale> ite = PG.reseau.listIterator();
		while(ite.hasNext()){
			PosteLocale tmp = ite.next();
			//On controle si elle n'est pas une PL
			if(tmp.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
			
			//On controle si le coffre n'est pas déjà pris
			Iterator<BALPublique> ite2 = tmp.reseau_publique.iterator();
			while(ite2.hasNext()){
				BALPublique BAL = ite2.next();
				if(BAL.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
			}
			Iterator<BALPrivee> ite3 = tmp.reseau_prive.iterator();
			while(ite3.hasNext()){
				BALPrivee BAL = ite3.next();
				if(BAL.getBoite().getLocation().equals(coffre.getLocation())) throw new NoChestException();
			}
			
		}
		//On vérifie si le nom n'est pas déjà pris
		Iterator<BALPrivee> ite2 = reseau_prive.iterator();
		while(ite2.hasNext()){
			BALPrivee BAL = ite2.next();
			if(BAL.getNom().equalsIgnoreCase(nom)) throw new BALPubliqueExistanteException();
		}
		
		
		Chest c = (Chest) coffre.getState();
		reseau_publique.add(new BALPublique(nom,c));
	}

	public boolean possedeBALPublique(Chest coffre){
		//Si la boite est possédée par la PL
		Iterator<BALPublique> ite = reseau_publique.iterator();
		while(ite.hasNext()){
			BALPublique tmp = ite.next();
			if(tmp.getBoite().getLocation().equals(coffre.getLocation())) return true;
		}
		
		return false;
	}
	public boolean possedeBALPrivee(Chest coffre){
		Iterator<BALPrivee> ite = reseau_prive.iterator();
		while(ite.hasNext()){
			BALPrivee tmp = ite.next();
			if(tmp.getBoite().equals(coffre)) return true;
		}
		
		return false;
	}

}
