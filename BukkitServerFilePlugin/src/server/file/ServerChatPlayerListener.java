package server.file;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerChatPlayerListener extends PlayerListener {
	public static Loader plugin;
	public FWrite writer = new FWrite();
	
	public ServerChatPlayerListener(Loader loader){
		plugin = loader;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		redo();
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		redo();
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		redo();
	}
	
	public void offline(){
		writer = new FWrite();
		writer.wipe();
		int port = Bukkit.getPort();
		writer.writeIP(getIP()+":"+port);
		writer.writeState("Offline");
		writer.writeName(Bukkit.getServerName());
		writer.writeAccountType(!Bukkit.getServer().getOnlineMode());
		writer.writePopulation(Bukkit.getServer().getOnlinePlayers().length+"/"+Bukkit.getServer().getMaxPlayers());
	}
	
	public void online(){
		writer = new FWrite();
		writer.wipe();
		int port = Bukkit.getPort();
		writer.writeIP(getIP()+":"+port);
		writer.writeState("Online");
		writer.writeName(Bukkit.getServerName());
		writer.writeAccountType(!Bukkit.getServer().getOnlineMode());
		writer.writePopulation(Bukkit.getServer().getOnlinePlayers().length+"/"+Bukkit.getServer().getMaxPlayers());
	}
	
	public void redo(){
		writer = new FWrite();
		writer.wipe();
		int port = Bukkit.getPort();
		writer.writeIP(getIP()+":"+port);
		writer.writeState("Online");
		writer.writeName(Bukkit.getServerName());
		writer.writeAccountType(!Bukkit.getServer().getOnlineMode());
		writer.writePopulation(Bukkit.getServer().getOnlinePlayers().length+"/"+Bukkit.getServer().getMaxPlayers());
	}
	
	public String getIP(){
		String ret = "";
		try{
			String url = "http://whatismyip.org";
	        URL addr = new URL(url); 
	        BufferedReader in = new BufferedReader(new InputStreamReader(addr.openStream())); 
	        String inputLine; 
	        while ((inputLine = in.readLine()) != null) { 
	        	ret = inputLine;
	        } 
	        in.close(); 
		}catch(Exception e){}
		return ret;
	}
}
