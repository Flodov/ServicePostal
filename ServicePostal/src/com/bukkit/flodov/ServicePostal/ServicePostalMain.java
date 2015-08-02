package com.bukkit.flodov.ServicePostal;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.flodov.listeners.Commands;

public class ServicePostalMain extends JavaPlugin {

	public static FileConfiguration config;
	
	@Override
	public void onEnable(){
		loadConfig();
		/*this.getServer().getPluginManager().registerEvents(new Listeners(), this);
		System.out.println(this.getConfig().getString("messages.1"));
		//BIEN FAIRE GAFFE QUE C EST UN COFFRE CONNARD + coffre pointé
			  
		*/
		 PosteGenerale PG = new PosteGenerale();
		 Commands commandes = new Commands(this,null,PG);
		//this.getCommand("createPG").setExecutor(commandes);
		this.getCommand("poste").setExecutor(commandes);
		this.getCommand("poste test").setExecutor(commandes);
		/*Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TestHello(), 0,config.getInt("config.hi-time") * 20L); // La durée est en tick, donc on 
		*/
		
	}
	public void loadConfig() {
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		}
		
}
