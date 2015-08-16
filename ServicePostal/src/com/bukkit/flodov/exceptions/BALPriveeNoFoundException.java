package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class BALPriveeNoFoundException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5460259778380424269L;

	public void toString(Player p){
		p.sendMessage("La boîte aux lettres n'existe pas.");
	}
}
