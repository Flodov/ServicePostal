package com.bukkit.flodov.tasks;


import com.bukkit.flodov.ServicePostal.PosteGenerale;




public class PNJPosteGeneralRunnable implements Runnable {

	private PosteGenerale PG;
	private boolean stop;
	
	public PNJPosteGeneralRunnable(PosteGenerale PG){
		this.PG = PG;
		stop = false;
	}
	
	@Override
	public void run() {
		//Debut de la tournée
		
		
		
		//Iterator<PosteLocale> ite = PG.getReseau().iterator();
		//while(ite.hasNext()){
			
			//PosteLocale PL = ite.next();
			//PG.bouge(PL.getBoite().getLocation());
		if(PG.NpcDispo()  && !stop) PG.NpcInitTournee();
		

	}
	
	public void stop(){
		stop = true;
	}

}
