package com.bukkit.flodov.ServicePostal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import com.bukkit.flodov.exceptions.BALPubliqueExistanteException;
import com.bukkit.flodov.exceptions.NoChestException;
import com.bukkit.flodov.exceptions.ServicePostalException;
import com.bukkit.flodov.tasks.PosteLocaleRunnable;

public class PosteLocale extends Poste {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5768327087262767940L;
	private PosteGenerale PG;
	protected List<BALPublique> reseau_publique;
	protected List<BALPrivee>  reseau_prive;
	private PosteLocaleRunnable thread;
	
	public PosteLocale(PosteLocale PL){
		this.PG = PL.PG;
		reseau_publique = new ArrayList<BALPublique>(PL.getReseau_publique());
		reseau_prive = new ArrayList<BALPrivee>(PL.getReseau_prive());
		thread = PL.thread;
		name = PL.getName();
		boite = PL.getBoite();
	}



	public PosteLocale(PosteGenerale PG,Chest coffre, String name) {
		
		NPCRegistry registry = PG.getRegistry();
		facteur = registry.createNPC(EntityType.PLAYER, ServicePostalMain.config.getString("nomPNJ.PL"));
		this.boite = coffre;
		facteur.spawn(boite.getLocation());
		this.name = name;
		this.PG = PG;
		reseau_publique = new ArrayList<BALPublique>();
		reseau_prive = new ArrayList<BALPrivee>();
		facteur.data().set("origine",this);
		facteur.data().set("mutex",false);
		thread = new PosteLocaleRunnable(this);

	}
	
	public PosteLocale(SauvegardeClasse sc, PosteGenerale PG) {
		name = sc.getNom();
		Location loc = new Vector(sc.getCoord().get(0),sc.getCoord().get(1),sc.getCoord().get(2)).toLocation(Bukkit.getWorlds().get(0));
		boite = (Chest) loc.getBlock().getState();
		NPCRegistry registry = PG.getRegistry();
		facteur = registry.createNPC(EntityType.PLAYER, ServicePostalMain.config.getString("nomPNJ.PL"));
		facteur.spawn(boite.getLocation());
		this.PG=PG;
		reseau_publique = new ArrayList<BALPublique>();
		reseau_prive = new ArrayList<BALPrivee>();
		facteur.data().set("origine",this);
		facteur.data().set("mutex",false);
		thread = new PosteLocaleRunnable(this);

	}

	public void addBALPublique(SauvegardeClasse sc){
		Location loc = new Vector(sc.getCoord().get(0),sc.getCoord().get(1),sc.getCoord().get(2)).toLocation(Bukkit.getWorlds().get(0));
		Chest c = (Chest) loc.getBlock().getState();
		
		List<Location> chemin = new ArrayList<Location>();
		for(List<Integer> tmp : sc.getChemin()){
			loc = new Vector(tmp.get(0),tmp.get(1),tmp.get(2)).toLocation(Bukkit.getWorlds().get(0));
			chemin.add(loc);
		}
		
		reseau_publique.add(new BALPublique(sc.getNom(),c,chemin));
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("ServicePostal"), thread, 0, ServicePostalMain.config.getInt("parametres.tourneePL") * 20L);	}
	public void addBALPrivee(SauvegardeClasse sc){
		Location loc = new Vector(sc.getCoord().get(0),sc.getCoord().get(1),sc.getCoord().get(2)).toLocation(Bukkit.getWorlds().get(0));
		Chest c = (Chest) loc.getBlock().getState();
		List<Location> chemin = new ArrayList<Location>();
		for(List<Integer> tmp : sc.getChemin()){
			loc = new Vector(tmp.get(0),tmp.get(1),tmp.get(2)).toLocation(Bukkit.getWorlds().get(0));
			chemin.add(loc);
		}
		reseau_prive.add(new BALPrivee(sc.getNom(),c,chemin));
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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("ServicePostal"), thread, 0, ServicePostalMain.config.getInt("parametres.tourneePL") * 20L);
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
		reseau_prive.add(new BALPrivee(nom,c));
	}

	public List<BALPublique> getReseau_publique() {
	return reseau_publique;
}


public List<BALPrivee> getReseau_prive() {
	return reseau_prive;
}




	public void NpcInitTournee(){
			List<BALPublique> tournee = new ArrayList<BALPublique>();
			for(BALPublique bal : reseau_publique){
				if(bal.pret) tournee.add(bal);
			}
			if(!tournee.isEmpty()){
				facteur.data().set("tournee", tournee);
				facteur.data().set("sens", true);
				facteur.data().set("mutex",false);
				facteur.data().set("chemin", tournee.get(0).getChemin());
				facteur.data().set("indice_chemin",0);
				//bouge(reseau_publique.get(0).getBoite().getLocation());
				bouge(tournee.get(0).getChemin().get(0));//on l'envoie sur la premiere etape du chemin
				facteur.data().set("mutex",true);
			}
		
	}
	
	public void bouge(Location loc){
		facteur.getNavigator().setTarget(loc);
	}
	public boolean NpcDispo(){
		return !(boolean) facteur.data().get("mutex")&&!reseau_publique.isEmpty();
	}
	public void removeBAL(BALPublique BAL){
		reseau_publique.remove(BAL);
	}



	public void removeBAL(BALPrivee bAL) {
		reseau_prive.remove(bAL);
		
	}
	public void stop(){
		facteur.destroy();
		thread.stop();
		
	}

}
