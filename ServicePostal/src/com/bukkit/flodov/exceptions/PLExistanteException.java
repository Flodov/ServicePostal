package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;


public class PLExistanteException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PLExistanteException() {
		// TODO Auto-generated constructor stub
	}
	public void toString(Player player){player.sendMessage("La Poste Locale existe déjà.");}


}
