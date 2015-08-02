package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;


public class PGExistanteException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PGExistanteException() {
		// TODO Auto-generated constructor stub
	}
	public void toString(Player player){player.sendMessage("La Poste Générale existe déjà.");}


	

}
