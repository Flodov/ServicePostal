package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class NoChestException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoChestException(){
			}
	public void toString(Player player){player.sendMessage("Aucun coffré pointé.");}

}
