package com.bukkit.flodov.listeners;


import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import com.bukkit.flodov.ServicePostal.PosteGenerale;
import com.bukkit.flodov.ServicePostal.TestHello;
import com.bukkit.flodov.exceptions.PLNoTrouveeException;
import com.bukkit.flodov.exceptions.ServicePostalException;

@SuppressWarnings("unused")
public class Commands implements CommandExecutor{

	private Plugin plugin;
	private Statement state;
	private PosteGenerale PG;
	
	public Commands(Plugin plugin, Statement state, PosteGenerale PG){
		this.plugin = plugin;
		this.state = state;
		this.PG = PG;
	}
	@Override
	public boolean onCommand(final CommandSender sender,final Command cmd, final String label,final String[] arg3) {
		
			Player player = null;
			if(sender instanceof Player) {
				player = (Player)sender;
			}
			else {
				return false;
			}

				if(cmd.getName().equalsIgnoreCase("poste") && arg3.length != 0){
					if(arg3[0].equalsIgnoreCase("createPG")) {
						/*
						player.sendMessage(ServicePostalMain.config.getString("messages.2"));
						String message = ServicePostalMain.config.getString("messages.3").replace("/player/", player.getName());
						Bukkit.broadcastMessage(ChatColor.RED + message);
						*/
						//System.out.println(Bukkit.getPlayer("Hautension").getAddress().toString());
					    
						//NPCRegistry registry = CitizensAPI.getNPCRegistry();
					    //NPC npc = registry.createNPC(EntityType.PLAYER, "MyNPC");
					    //npc.spawn(Bukkit.getWorlds().get(0).getSpawnLocation());
					    
						//NPC npc = registry.getById(1);
						//npc.teleport(Bukkit.getPlayer("Hautension").getLocation(),  TeleportCause.PLUGIN);
					    
					    //npc.getNavigator().setTarget(Bukkit.getPlayer("Hautension").getLocation()); // walk to a point
					   // npc.getNavigator().setTarget(entity, true); // aggressively attack an entity
						
					   Location loc = player.getLocation();
					   loc.setY(loc.getY()-0.8);
					   Block b = loc.getBlock();
					  
					 /* if(b.getType() == Material.CHEST){
						   Bukkit.broadcastMessage("ok");
						  b.setMetadata("test", new FixedMetadataValue(plugin,"1234"));
						  Bukkit.broadcastMessage(b.getMetadata("test").get(0).asString());
						  Chest chest = (Chest) b.getState();
						 Inventory inventaire =  chest.getInventory();
						 npc.data().set("test", inventaire.getItem(0));
						 inventaire.clear();
						 //inventaire.addItem(new ItemStack(Material.BAKED_POTATO));
						 //player.getInventory().addItem(new ItemStack(Material.BAKED_POTATO));
						 inventaire.addItem((ItemStack)npc.data().get("test"));
					   }
					   */
					   
				
					   try{
						   PG.initialisation(b);
					   }catch(ServicePostalException  e){
						  e.toString(player);
						   
					   } catch (Exception e) {
						e.printStackTrace();
					   }
					   return true;
					}
					
					if(arg3[0].equalsIgnoreCase("createPL")){
						
						if(arg3.length != 2){
							return false;
						}
						
						Location loc = player.getLocation();
						loc.setY(loc.getY()-0.8);
						
						 try {
							PG.addPL(loc.getBlock(), arg3[1]);
						} catch (ServicePostalException e) {
							e.toString(player);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
					if(arg3[0].equalsIgnoreCase("createBAL")){
						if(arg3.length != 4) return false;
						   Location loc = player.getLocation();
						   loc.setY(loc.getY()-0.8);
						   Block b = loc.getBlock();
						   int type;
						   switch(arg3[2].toLowerCase()){
						   case("-private"): type = 0; break;
						   case("-public") : type = 1; break;
						   default: return false;
						   }
						   
						try {
							PG.addBAL(b,type, arg3[1].toString(), arg3[3].toString());
						} catch (ServicePostalException e) {
							e.toString(player);
						}
						return true;
					}
					if(arg3[0].equalsIgnoreCase("test")){
						Bukkit.getScheduler().cancelTasks(Bukkit.getPluginManager().getPlugin("ServicePostal"));
						return true;
					}
					if(arg3[0].equalsIgnoreCase("envoyer")) {
						
						switch(arg3[1].toLowerCase()){
						case("-lettre"):
							if(player.getItemInHand().getType() == Material.WRITTEN_BOOK){
								if(!arg3[2].isEmpty() && !arg3[3].isEmpty()){
									try {
										PG.PosterLettre( player.getItemInHand(), arg3[2], arg3[3],arg3[4]);
									} catch (ServicePostalException e) {
										// TODO Auto-generated catch block
										e.toString(player);
									}
								}
							}
							else{
								player.sendMessage("Veuillez tenir la lettre à envoyer.");
							}
						case("-colis"):
							
						}
						return true;
					}
				}
				
				return false;
	}

}
