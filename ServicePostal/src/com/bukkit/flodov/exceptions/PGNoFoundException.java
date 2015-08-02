package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class PGNoFoundException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PGNoFoundException() {
		// TODO Auto-generated constructor stub
	}
	public void toString(Player player){player.sendMessage("La Poste Générale n'existe pas.");}


}
