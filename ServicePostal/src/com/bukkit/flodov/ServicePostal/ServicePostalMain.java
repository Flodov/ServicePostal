package com.bukkit.flodov.ServicePostal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.flodov.listeners.Commands;
import com.bukkit.flodov.listeners.Listeners;

public class ServicePostalMain extends JavaPlugin {

	public static FileConfiguration config;
	private PosteGenerale PG;
	public static String nomFichier = "plugins/ServicePostal/saveSP";
	private File fichier;
	
	@Override
	public void onEnable(){
		loadConfig();
		this.getServer().getPluginManager().registerEvents(new Listeners(), this);
		fichier = new File(nomFichier);
		if(fichier.exists()){
			try {
				ObjectInputStream in = new ObjectInputStream(
				          new BufferedInputStream(
				                  new FileInputStream(
				                    fichier)));
				//on recupere le nombre d'itération
				int nb = in.readInt();
				
				//PG est tjrs la premiere
				SauvegardeClasse SCPG = (SauvegardeClasse) in.readObject();
				//System.out.println(SCPG.toString());
				PG = new PosteGenerale(SCPG);
				
				for(int i = 1; i<nb;i++){
					SauvegardeClasse tmp = (SauvegardeClasse) in.readObject();
					switch(tmp.getType()){
					case PL:
						PG.addPL(tmp);
						break;
					case BALPublique:
						for(PosteLocale PL : PG.getReseau()){
							if(PL.getName().equals(tmp.getAppartenance())){
								PL.addBALPublique(tmp);
							}
						}
						break;
					case BALPrivee:
						for(PosteLocale PL : PG.getReseau()){
							if(PL.getName().equals(tmp.getAppartenance())){
								PL.addBALPrivee(tmp);
							}
						}
						break;
					default:
						break;
					}
				}
				
				
			in.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else PG = new PosteGenerale();
		 Commands commandes = new Commands(this,null,PG);

		this.getCommand("poste").setExecutor(commandes);

		
	}
	public void loadConfig() {
		config = this.getConfig();
		config.addDefault("parametres.tourneePG", 15);
		config.addDefault("parametres.tourneePL", 15);
		config.addDefault("nomPNJ.PG", "Intendant");
		config.addDefault("nomPNJ.PL", "Facteur");
		
		config.options().copyDefaults(true);
		this.saveConfig();
		}
	
	public void onDisable(){
		Bukkit.getScheduler().cancelTasks(this);
		PG.save();
		
		Iterator<NPC> ite = PG.getRegistry().iterator();
		while(ite.hasNext()){
			NPC npc = ite.next();
			if(npc.data().has("origine")){
				npc.despawn();
				ite.remove();
			}
		}
	}
	
	
		
}
