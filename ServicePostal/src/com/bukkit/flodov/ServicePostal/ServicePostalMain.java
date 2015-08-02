package com.bukkit.flodov.ServicePostal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.flodov.listeners.Commands;
import com.bukkit.flodov.tasks.Listeners;

public class ServicePostalMain extends JavaPlugin {

	public static FileConfiguration config;
	
	@Override
	public void onEnable(){
		loadConfig();
		this.getServer().getPluginManager().registerEvents(new Listeners(), this);
		//Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TestHello(), 0, 1 * 20L);
		
		/*System.out.println(this.getConfig().getString("messages.1"));
		// coffre pointé
			  
		*/
		 PosteGenerale PG = new PosteGenerale();
		 Commands commandes = new Commands(this,null,PG);
		//this.getCommand("createPG").setExecutor(commandes);
		this.getCommand("poste").setExecutor(commandes);
		this.getCommand("poste test").setExecutor(commandes);//a enlever
		/*Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TestHello(), 0,config.getInt("config.hi-time") * 20L); // La durée est en tick, donc on 
		*/
		
	}
	public void loadConfig() {
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		}
	
	public void onDisable(){
		Bukkit.getScheduler().cancelTasks(this);
		
	}
	
		
}
