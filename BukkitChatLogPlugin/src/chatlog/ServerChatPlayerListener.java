package chatlog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//import ChatLogger.ChatLog;

public class ServerChatPlayerListener extends PlayerListener {
	public static Grabber plugin;
	
	public ServerChatPlayerListener(Grabber grabber){
		plugin = grabber;
	}
	
	public void onPlayerChat(PlayerChatEvent event){
		Player player = event.getPlayer();
		String message = event.getMessage();
		File file = new File("plugins/AMS_GrantedPlayers/"+player.getName()+".PLAYER");
		int perms = 0;
		if(!player.isOp()){
			if(file.exists()){
				try{
					BufferedReader in = new BufferedReader(new FileReader(file));
					String line;
					while((line = in.readLine()) != null){
						if(line.equalsIgnoreCase("global")){
							perms = 1;
						}
					}
					in.close();
				}catch(Exception e){
					ChatLog.log_error(e.getMessage());
				}
			}else{
				perms = -1;
			}
		}else{
			perms = 3;
			try{
				BufferedReader in = new BufferedReader(new FileReader("admins.txt"));
				String line;
				while((line = in.readLine()) != null){
					if(line.toLowerCase().startsWith(player.getName().toLowerCase())){
						perms = 2;
					}
				}
				in.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
		String prefix = "";
		switch(perms){
		case -1:
			prefix = "[As Guest]";
			break;
		case 0:
			prefix = "[As Guest]";
			break;
		case 1:
			prefix = "[As Builder]";
			break;
		case 2:
			prefix = "[As Admin]";
			break;
		case 3:
			prefix = "[As OP]";
			break;
		default:
			prefix = "[As Broken]";
		}
		String parts[] = message.toLowerCase().split("#suggest");
		if(parts.length > 1 && message.toLowerCase().startsWith("#suggest")){
			String mess = player.getName() + ": ";
			for(int i=1;i<parts.length;i++){
				mess = mess + parts[i];
			}
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter("suggestions.txt", true));
				writer.write(mess + "\r\n");
				writer.close();
				player.sendMessage(ChatColor.GOLD + "[Suggestions] " + ChatColor.GREEN + "Thank you for your suggestion, it has been recorded.");
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
				player.sendMessage(ChatColor.GOLD + "[Suggestions] " + ChatColor.RED + "Something went wrong in recording your suggestion, Sorry!");
			}
		}else if (message.toLowerCase().startsWith("#mob")){
			try{
				String mess = "Reported not-allowed mob spawn by "+player.getName()+" at "+player.getLocation().toString();
				BufferedWriter writer = new BufferedWriter(new FileWriter("MOB_SPAWNS.txt", true));
				writer.write(mess + "\r\n");
				writer.close();
				player.sendMessage(ChatColor.GOLD + "[Mobs] " + ChatColor.GREEN + "Thank you, we hope to get it fixed soon!");
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
				player.sendMessage(ChatColor.GOLD + "[Mobs] " + ChatColor.RED + "Something broke! Sorry...");
			}
		}else if (message.toLowerCase().startsWith("#bug")){
			try{
				String rest = "";
				parts = message.toLowerCase().split("#bug");
				for(int i=1;i<parts.length;i++){
					rest = rest + parts[i];
				}
				String mess = "Reported bug by "+player.getName()+" : "+rest;
				BufferedWriter writer = new BufferedWriter(new FileWriter("BUG_REPORTS.txt", true));
				writer.write(mess + "\r\n");
				writer.close();
				player.sendMessage(ChatColor.GOLD + "[BUGS] " + ChatColor.GREEN + "Thank you, we hope to get it fixed soon!");
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
				player.sendMessage(ChatColor.GOLD + "[BUGS] " + ChatColor.RED + "Something broke! Sorry... (*cough* fail)");
			}
		}else if (message.toLowerCase().startsWith("#requestAMS")){
			try{
				DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
				Date date = new Date();
		      	String timestamp = dateFormat.format(date);
				String mess = "["+timestamp+"] AMS Request by "+player.getName()+" at "+player.getLocation().toString();
				BufferedWriter writer = new BufferedWriter(new FileWriter("AMS_REQUESTS.txt", true));
				writer.write(mess + "\r\n");
				writer.close();
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Your request has been logged. Please read the information on mc.turt2live.com/ams");
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Something broke! Sorry!");
			}
		}
		ChatLog.log_message(player.getName(), prefix+" "+message, player.isOp());
	}
	
	public void onPlayerLogin(PlayerLoginEvent event){
		Player player = event.getPlayer();
		ChatLog.log_server_message(player.getName()+" has just logged in. (IP: "+player.getAddress()+")");
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		ChatLog.log_server_message(player.getName()+" has just joined. (IP: "+player.getAddress()+")");
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
		ChatLog.log_server_message(player.getName()+" has just been kicked for "+event.getReason()+". (IP: "+player.getAddress()+")");
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		ChatLog.log_server_message(player.getName()+" has just left the server. (IP: "+player.getAddress()+")");
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		Player player = event.getPlayer();
		ChatLog.log_server_message(player.getName()+" has sent the command \""+event.getMessage()+"\". (IP: "+player.getAddress()+")");
	}
}
