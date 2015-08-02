package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class PLNoTrouveeException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1334568151400695645L;
	
	public void toString(Player player){
		player.sendMessage("Aucune Poste Locale trouvée.");
	}
}
