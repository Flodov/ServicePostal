package com.bukkit.flodov.tasks;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.bukkit.flodov.ServicePostal.ServicePostalMain;

public class Listeners implements Listener{

	
	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
	String message = "Bonjour";
	ServicePostalMain.config.getString("messages.4").replace("/player/", 
	event.getPlayer().getName());
	event.setJoinMessage(ChatColor.AQUA + message);
	}
}
