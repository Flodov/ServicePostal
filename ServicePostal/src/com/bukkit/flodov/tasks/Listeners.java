package com.bukkit.flodov.tasks;

import java.util.List;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.bukkit.flodov.ServicePostal.BALPublique;
import com.bukkit.flodov.ServicePostal.Poste;
import com.bukkit.flodov.ServicePostal.PosteGenerale;
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
		
		if(npc.data().get("origine").getClass() == PosteGenerale.class){
		
			List<PosteLocale> tournee = npc.data().get("tournee");
			if(!(boolean) npc.data().get("sens")){
					tournee.remove(0);
					npc.data().set("tournee",tournee);
					if(tournee.size() != 0){
						npc.data().set("sens", true);
						npc.getNavigator().setTarget(tournee.get(0).getBoite().getLocation());
					}
				}
				
			
			else{
				//aller
				npc.data().set("sens", false);
				npc.getNavigator().setTarget((Location) ((Poste) npc.data().get("origine")).getBoite().getLocation());
			}
			
			
			
			
		}else{
			
			if(npc.data().get("origine").getClass() == PosteLocale.class){
				List<BALPublique> tournee = npc.data().get("tournee");
				if(!(boolean) npc.data().get("sens")){
					tournee.remove(0);//d'ou il vient
					npc.data().set("tournee",tournee);
					if(tournee.size() != 0){
						npc.data().set("sens", true);
						npc.getNavigator().setTarget(tournee.get(0).getBoite().getLocation());
					}
					else{
						npc.data().set("mutex",false);
					}
				}
				
			
			else{
				//aller
				npc.data().set("sens", false);
				npc.getNavigator().setTarget((Location) ((Poste) npc.data().get("origine")).getBoite().getLocation());
			}
			}
		}
	}
	
	
}
