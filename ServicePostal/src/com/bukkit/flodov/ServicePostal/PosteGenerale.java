package com.bukkit.flodov.ServicePostal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.bukkit.flodov.exceptions.BALPriveeNoFoundException;
import com.bukkit.flodov.exceptions.NoChestException;
import com.bukkit.flodov.exceptions.PGExistanteException;
import com.bukkit.flodov.exceptions.PGNoFoundException;
import com.bukkit.flodov.exceptions.PLExistanteException;
import com.bukkit.flodov.exceptions.PLNoTrouveeException;
import com.bukkit.flodov.exceptions.ServicePostalException;
import com.bukkit.flodov.tasks.PNJPosteGeneralRunnable;

public class PosteGenerale extends Poste {

	public static boolean init = false;
	
	private PNJPosteGeneralRunnable thread;
	
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
				thread = new PNJPosteGeneralRunnable(this);
				facteur.data().set("origine", this);
				facteur.data().set("mutex", false);
				Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("ServicePostal"), thread, 0, 15 * 20L);
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
	public List<PosteLocale> getReseau(){
		return reseau;
	}
	
	public void NpcInitTournee(){
		List<PosteLocale> tournee = new ArrayList<PosteLocale>();
		tournee.add(null);
		for(PosteLocale PL : reseau){
			tournee.add(PL);
		}
		facteur.data().remove("tournee");
		facteur.data().set("tournee", tournee);
		facteur.data().set("mutex", true);
		facteur.data().set("sens", false);
		facteur.data().set("sacoche", new ArrayList<ItemStack>());
		facteur.teleport(this.boite.getLocation(),null);
		tournee();
	}
	
	public List<PosteLocale> getNpcTournee(){
		return facteur.data().get("tournee");
	}
	
	public void removeEtape(){
		List<PosteLocale> tournee = facteur.data().get("tournee");
		tournee.remove(0);
		facteur.data().remove("tournee");
		facteur.data().set("tournee", tournee);
		
	}
	public boolean TourneeHasNext(){
		List<PosteLocale> tournee = facteur.data().get("tournee");
		return tournee.size() != 0;
	}
	public void bouge(Location loc){
		facteur.getNavigator().setTarget(loc);
		facteur.data().set("sens", true);
	}
	
	public void PosterLettre(ItemStack lettre, String PL_name, String BAL_name, String destinataire_name) throws ServicePostalException{
		
		BookMeta tmp = (BookMeta) lettre.getItemMeta();
		
		
		boolean trouve = false;
		PosteLocale PL_dest = null;
		for(PosteLocale PL : reseau){
			if(PL.getName().equalsIgnoreCase(PL_name)){
				trouve = true;
				PL_dest = PL;
				break;
			}
		}
		if(!trouve) throw new PLNoTrouveeException();
		trouve = false;
		for(BALPrivee BAL : PL_dest.reseau_prive){
			if(BAL.getNom().equalsIgnoreCase(BAL_name)){
				trouve = true;
				break;
				
			}
		}
		
		if(!trouve) throw new BALPriveeNoFoundException();
		
		List<String> pages = new ArrayList<String>( tmp.getPages());
		SimpleDateFormat formater = new SimpleDateFormat("'le' dd MMMM yyyy 'à' hh:mm:ss");
		pages.add(0, "------------------\n    Cadre d'envoi\n------------------\n"+"Expéditeur : " +tmp.getAuthor()+"\nDestinataire : "+destinataire_name+"\nAdresse : "+BAL_name+"\nLieu : "+PL_name+"\nDate : "+formater.format(new Date()));
		tmp.setPages(pages);
		List<String> destination = new ArrayList<String>();
		destination.add(PL_name);
		destination.add(BAL_name);
		destination.add(destinataire_name);

		tmp.setLore(destination);
		lettre.setItemMeta(tmp);
		
		

		/*ItemStack writtenBook = new Material(Material.WRITTEN_BOOK);
BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
bookMeta.setTitle(title);
bookMeta.setAuthor(author);
bookMeta.setPages(text);
writtenBook.setItemMeta(bookMeta);
*/
		
		/*
		 * 
		 * List<String> pages = new ArrayList<String>();
pages.add("Hello, welcome to TimeVisualSale's server!"); // Page 1
pages.add("Website: timevisualsales.com"); // Page 2
pages.add("Hope you enjoy your stay/play!"); // Page 3
		 */
		
	}
	public boolean NpcDispo() {
		return !(boolean) facteur.data().get("mutex");
	}
	
	public void tournee(){
		//tournée de la PG
		List<ItemStack> sacoche = facteur.data().get("sacoche");
		List<PosteLocale> tournee = facteur.data().get("tournee");
		PosteLocale PL = tournee.get(0);
		
		if(!(boolean) facteur.data().get("sens")){
			//retour
				PosteGenerale PG = facteur.data().get("origine");
				
				//on vide la sacoche
				for(ItemStack item : sacoche){
					PG.getBoite().getBlockInventory().addItem(item);
					
				}
				sacoche.clear();
				//on remplit pour la nouvelle tournee
				
				tournee.remove(0);
				facteur.data().set("tournee",tournee);
				
				if(tournee.size() != 0){
					PL = tournee.get(0);
					
				
					for(ItemStack item : PG.getBoite().getBlockInventory()){
						if(item != null){
							if(item.getType()==Material.WRITTEN_BOOK){
								BookMeta courrier = (BookMeta) item.getItemMeta();
								if(courrier.hasLore()){
									if(courrier.getLore().get(0).equalsIgnoreCase(PL.getName())){
										sacoche.add(item);
										PG.getBoite().getBlockInventory().remove(item);
									}
								}
							}
						}
						
					}
					
					facteur.data().set("sacoche", sacoche);
					facteur.data().set("sens", true);
					//facteur.getNavigator().setTarget(tournee.get(0).getBoite().getLocation());
					facteur.teleport(tournee.get(0).getBoite().getLocation(), null);
					tournee();
				}
				else{
					facteur.data().set("mutex",false);
				}
				
				
			}
			
		
		else{
			//aller
			
			
			
			//on vide la sacoche
			for(ItemStack item : sacoche){
				PL.getBoite().getBlockInventory().addItem(item);
			}
			sacoche.clear();
			
			//On rempli à nouveau des courriers de la PL
			for(ItemStack item : PL.getBoite().getBlockInventory()){
				if(item != null){
					if(item.getType()==Material.WRITTEN_BOOK){
						BookMeta courrier = (BookMeta) item.getItemMeta();
						if(courrier.hasLore()){
							if(!courrier.getLore().get(0).equalsIgnoreCase(PL.getName())){
								sacoche.add(0, item);
								PL.getBoite().getBlockInventory().remove(item);
							}
						}
					}
						
				}
			}
			
			facteur.data().set("sacoche", sacoche);
			
			facteur.data().set("sens", false);
			facteur.teleport((Location) ((Poste) facteur.data().get("origine")).getBoite().getLocation(),null);
			tournee();
		}
		
	}
}
