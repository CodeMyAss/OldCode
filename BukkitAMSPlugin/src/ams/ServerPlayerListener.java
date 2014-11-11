package ams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

//import ChatLogger.ChatLog;

public class ServerPlayerListener extends PlayerListener{
	public static AMSMain plugin;
	
	public ServerPlayerListener(AMSMain ams){
		plugin = ams;
	}
	
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
		if(MultiFunction.isRestricted(event.getPlayer().getLocation()) && !MultiFunction.hasPermission(event.getPlayer(), event.getPlayer().getLocation(), true)){
			event.setCancelled(true);
			if(event.getPlayer() != null){
				Player player = event.getPlayer();
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You are not allowed to place in " + MultiFunction.getZone(player) + ".");
				MultiFunction.reportPlayer(player);
			}
		}
	}
	
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(MultiFunction.isRestricted(player.getLocation())){
			if(!playerRegistered(player, MultiFunction.getZone(player))){
				if(!MultiFunction.hasPermission(player, event.getPlayer().getLocation(), true)){
					player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Welcome to " + MultiFunction.getZone(player) + ", you are unable to build or edit here. Please leave the area to build!");
				}else{
					player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.AQUA + "Welcome to " + MultiFunction.getZone(player) + ", You are able to build here!");
				}
				playerRegister(player, MultiFunction.getZone(player));
			}
		}else{
			String lastzone = playerRegisteredZone(player);
			if(!lastzone.equals("[NONE/UNKNOWN]") && lastzone != null){
				playerUnregister(player);
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.AQUA + "You have left the zone " + lastzone + " and are now able to build!");
			}
		}
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event){
		Player player = event.getPlayer();
		if(!MultiFunction.getZone(player).equalsIgnoreCase(MultiFunction.getZone(event.getTo()))){
			playerRegister(player, MultiFunction.getZone(event.getTo()));
		}
	}
	
	public boolean playerRegistered(Player player, String zone) {
		boolean registered = false;
		try{
			File file = new File ("plugins/AMS_RegisteredPlayers/"+player.getName()+".PLAYER");
			if(file.exists()){
				registered = true;
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line = in.readLine();
				if(line != "" && line != null){
					line = line.replaceAll("\\\\r\\\\n", "");
				}else{
					line = "[NONE/UNKNOWN]";
				}
				in.close();
				if(!line.equalsIgnoreCase(zone) && !line.equalsIgnoreCase("[none/unknown]")){
					BufferedWriter out = new BufferedWriter(new FileWriter("notifications.txt", true));
					out.write("[AUTO] Player "+player.getName()+" registered to zone \""+line+"\" but was found in \""+zone+"\".\r\n");
					out.close();
					playerRegister(player, zone);
				}
			}
		}catch (Exception e){
			ChatLog.log_error(e.toString());
			e.printStackTrace();
		}
		return registered;
	}
	
	public void playerRegister(Player player, String zone){
		if(!MultiFunction.hasPermission(player)){
			File file = new File ("plugins/AMS_RegisteredPlayers/"+player.getName()+".PLAYER");
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
				out.write(zone);
				out.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
		plugin.loadOP();
	}
	
	public String playerRegisteredZone(Player player){
		String zone = "";
		try{
			File file = new File ("plugins/AMS_RegisteredPlayers/"+player.getName()+".PLAYER");
			if(file.exists()){
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line = in.readLine();
				in.close();
				if(line == "" || line == null){
					line = "[NONE/UNKNOWN]";
				}
				zone = line;
			}else{
				zone = "[NONE/UNKNOWN]";
			}
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return zone;
	}
	
	public void playerUnregister(Player player){
		File file = new File ("plugins/AMS_RegisteredPlayers/"+player.getName()+".PLAYER");
		if(file.exists()){
			file.delete();
		}
	}
}
