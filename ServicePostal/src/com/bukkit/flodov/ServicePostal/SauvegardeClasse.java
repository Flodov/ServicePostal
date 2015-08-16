package com.bukkit.flodov.ServicePostal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SauvegardeClasse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -749506718074204385L;
	private List<Integer> coord;
	private TypeClasse type;
	private UUID idNPC;
	private String appartenance;
	private String nom;
	private List<ArrayList<Integer>> chemin;

	
	public List<ArrayList<Integer>> getChemin() {
		return chemin;
	}

	public void setChemin(List<ArrayList<Integer>> chemin) {
		this.chemin = chemin;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public SauvegardeClasse(List<Integer>_coord, TypeClasse _type, UUID id, String _appartenance, String _nom, List<ArrayList<Integer>> _chemin){
		coord = _coord;
		type = _type;
		idNPC = id;
		appartenance = _appartenance;
		nom = _nom;
		if(_chemin != null) chemin = new ArrayList<ArrayList<Integer>>( _chemin);
	}


	public List<Integer> getCoord() {
		return coord;
	}

	public void setCoord(List<Integer> coord) {
		this.coord = coord;
	}

	public TypeClasse getType() {
		return type;
	}

	public void setType(TypeClasse type) {
		this.type = type;
	}

	public UUID getIdNPC() {
		return idNPC;
	}

	public void setIdNPC(UUID idNPC) {
		this.idNPC = idNPC;
	}

	public String getAppartenance() {
		return appartenance;
	}

	public void setAppartenance(String appartenance) {
		this.appartenance = appartenance;
	}
	
	public String toString(){
		return nom+" "+type+" "+idNPC+" "+appartenance+" "+coord+"\n";
	}
	
	
	
}
