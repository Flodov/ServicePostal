package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class BALPubliqueExistanteException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5543528540179491129L;

	public void toString(Player player){
		player.sendMessage("La BAL Publique existe déjà pour cette poste.");
	}
}
