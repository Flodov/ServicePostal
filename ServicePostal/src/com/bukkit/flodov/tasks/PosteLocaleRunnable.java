package com.bukkit.flodov.tasks;



import com.bukkit.flodov.ServicePostal.PosteLocale;

public class PosteLocaleRunnable implements Runnable{

private PosteLocale PL;
private boolean stop;

	public PosteLocaleRunnable(PosteLocale PL){
		this.PL = PL;
		stop = false;
	}
	@Override
	public void run() {
		if(PL.NpcDispo()&&!stop) PL.NpcInitTournee();

	}
	
	public void stop(){
		stop = true;
	}

}
