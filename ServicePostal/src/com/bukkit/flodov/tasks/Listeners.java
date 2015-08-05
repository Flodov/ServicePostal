package com.bukkit.flodov.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.bukkit.flodov.ServicePostal.BALPrivee;
import com.bukkit.flodov.ServicePostal.BALPublique;
import com.bukkit.flodov.ServicePostal.Poste;
import com.bukkit.flodov.ServicePostal.PosteLocale;


public class Listeners implements Listener{

	
	/*@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
	String message = "Bonjour";
	ServicePostalMain.config.getString("messages.4").replace("/player/", 
	event.getPlayer().getName());
	event.setJoinMessage(ChatColor.AQUA + message);
	}*/
	@EventHandler
	public void onNavigationComplete(NavigationCompleteEvent event){
		
		NPC npc = event.getNPC();
		
		if(npc.data().has("origine")){
		
			if(npc.data().get("origine").getClass() == PosteLocale.class){
				
				if(npc.data().get("origine").getClass() == PosteLocale.class){
					List<BALPublique> tournee = npc.data().get("tournee");
					PosteLocale PL = npc.data().get("origine");
					if(!(boolean) npc.data().get("sens")){
						
						
						//A chaque fois qu'il rentre
						List<BookMeta> sacoche =  npc.data().get("sacoche");
						ItemStack tmp = new ItemStack(Material.WRITTEN_BOOK);
						int i = PL.getBoite().getBlockInventory().firstEmpty();
						if(sacoche != null && tournee !=  null){
							for(BookMeta item : sacoche){
								
								List<String> contenu = new ArrayList<String>( item.getPages());
								SimpleDateFormat formater = new SimpleDateFormat("'le' dd MMMM yyyy 'à' hh:mm:ss");
								contenu.add(0, "------------------\n    Service Postal\n------------------\nEnvoyé depuis la Boîte Publique :\n"+tournee.get(0).getNom()+"\nA "+PL.getName()+"\n\nAffranchissement : \n"+formater.format(new Date()));
								item.setPages(contenu);
								tmp.setItemMeta(item);
								
								PL.getBoite().getBlockInventory().setItem(i++, tmp);
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
							npc.getNavigator().setTarget(tournee.get(0).getBoite().getLocation());
							
						}
						else{
							//tournée terminée
							//Il faut distribuer le courrier ramassé
							for(ItemStack item : PL.getBoite().getBlockInventory()){
								if(item != null){
									if(item.getType() == Material.WRITTEN_BOOK){
										BookMeta courrier = (BookMeta) item.getItemMeta();
										if(courrier.hasLore()){
											List<String> adresse = courrier.getLore();
											if(adresse.get(0).equalsIgnoreCase(PL.getName())){
												for(BALPrivee BAL : PL.getReseau_prive()){
													if(BAL.getNom().equalsIgnoreCase(adresse.get(1))){
														//on a trouvé la BAL, il faut envoyer le PNJ
														npc.data().set("main", item);
														npc.data().set("sens", true);
														PL.getBoite().getBlockInventory().remove(item);
														npc.getNavigator().setTarget(BAL.getBoite().getLocation());
													
														break;
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
								BAL.getBoite().getBlockInventory().addItem(item);
								npc.data().set("main",null);
								//npc.data().set("sacoche", new ArrayList<BookMeta>());
								npc.getNavigator().setTarget(PL.getBoite().getLocation());
								break;
							}
						}
					}
					else{
						
						List<BALPublique> listeBAL = npc.data().get("tournee");
						BALPublique BAL = listeBAL.get(0);
						
						Inventory inventaire = BAL.getBoite().getBlockInventory();
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
						npc.getNavigator().setTarget((Location) ((Poste) npc.data().get("origine")).getBoite().getLocation());
					}
				}
					
				}
			}
		}
	}
	
	
}
