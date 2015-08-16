package com.bukkit.flodov.ServicePostal;

import java.util.UUID;

public class SessionPath {

	private UUID joueur;
	private BAL bal;
	private boolean type;
	private String nom_PL;
	
	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public String getNom_PL() {
		return nom_PL;
	}

	public void setNom_PL(String nom_PL) {
		this.nom_PL = nom_PL;
	}

	public SessionPath(UUID Joueur, BAL bal, String nom_PL, boolean type){
		this.joueur = Joueur;
		this.bal = bal;
		this.nom_PL = nom_PL;
		this.type = type;
	}

	public SessionPath(SessionPath tmp) {
		joueur = tmp.getJoueur();
		bal = tmp.getBal();
		nom_PL = tmp.getNom_PL();
		type = tmp.isType();
	}

	public UUID getJoueur() {
		return joueur;
	}

	public void setJoueur(UUID joueur) {
		this.joueur = joueur;
	}

	public BAL getBal() {
		return bal;
	}

	public void setBal(BAL bal) {
		this.bal = bal;
	}
	
	
}
