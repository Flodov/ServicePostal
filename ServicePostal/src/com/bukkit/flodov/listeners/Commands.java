package com.bukkit.flodov.listeners;


import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import com.bukkit.flodov.ServicePostal.PosteGenerale;
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
						
						
						if(player.hasPermission("servicepostal.createpg")){
							if(arg3.length == 1){
							
							   Location loc = player.getLocation();
							   loc.setY(loc.getY()-0.8);
							   Block b = loc.getBlock();
							  
						
							   try{
								   PG.initialisation(b);
							   }catch(ServicePostalException  e){
								  e.toString(player);
								   
							   } catch (Exception e) {
								e.printStackTrace();
							   }
							   return true;
							}
							else{
								player.sendMessage("[ServicePostal] /poste createpg <nom>");
							}
						}
						else{
							player.sendMessage("[ServicePostal] Vous n'avez pas la permission.");
							return true;
						}
					}
					
					if(arg3[0].equalsIgnoreCase("createPL")){
						
						if(player.hasPermission("servicepostal.createpl")){
						
							if(arg3.length != 2){
								player.sendMessage("[ServicePostal] /poste createpl <nom>");
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
						else{
							player.sendMessage("[ServicePostal] Vous n'avez pas la permission.");
							return true;
						}
					}
					if(arg3[0].equalsIgnoreCase("createBAL")){
						if(player.hasPermission("servicepostal.createbal.*")){
							if(arg3.length != 4){
								player.sendMessage("[ServicePostal] /poste createbal <nomBAL> <option> <nomPL>");
								return false;
								
							}
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
						else{
							player.sendMessage("[ServicePostal] Vous n'avez pas la permission.");
							return true;
						}
					}
					if(arg3[0].equalsIgnoreCase("envoyer")) {
						
						if(player.hasPermission("servicepostal.envoyer")){
						
							switch(arg3[1].toLowerCase()){
							case("-lettre"):
								if(player.getItemInHand().getType() == Material.WRITTEN_BOOK){
									if(!arg3[2].isEmpty() && !arg3[3].isEmpty() && !arg3[4].isEmpty()){
										try {
											PG.PosterLettre( player.getItemInHand(), arg3[2], arg3[3],arg3[4]);
										} catch (ServicePostalException e) {
											// TODO Auto-generated catch block
											e.toString(player);
										}
									}
								}
								else{
									player.sendMessage("Veuillez tenir la lettre à envoyer dans votre main.");
								}
							return true;
							case("-colis"):
								
								Location loc = player.getLocation();
							   loc.setY(loc.getY()-0.8);
							   Block b = loc.getBlock();
							if(player.getItemInHand().getType()==Material.WRITTEN_BOOK  && b.getType() == Material.CHEST){
								
								
								   
								if(!arg3[2].isEmpty() && !arg3[3].isEmpty() && !arg3[4].isEmpty()){
									try {
										PG.posterColis(player.getItemInHand(), arg3[2], arg3[3], arg3[4], b);
									} catch (ServicePostalException e) {
										// TODO Auto-generated catch block
										e.toString();
									}
								}
								
							}
							default:
								player.sendMessage("[ServicePostal] /poste envoyer <option> <PL> <BAL> [destinataire]");
								return false;
							}
						}
						else{
							player.sendMessage("[ServicePostal] Vous n'avez pas la permission.");
						}
						return true;
					}
					if(arg3[0].equalsIgnoreCase("recevoir")){
						if(player.hasPermission("servicepostal.recevoir")){
							if(player.getItemInHand().getType() == Material.WRITTEN_BOOK){
								BookMeta courrier = (BookMeta) player.getItemInHand().getItemMeta();
								if(courrier.getDisplayName().equalsIgnoreCase("colis")){
									Location loc = player.getEyeLocation();
									   //loc.setY(loc.getY()+1);
									   Block b = loc.getBlock();
									   b.setType(Material.CHEST);
									   Chest coffre = (Chest) b.getState();
									   String contenu = courrier.getPage(3);
									   String[] liste = contenu.split("\n");
									   for(int i = 0 ; i<liste.length;i++){
										   String[] tmp = liste[i].split(" ");
										  tmp[1] = tmp[1].replace("§0", "");
										  tmp[0] = tmp[0].replace("§0", "");
										   coffre.getBlockInventory().setItem(coffre.getBlockInventory().firstEmpty(), new ItemStack(Material.valueOf(tmp[0]),Integer.parseInt(tmp[1])));
									   }
									   List<String> tmp = new ArrayList<String>(courrier.getPages());
									   tmp.set(2, "Reçu");
									   
									   courrier.setPages(tmp);
									   ItemStack item = player.getItemInHand();
									   item.setItemMeta(courrier);
									   player.setItemInHand(item);
									   return true;
									   
									   
								}
							}
						}else{
							player.sendMessage("[ServicePostal] Vous n'avez pas la permission.");
							return true;
						}
						
					}
					if(arg3[0].equalsIgnoreCase("save")){
						if(player.hasPermission("servicepostal.save")){
							PG.save();
							player.sendMessage("[ServicePostal] Sauvegarde terminée");
							return true;
						}else{
							player.sendMessage("[ServicePostal] Vous n'avez pas la permission.");
							return true;
						}
					}
					
				}
				
				return false;
	}

}
