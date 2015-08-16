package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class SessionPathMutexException extends ServicePostalException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2567479818516714058L;


	public void toString(Player p){
		p.sendMessage("[ServicePostal] Quelqu'un réalise déjà ce chemin.");
	}
}
