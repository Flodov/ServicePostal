package com.bukkit.flodov.ServicePostal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.Vector;

import com.bukkit.flodov.exceptions.BALPriveeNoFoundException;
import com.bukkit.flodov.exceptions.EtapeTropLoinException;
import com.bukkit.flodov.exceptions.NoChestException;
import com.bukkit.flodov.exceptions.PGExistanteException;
import com.bukkit.flodov.exceptions.PGNoFoundException;
import com.bukkit.flodov.exceptions.PLExistanteException;
import com.bukkit.flodov.exceptions.PLNoTrouveeException;
import com.bukkit.flodov.exceptions.ReseauExistantException;
import com.bukkit.flodov.exceptions.ServicePostalException;
import com.bukkit.flodov.exceptions.SessionPathMutexException;
import com.bukkit.flodov.tasks.PNJPosteGeneralRunnable;

public class PosteGenerale extends Poste{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5472691240981149922L;

	public static boolean init = false;
	
	private PNJPosteGeneralRunnable thread;
	
	protected List<PosteLocale> reseau;
	protected String nomFichier = "saveSP";
	protected static NPCRegistry registry;
	protected List<SessionPath> sessions = new ArrayList<SessionPath>();
	
	public PosteGenerale(){}
	public PosteGenerale(NPC facteur, Chest boite, List<PosteLocale> reseau) {
		super(facteur, boite);
		this.reseau = reseau;
	}
	
	public  PosteGenerale(SauvegardeClasse sc){
		init = true;
		Location loc = new Vector(sc.getCoord().get(0),sc.getCoord().get(1),sc.getCoord().get(2)).toLocation(Bukkit.getWorlds().get(0));
		boite = (Chest) loc.getBlock().getState();
		registry = CitizensAPI.getNPCRegistry();
		facteur = registry.createNPC(EntityType.PLAYER, ServicePostalMain.config.getString("nomPNJ.PG"));
		facteur.spawn(boite.getLocation());
		name = sc.getNom();
		thread = new PNJPosteGeneralRunnable(this);
		facteur.data().set("origine", this);
		facteur.data().set("mutex", false);
		reseau = new ArrayList<PosteLocale>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("ServicePostal"), thread, 0, ServicePostalMain.config.getInt("parametres.tourneePG") * 20L);	}
	
	public void initialisation(Block block) throws Exception{
		//On initialise un coffre + npc pour la PG
		if( !init ) {
			
			if(block.getType() == Material.CHEST){
				boite = (Chest) block.getState();
				
				registry = CitizensAPI.getNPCRegistry();
				//On crée le PNJ
				facteur = registry.createNPC(EntityType.PLAYER, ServicePostalMain.config.getString("nomPNJ.PG"));
				facteur.spawn(boite.getLocation());
				reseau = new ArrayList<PosteLocale>();
				name = "PosteGénérale";
				init = true;
				thread = new PNJPosteGeneralRunnable(this);
				facteur.data().set("origine", this);
				facteur.data().set("mutex", false);
				Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("ServicePostal"), thread, 0, ServicePostalMain.config.getInt("parametres.tourneePG") * 20L);
			}
			else{
				throw new NoChestException();
			}
		}else{
			throw new PGExistanteException();
		}
		
		
		
	}
	
	public void addPL(SauvegardeClasse sc){
		reseau.add(new PosteLocale(sc,this));
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
		
		
	}
	public boolean NpcDispo() {
		return !(boolean) facteur.data().get("mutex")&&!reseau.isEmpty();
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
	public void posterColis(ItemStack lettre, String PL_name, String BAL_name, String proprietaire_name, Block coffre) throws ServicePostalException{
		
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
		
		if(this.boite.equals(coffre)) throw new NoChestException();
		
		for(PosteLocale PL : reseau){
			if(PL.getBoite().equals(coffre)) throw new NoChestException();
			for(BALPublique BAL : PL.getReseau_publique()){
				if(BAL.getBoite().equals(coffre)) throw new NoChestException();
			}
			for(BALPrivee BAL : PL.getReseau_prive()){
				if(BAL.getBoite().equals(coffre)) throw new NoChestException();
			}
		}
		
		String contenu = new String();
		Chest boite = (Chest) coffre.getState();
		int nbStack = 1,nbPages = 1; 
		List<String> pages = new ArrayList<String>( tmp.getPages());
		
		for(ItemStack item : boite.getBlockInventory()){
			if(item != null){	
				if(nbStack++> 13){
					nbPages++;
					nbStack=0;
					pages.add(0,contenu);
					contenu = "";
				}
			contenu+=""+(item.getType()+" "+item.getData().toItemStack().getDurability()+" "+item.getAmount()+"\n");
			}
		}
		SimpleDateFormat formater = new SimpleDateFormat("'le' dd MMMM yyyy 'à' hh:mm:ss");
		pages.add(0, contenu);
		pages.add(0, "------------------\n    Cadre d'envoi\n------------------\n"+"Expéditeur : " +tmp.getAuthor()+"\nDestinataire : "+proprietaire_name+"\nAdresse : "+BAL_name+"\nLieu : "+PL_name+"\nDate : "+formater.format(new Date()));
		
		
		tmp.setPages(pages);
		tmp.setDisplayName("colis");
		List<String> destination = new ArrayList<String>();
		destination.add(PL_name);
		destination.add(BAL_name);
		destination.add(proprietaire_name);
		destination.add(Integer.toString(nbPages));

		tmp.setLore(destination);
		lettre.setItemMeta(tmp);
		boite.getBlockInventory().clear();
		coffre.breakNaturally();
	}
	
	public void save(){
		
		if(init){
			List<SauvegardeClasse> tab = new ArrayList<SauvegardeClasse>();
			
			//On ajoute la PG
			tab.add(new SauvegardeClasse(getCoord(),TypeClasse.PG,facteur.getUniqueId(),null,this.name,null));
			//on va ajouter chaque PL
			for(PosteLocale PL : reseau){
				tab.add(new SauvegardeClasse(PL.getCoord(),TypeClasse.PL,PL.getIDNPC(),null,PL.name,null));
				
				for(BALPublique BAL : PL.getReseau_publique()){
					tab.add(new SauvegardeClasse(BAL.getCoord(),TypeClasse.BALPublique, null,PL.getName(),BAL.getNom(),BAL.getCoords()));
				}
				for(BALPrivee BAL : PL.getReseau_prive()){
					tab.add(new SauvegardeClasse(BAL.getCoord(),TypeClasse.BALPrivee, null,PL.getName(),BAL.getNom(),BAL.getCoords()));
				}
			}
			
			
			
			try{
	            // On crée la sortie vers le fichier
				File fichier = new File(ServicePostalMain.nomFichier);
				
				ObjectOutputStream  out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fichier)));
				int longueur = tab.size();
				out.writeInt(longueur);
				
	            for(SauvegardeClasse tmp1 : tab ){
	            	out.writeObject(tmp1);
	            }
	            out.close();
	            
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	           
	        } catch (IOException e) {
	            e.printStackTrace();
	           
	        }
			

		}
		else{
			File fichier = new File(ServicePostalMain.nomFichier);
			fichier.delete();
		}
	}
	
	public NPCRegistry getRegistry(){
		return registry;
	}
	public void setRegistry(NPCRegistry r){
		registry = r;
	}
	
	public void removeBAL(String nom_BAL, String nom_PL,boolean type) throws ServicePostalException{
		
		boolean trouve = false;
		PosteLocale PL=null;
		for(PosteLocale tmp_PL : reseau){
			if(tmp_PL.getName().equalsIgnoreCase(nom_PL)){
				trouve = true;
				PL = tmp_PL;
				break;
			}
		}
		
		if(!trouve) throw new PLNoTrouveeException();
		trouve = false;
		if(type){
			//public
			for(BALPublique BAL : PL.getReseau_publique()){
				if(BAL.getNom().equalsIgnoreCase(nom_BAL)){
					trouve = true;
					PL.removeBAL(BAL);
					break;
				}
					
			}
			if(!trouve) throw new BALPriveeNoFoundException();
		}
		else{
			//private
			for(BALPrivee BAL : PL.getReseau_prive()){
				if(BAL.getNom().equalsIgnoreCase(nom_BAL)){
					trouve = true;
					PL.removeBAL(BAL);
					break;
				}
					
			}
			if(!trouve) throw new BALPriveeNoFoundException();
		}
		
		
	}
	
	public void removePL(String nomPL) throws ServicePostalException{
		PosteLocale PL = null;
		for(PosteLocale tmp : reseau){
			if(tmp.getName().equalsIgnoreCase(nomPL)){
				PL = tmp;
				break;
			}
		}
		if(PL == null) new PLNoTrouveeException();
		
		if(PL.getReseau_prive().isEmpty() && PL.getReseau_publique().isEmpty()){
			PL.stop();
			reseau.remove(PL);
		}
		else{
			throw new ReseauExistantException();
		}
		
		
	}
	
	@SuppressWarnings("static-access")
	public void removePG() throws ReseauExistantException{
		if(reseau.isEmpty()){
			facteur.destroy();
			thread.stop();
			
			this.init = false;
			
		}
		else{
			throw new ReseauExistantException();
		}
	}
	
	public void setPath(String nom_PL, String nom_BAL, boolean type,Player p) throws ServicePostalException{
		
		PosteLocale PL = null;
		for(PosteLocale tmp : reseau){
			if(tmp.getName().equalsIgnoreCase(nom_PL)) PL = new PosteLocale(tmp);
		}
		
		if(PL == null) throw new PLNoTrouveeException();
		
		BAL bal = null;
		if(type){
			for(BALPublique tmp : PL.getReseau_publique()){
				if(tmp.getNom().equalsIgnoreCase(nom_BAL)) bal = new BALPublique(tmp);
			}
			
		}else{
			for(BALPrivee tmp : PL.getReseau_prive()){
				if(tmp.getNom().equalsIgnoreCase(nom_BAL)) bal = new BALPrivee(tmp);
			}
		}
		
		if(bal == null) throw new BALPriveeNoFoundException();
		//La BAL et la PL existe
		bal.pret = false;
		bal.clearChemin();
		bal.getChemin().add(PL.getBoite().getLocation());
		for(SessionPath s : sessions){
			if(s.getBal().equals(bal) || s.getJoueur().equals(p.getUniqueId())) throw new SessionPathMutexException();
		}
		
		sessions.add(new SessionPath(p.getUniqueId(),bal,PL.getName(),type));
		p.teleport(PL.getBoite().getLocation());
		
	}
	
	public void addStep(Player p) throws ServicePostalException{
		
		SessionPath s = null;
		for(SessionPath tmp : sessions){
			if(tmp.getJoueur().equals(p.getUniqueId())){
				s = new SessionPath(tmp);
			}
		}
		if(s==null) throw new SessionPathMutexException();
		
			if(s.getBal().getChemin().get(s.getBal().getChemin().size()-1).distance(p.getLocation()) <= 10.0)
				s.getBal().getChemin().add(p.getLocation());
			else throw new EtapeTropLoinException();

	}
	
	public void stopStep(Player p) throws SessionPathMutexException{
		SessionPath s = null;
		for(SessionPath tmp : sessions){
			if(tmp.getJoueur().equals(p.getUniqueId())){
				s = tmp;
				break;
			}
		}
		if(s==null) throw new SessionPathMutexException();
		s.getBal().getChemin().add(s.getBal().getBoite().getLocation());
		s.getBal().pret = true;
		PosteLocale PL = null;
		for(PosteLocale tmp : reseau){
			if(tmp.getName().equalsIgnoreCase(s.getNom_PL())){
				PL = tmp;
				break;
			}
		}
		BAL bal = null;
		if(s.isType()){
			for(BALPublique tmp : PL.getReseau_publique()){
				if(tmp.getNom().equalsIgnoreCase(s.getBal().getNom())){
					bal = tmp;
					break;
				}
			}
		}else{
			for(BALPrivee tmp : PL.getReseau_prive()){
				if(tmp.getNom().equalsIgnoreCase(s.getBal().getNom())){
					bal = tmp;
					break;
				}
			}
		}
		bal.setChemin(s.getBal().getChemin());
		bal.pret = true;
		sessions.remove(s);
		
	}
	
	public void liste(String nom_PL, String type, Player p){
		if(nom_PL.isEmpty()){
			//poste liste
			p.sendMessage("----------Plan du réseau -------");
			p.sendMessage("Postes Locales :");
			for(PosteLocale tmp : reseau){
				p.sendMessage(" - "+tmp.getName()+"\n");
			}
			
		}else{
				
			if(type.isEmpty()){
				//poste liste <ville>
				
				PosteLocale PL = null;
				for(PosteLocale tmp : reseau){
					if(tmp.getName().equalsIgnoreCase(nom_PL)) PL = new PosteLocale(tmp);
				}
				
				if(PL==null){
					p.sendMessage("Poste Locale inconnue, veuillez consulter le plan.");
					liste("","",p);
					return;
				}
				p.sendMessage("----------Plan du réseau -------");
				p.sendMessage("Poste Locale : "+nom_PL);
				p.sendMessage("Boîtes aux lettres publiques :");
				for(BAL bal : PL.getReseau_publique()){
					p.sendMessage(" - "+bal.getNom());
				}
				
				p.sendMessage("Boîtes aux lettres privées :");
				for(BAL bal : PL.getReseau_prive()){
					p.sendMessage(" - "+bal.getNom());
				}
				
			}else{
				//poste liste <ville> -option
				PosteLocale PL = null;
				for(PosteLocale tmp : reseau){
					if(tmp.getName().equalsIgnoreCase(nom_PL)) PL = new PosteLocale(tmp);
				}
				
				if(PL==null){
					p.sendMessage("Poste Locale inconnue, veuillez consulter le plan.");
					liste("","",p);
					return;
				}
				p.sendMessage("----------Plan du réseau -------");
				p.sendMessage("Poste Locale : "+nom_PL);
				
				
				switch(type){
				case("-public"):
					for(BAL bal : PL.getReseau_publique()){
						p.sendMessage(" - "+bal.getNom());
					}
					break;
				
				case("-private"):
					for(BAL bal : PL.getReseau_prive()){
						p.sendMessage(" - "+bal.getNom());
					}
					break;
				
				default:
					liste(nom_PL,"",p);
					break;
				}
			}
			
			
		}
	}

}
