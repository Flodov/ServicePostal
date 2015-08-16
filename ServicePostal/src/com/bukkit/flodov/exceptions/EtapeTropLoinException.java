package com.bukkit.flodov.exceptions;

import org.bukkit.entity.Player;

public class EtapeTropLoinException extends ServicePostalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1647965328262211505L;

	public void toString(Player p){
		p.sendMessage("[ServicePostal] Etape trop lointaine ... Rapprochez vous de la précédente.");
	}
}
