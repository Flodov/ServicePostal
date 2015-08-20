package com.bukkit.flodov.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.bukkit.flodov.ServicePostal.BALPrivee;
import com.bukkit.flodov.ServicePostal.BALPublique;
import com.bukkit.flodov.ServicePostal.PosteLocale;


public class Listeners implements Listener{

	
	/*
	ServicePostalMain.config.getString("messages.4").replace("/player/", 
	A ENLEVER
	*/
	@EventHandler
	public void onNavigationComplete(NavigationCompleteEvent event){
		
		NPC npc = event.getNPC();
		
		if(npc.data().has("origine")){
		
			if(npc.data().get("origine").getClass() == PosteLocale.class){
					List<BALPublique> tournee = npc.data().get("tournee");
					PosteLocale PL = npc.data().get("origine");
					//on controle s'il est encore en train de marcher
					List<Location> chemin = npc.data().get("chemin");
					if(chemin != null){
						//on marche
						boolean sens = npc.data().get("sens");//Quel sens ?
						int indice = npc.data().get("indice_chemin");
						if(sens){
							//aller
							if(chemin.size() != indice+1){
								//si on n'est pas au bout 
								npc.getNavigator().setTarget(chemin.get(++indice));
								npc.data().set("indice_chemin", indice);
								return;
							}
							else{
								//on est au bout !
							}
							
						}else{
							//retour
							if(indice-1 >= 0){
								//si on n'est pas au bout
								npc.getNavigator().setTarget(chemin.get(--indice));
								npc.data().set("indice_chemin", indice);
								return;
							}
							else{
								//on est au bout !
							}
						}
					
						//c'est pas beau ... je sais ... mais j'ai pas la foi pour modifier
					}
					
					
					
					World world = Bukkit.getWorlds().get(0);
					if(world.getBlockAt(PL.getBoite().getLocation()).getType() != Material.CHEST)
						world.getBlockAt(PL.getBoite().getLocation()).setType(Material.CHEST);

					for(BALPublique bal : PL.getReseau_publique()){
						if(world.getBlockAt(bal.getBoite().getLocation()).getType() != Material.CHEST)
							world.getBlockAt(bal.getBoite().getLocation()).setType(Material.CHEST);

					}
					for(BALPrivee bal : PL.getReseau_prive()){
						if(world.getBlockAt(bal.getBoite().getLocation()).getType() != Material.CHEST)
							world.getBlockAt(bal.getBoite().getLocation()).setType(Material.CHEST);

					}
					if(!(boolean) npc.data().get("sens")){
						
						
						//A chaque fois qu'il rentre
						List<BookMeta> sacoche =  npc.data().get("sacoche");
						ItemStack tmp = new ItemStack(Material.WRITTEN_BOOK);
						//int i = PL.getBoite().getBlockInventory().firstEmpty();
						Chest boite = (Chest) world.getBlockAt(PL.getBoite().getLocation()).getState();
						int i = boite.getBlockInventory().firstEmpty();
						if(sacoche != null && tournee !=  null){
							for(BookMeta item : sacoche){
								
								List<String> contenu = new ArrayList<String>( item.getPages());
								SimpleDateFormat formater = new SimpleDateFormat("'le' dd MMMM yyyy 'à' hh:mm:ss");
								contenu.add(0, "------------------\n    Service Postal\n------------------\nEnvoyé depuis la Boîte Publique :\n"+tournee.get(0).getNom()+"\nA "+PL.getName()+"\n\nAffranchissement : \n"+formater.format(new Date()));
								item.setPages(contenu);
								tmp.setItemMeta(item);
								
								boite.getBlockInventory().setItem(i++, tmp);
							}
							sacoche.clear();
							sacoche = null;
							npc.data().set("sacoche", null);
						
							tournee.remove(0);//d'ou il vient
							npc.data().set("tournee",tournee);
						}
						//On prépare la suite
						if(tournee.size() != 0){
							npc.data().set("sens", true);
							npc.data().set("chemin", tournee.get(0).getChemin());
							npc.data().set("indice_chemin", 0);
							npc.getNavigator().setTarget(tournee.get(0).getChemin().get(0));
							//npc.getNavigator().setTarget(tournee.get(0).getBoite().getLocation());
							
						}
						else{
							//tournée terminée
							//Il faut distribuer le courrier ramassé
							//Chest boite = (Chest) world.getBlockAt(PL.getBoite().getLocation()).getState();
							for(ItemStack item : boite.getBlockInventory()){
								if(item != null){
									if(item.getType() == Material.WRITTEN_BOOK){
										BookMeta courrier = (BookMeta) item.getItemMeta();
										if(courrier.hasLore()){
											List<String> adresse = courrier.getLore();
											if(adresse.get(0).equalsIgnoreCase(PL.getName())){
												for(BALPrivee bal : PL.getReseau_prive()){
													if(bal.getNom().equalsIgnoreCase(adresse.get(1)) && bal.estPret()){
														//on a trouvé la BAL, il faut envoyer le PNJ
														npc.data().set("main", item);
														npc.data().set("sens", true);
														boite.getBlockInventory().remove(item);
														//npc.getNavigator().setTarget(bal.getBoite().getLocation());
														npc.data().set("chemin", bal.getChemin() );
														npc.data().set("indice_chemin", 0);
														npc.getNavigator().setTarget(bal.getChemin().get(0));
														return;
													}
												}
												
												break;
												
											}
										}
										
									}
								}
								
								
							}
							npc.data().set("mutex",false);
						}
					}
					
				
				else{
					//aller
					npc.data().set("sens", false);
					if(npc.data().get("main") != null){
						ItemStack item = npc.data().get("main");
						BookMeta courrier = (BookMeta) item.getItemMeta();
						for(BALPrivee BAL : PL.getReseau_prive()){
							if(BAL.getNom().equalsIgnoreCase(courrier.getLore().get(1))){
								Chest boite = (Chest) world.getBlockAt(BAL.getBoite().getLocation()).getState();
								boite.getBlockInventory().addItem(item);
								npc.data().set("main",null);
								//npc.data().set("sacoche", new ArrayList<BookMeta>());
								//npc.getNavigator().setTarget(PL.getBoite().getLocation());
								npc.data().set("chemin", BAL.getChemin());
								npc.data().set("indice_chemin", BAL.getChemin().size()-1);
								npc.getNavigator().setTarget(BAL.getChemin().get(BAL.getChemin().size()-1));
								break;
							}
						}
					}
					else{
						//On relève le courrier
						List<BALPublique> listeBAL = npc.data().get("tournee");
						BALPublique BAL = listeBAL.get(0);
						Chest boite = (Chest) world.getBlockAt(BAL.getBoite().getLocation()).getState();
						Inventory inventaire = boite.getBlockInventory();
						List<BookMeta> sacoche = new ArrayList<BookMeta>();
						for(ItemStack item : inventaire.getContents()){
							if(item!=null){
								if(item.getType() == Material.WRITTEN_BOOK  ){
									BookMeta tmp = (BookMeta) item.getItemMeta();
									if(tmp.hasLore()){
										sacoche.add(tmp);
										inventaire.remove(item);						
									}
								}
							}
						}
						npc.data().set("sacoche", sacoche);	
						//npc.getNavigator().setTarget((Location) ((Poste) npc.data().get("origine")).getBoite().getLocation());
						npc.data().set("chemin", BAL.getChemin());
						npc.data().set("indice_chemin", BAL.getChemin().size()-1);
						npc.getNavigator().setTarget(BAL.getChemin().get(BAL.getChemin().size()-1));
					}
				}
					
				}
			}
		}
	}
	
	

