package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class ReseauExistantException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4047136947435878930L;

	public void toString(Player p){
		p.sendMessage("[ServicePostal]Le reseau doit être effacé avant.");
	}
}
