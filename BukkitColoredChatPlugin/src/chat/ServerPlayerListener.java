package chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

//import ChatLogger.ChatLog;

public class ServerPlayerListener extends PlayerListener{
	public static AddColor plugin;
	
	public ServerPlayerListener(AddColor ams){
		plugin = ams;
	}
	
	public void onPlayerChat(PlayerChatEvent event){
		Player player = event.getPlayer();
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
		String output = getMessage(perms, true, event.getMessage(), player);
		event.setFormat(output);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.isOp()){
			player.setSleepingIgnored(true);
		}
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
		String output = getMessage(perms, false, "has joined.", player);
		String prefix = getPrefix(perms);
		if(prefix.equalsIgnoreCase("admin")){
			player.setPlayerListName(ChatColor.getByCode(0x9) + player.getName());
		}else if(prefix.equalsIgnoreCase("builder")){
			player.setPlayerListName(ChatColor.getByCode(0xB) + player.getName());
		}else if(prefix.equalsIgnoreCase("guest")){
			player.setPlayerListName(ChatColor.getByCode(0x7) + player.getName());
		}else if(prefix.equalsIgnoreCase("mod")){
			player.setPlayerListName(ChatColor.getByCode(0xA) + player.getName());
		}else{
			player.setPlayerListName(ChatColor.RED + player.getName());
		}
		event.setJoinMessage(output);
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
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
		String output = getMessage(perms, false, "has been booted.", player);
		event.setLeaveMessage(output);
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
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
		String output = getMessage(perms, false, "has rage quit.", player);
		event.setQuitMessage(output);
	}

	public String getMessage(int perms, boolean colon, String mess, Player player) {

		ChatColor chatColor = ChatColor.AQUA;
		ChatColor nameColor = ChatColor.AQUA;
		ChatColor bracketColor = ChatColor.AQUA;
		ChatColor colonColor = ChatColor.AQUA;
		ChatColor prefixColor = ChatColor.AQUA;
		ChatColor messageColor = ChatColor.AQUA;
		String prefix = "";
		switch(perms){
		case -1:
			bracketColor = ChatColor.getByCode(0x8);
			prefixColor = ChatColor.getByCode(0x7);
			nameColor = ChatColor.getByCode(0x7);
			colonColor = ChatColor.getByCode(0xF);
			chatColor = ChatColor.getByCode(0xF);
			messageColor = ChatColor.getByCode(0xE);
			prefix = "Guest";
			break;
		case 0:
			bracketColor = ChatColor.getByCode(0x8);
			prefixColor = ChatColor.getByCode(0x7);
			nameColor = ChatColor.getByCode(0x7);
			colonColor = ChatColor.getByCode(0xF);
			chatColor = ChatColor.getByCode(0xF);
			messageColor = ChatColor.getByCode(0xE);
			prefix = "Guest";
			break;
		case 1:
			bracketColor = ChatColor.getByCode(0x6);
			prefixColor = ChatColor.getByCode(0xB);
			nameColor = ChatColor.getByCode(0xA);
			colonColor = ChatColor.getByCode(0xF);
			chatColor = ChatColor.getByCode(0xF);
			messageColor = ChatColor.getByCode(0xE);
			prefix = "Builder";
			break;
		case 2:
			bracketColor = ChatColor.getByCode(0x4);
			prefixColor = ChatColor.getByCode(0x9);
			nameColor = ChatColor.getByCode(0xC);
			colonColor = ChatColor.getByCode(0xF);
			chatColor = ChatColor.getByCode(0xF);
			messageColor = ChatColor.getByCode(0xE);
			prefix = "Admin";
			break;
		case 3:
			bracketColor = ChatColor.getByCode(0x2);
			prefixColor = ChatColor.getByCode(0xA);
			nameColor = ChatColor.getByCode(0xB);
			colonColor = ChatColor.getByCode(0xF);
			chatColor = ChatColor.getByCode(0xF);
			messageColor = ChatColor.getByCode(0xE);
			prefix = "Mod";
			break;
		default:
			chatColor = ChatColor.BLUE;
			nameColor = ChatColor.BLUE;
			colonColor = ChatColor.BLUE;
			bracketColor = ChatColor.BLUE;
			prefixColor = ChatColor.BLUE;
			messageColor = ChatColor.BLUE;
			prefix = "Broken";
		}
		String name = player.getName();
		String colonMsg = ":";
		String msgColorOption = "";
		if(!colon){
			colonMsg = "";
			msgColorOption = messageColor+"";
		}
		String output = bracketColor+"["+prefixColor+prefix+bracketColor+"]"+nameColor+" "+name+colonColor+colonMsg+" "+chatColor+msgColorOption+mess;
		return output;
	} 
	
	public String getPrefix(int perms){
		switch(perms){
		case -1:
			return "Guest";
		case 0:
			return "Guest";
		case 1:
			return "Builder";
		case 2:
			return "Admin";
		case 3:
			return "Mod";
		default:
			return "Broken";
		}
	}
}
