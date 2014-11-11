package channel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Chat extends JavaPlugin{
	
	public static Chat plugin;
	public final Logger logger = Logger.getLogger("Minecraft");

	public static int SAY_MAX_RANGE = 20; 
	public static int YELL_MAX_RANGE = 60; 
	public ServerPlayerListener playerListener = new ServerPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {	
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
		for(Player player : Bukkit.getOnlinePlayers()){
			File file = new File("plugins/channels/"+player.getName()+".PLAYER");
			if(!file.exists()){
				try{
					BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
					out.write("");
					out.close();
				}catch(Exception e){
					ChatLog.log_error(e.getMessage());
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		Player player = (Player) sender;
		if(cmd.equalsIgnoreCase("tell") || cmd.equalsIgnoreCase("msg")){
			cmd = "pm";
		}
		if(cmd.equalsIgnoreCase("reply")){
			cmd = "r";
		}
		if(cmd.equalsIgnoreCase("say")){
			Player players[] = Bukkit.getOnlinePlayers();
			boolean heard = false;
			for(Player online : players){
				Location location = player.getLocation();
				if(location.distance(online.getLocation()) < SAY_MAX_RANGE  && online != player){
					sayMessage(args, online, player);
					heard = true;
				}
			}
			sayMessage(args, player, player);
			if(!heard){
				sendPlayerMessage(player, "No one heard you.");
			}
			String message = "";
			for(String line : args){
				message = message + line + " ";
			}
			ChatLog.log_message(player.getName(), "[Channel: SAY] "+message+" (Location: "+player.getLocation().toString(), player.isOp());
		}else if(cmd.equalsIgnoreCase("yell")){
			Player players[] = Bukkit.getOnlinePlayers();
			boolean heard = false;
			for(Player online : players){
				Location location = player.getLocation();
				if(location.distance(online.getLocation()) < YELL_MAX_RANGE  && online != player){
					yellMessage(args, online, player);
					heard = true;
				}
			}
			yellMessage(args, player, player);
			if(!heard){
				sendPlayerMessage(player, "No one heard you.");
			}
			String message = "";
			for(String line : args){
				message = message + line + " ";
			}
			ChatLog.log_message(player.getName(), "[Channel: YELL] "+message+" (Location: "+player.getLocation().toString(), player.isOp());
		}else if(cmd.equalsIgnoreCase("local")){
			Player players[] = Bukkit.getOnlinePlayers();
			boolean heard = false;
			if(!getZone(player).equalsIgnoreCase("[none/unknown]") && getZone(player) != null && getZone(player) != ""){
				for(Player online : players){
					if(getZone(online).equalsIgnoreCase(getZone(player)) && online != player){
						localMessage(args, online, player);
						heard = true;
					}
				}
			}
			localMessage(args, player, player);
			if(!heard){
				sendPlayerMessage(player, "No one heard you.");
			}
			String message = "";
			for(String line : args){
				message = message + line + " ";
			}
			ChatLog.log_message(player.getName(), "[Channel: LOCAL] "+message+" (Location: "+getZone(player), player.isOp());
		}else if(cmd.equalsIgnoreCase("ch")){
			if(args.length < 2){
				sendPlayerMessage(player, "You haven't supplied enough arguments! /ch <channel> <message>");
			}else{
				if(isChannel(args[0])){
					boolean heard = false;
					String channel = args[0];
					String message = "";
					for(int i=1; i<args.length; i++){
						message = message + args[i] + " ";
					}
					if(hasChannel(channel, player)){
						for(Player to : Bukkit.getOnlinePlayers()){
							if(hasChannel(channel, to)){
								channelMessage(message, channel, to, player);
								heard = true;
							}
						}
					}
					if(!heard){
						sendPlayerMessage(player, "No one heard you.");
					}
					ChatLog.log_message(player.getName(), "[Channel: "+channel+"] "+message, player.isOp());
				}else{
					sendPlayerMessage(player, "Channel does not exist.");
				}
			}
		}else if(cmd.equalsIgnoreCase("createCH")){
			if(channelExists(args[0])){
				sendPlayerMessage(player, "Channel exists already! Sorry.");
			}else{
				if(args[1] != null){
					try{
						File chfile = new File("plugins/channels/"+args[0]+".PWCHANNEL");
						BufferedWriter out = new BufferedWriter(new FileWriter(chfile, true));
						out.write(args[1]);
						out.close();
					}catch (Exception e){
						ChatLog.log_error(e.getMessage());
					}
				}else{
					try{
						File chfile = new File("plugins/channels/"+args[0]+".CHANNEL");
						BufferedWriter out = new BufferedWriter(new FileWriter(chfile, true));
						out.write("");
						out.close();
					}catch (Exception e){
						ChatLog.log_error(e.getMessage());
					}
				}
				sendPlayerMessage(player, "Channel created!");
			}
		}else if(cmd.equalsIgnoreCase("leaveCH")){
			try{
				BufferedReader in = new BufferedReader(new FileReader("plugins/channels/"+player.getName()+".PLAYER"));
				String fileContents = "";
				String line;
				while((line = in.readLine()) != null){
					line = line.replaceAll("\\\r\\\n", "");
					if(!line.toLowerCase().startsWith(args[0].toLowerCase())){
						fileContents = fileContents + line + "\r\n";
					}
				}
				in.close();
				BufferedWriter out = new BufferedWriter(new FileWriter("plugins/channels/"+player.getName()+".PLAYER", false));
				out.write(fileContents);
				out.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			channelBroadcast(player, args[0], "has left the channel.");
		}else if(cmd.equalsIgnoreCase("joinCH")){
			String channel = args[0];
			if(isChannel(channel) && !isPWProtected(channel)){
				if(hasChannel(channel, player)){
					sendPlayerMessage(player, "You are already in this channel!");
				}else{
					try{
						File file = new File("plugins/channels/"+player.getName()+".PLAYER");
						BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
						out.write(channel+"\r\n");
						out.close();
					}catch (Exception e){
						ChatLog.log_error(e.getMessage());
					}
					channelBroadcast(player, channel, "has joined the channel.");
				}
			}else if(isChannel(channel) && isPWProtected(channel) && args[1] != null){
				String password = args[1];
				if(hasChannel(channel, player)){
					sendPlayerMessage(player, "You are already in this channel!");
				}else{
					if(checkChannelPassword(channel, password)){
						try{
							File file = new File("plugins/channels/"+player.getName()+".PLAYER");
							BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
							out.write(channel+"\r\n");
							out.close();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
						}
						channelBroadcast(player, channel, "has joined the channel.");
					}else{
						sendPlayerMessage(player, "Channel password incorrect. Remember: Passwords are case sensitive!");
					}
				}
			}else if(isChannel(channel)){
				sendPlayerMessage(player, "Channel password not suppiled, if you would like to be in the channel please aquire the password.");
			}else{
				sendPlayerMessage(player, "Channel does not exist. Maybe create it?");
			}
		}else if(cmd.equalsIgnoreCase("r")){
			Player from = player;
			String message = "";
			for(int i=0; i<args.length; i++){
				message = message + args[i] + " ";
			}
			Player to = getLastMessage(from);
			if(to != null){
				if(to.isOnline()){
					String afk = getAFK(to);
					to.sendMessage(ChatColor.DARK_PURPLE + "[PM] "+getMessage(message, from));
					from.sendMessage(ChatColor.DARK_PURPLE + "[PM] >> "+afk+" "+getMessage(message, to));
					pmLog(from, to);
				}else{
					sendPlayerMessage(from, to.getName()+" is not online.");
				}
				String toOp = "";
				String fromOp = "";
				if(to.isOp()){
					toOp = " [OP]";
				}
				if(from.isOp()){
					fromOp = " [OP]";
				}
				ChatLog.log_whisper(to.getName()+" [As "+getPrefix(getPerms(to))+"]"+toOp, from.getName()+" [As "+getPrefix(getPerms(from))+"]"+fromOp, message, 'a', 'b');
			}else{
				sendPlayerMessage(from, "You have no one to reply to!");
			}
		}else if(cmd.equalsIgnoreCase("pm")){
			Player from = player;
			String to_raw = args[0];
			String message = "";
			for(int i=1; i<args.length; i++){
				message = message + args[i] + " ";
			}
			List<Player> matches = Bukkit.matchPlayer(to_raw);
			Player to = matches.get(0);
			if(to.isOnline()){
				String afk = getAFK(to);
				to.sendMessage(ChatColor.DARK_PURPLE + "[PM] "+getMessage(message, from));
				from.sendMessage(ChatColor.DARK_PURPLE + "[PM] >> "+afk+" "+getMessage(message, to));
				pmLog(from, to);
			}else{
				sendPlayerMessage(from, to_raw+" is not online.");
			}
			String toOp = "";
			String fromOp = "";
			if(to.isOp()){
				toOp = " [OP]";
			}
			if(from.isOp()){
				fromOp = " [OP]";
			}
			ChatLog.log_whisper(to.getName()+" [As "+getPrefix(getPerms(to))+"]"+toOp, from.getName()+" [As "+getPrefix(getPerms(from))+"]"+fromOp, message, 'a', 'b');
		}else{
			sendPlayerMessage(player, "Help Menu: ");
			sendPlayerMessage(player, "/say  - Say something");
			sendPlayerMessage(player, "/yell  - Yell something");
			sendPlayerMessage(player, "/local  - Speak in an AMS channel");
			sendPlayerMessage(player, "/pm <player> <message>  - Private message a player");
			sendPlayerMessage(player, "/createCH <channel name> - Create channel (no password)");
			sendPlayerMessage(player, "/createCH <channel name> <password> - Create channel (with password)");
			sendPlayerMessage(player, "/joinCH <channel name>  - Join a channel");
			sendPlayerMessage(player, "/joinCH <channel name> <password> - Join a password-protected channel");
			sendPlayerMessage(player, "/leaveCH <channel name>  - Leave channel");
			sendPlayerMessage(player, "/CHHelp  - Shows help menu");
		}
		return false;
	}		
	
	public void sayMessage(String[] incmessage, Player to, Player from) {
		String message = "";
		for(String line : incmessage){
			message = message + line + " ";
		}
		to.sendMessage(ChatColor.WHITE + "[SAY] "+getMessage(message, from));
	}	
	
	public void yellMessage(String[] incmessage, Player to, Player from) {
		String message = "";
		for(String line : incmessage){
			message = message + line + " ";
		}
		to.sendMessage(ChatColor.RED + "[YELL] "+getMessage(message, from));
	}
	
	public void localMessage(String[] incmessage, Player to, Player from) {
		String message = "";
		for(String line : incmessage){
			message = message + line + " ";
		}
		to.sendMessage(ChatColor.YELLOW + "[LOCAL] "+getMessage(message, from));
	}
	
	public void channelMessage(String[] incmessage, String channel, Player to, Player from) {
		String message = "";
		for(String line : incmessage){
			message = message + line + " ";
		}
		to.sendMessage(ChatColor.YELLOW + "["+channel+"] "+getMessage(message, from));
	}
	
	public void channelMessage(String incmessage, String channel, Player to, Player from) {
		String message = incmessage;
		to.sendMessage(ChatColor.YELLOW + "["+channel+"] "+getMessage(message, from));
	}
	
	public boolean isPWProtected(String channel){
		boolean pw = false;
		File file = new File("plugins/channels/"+channel+".PWCHANNEL");
		if(file.exists()){
			pw = true;
		}
		return pw;
	}
	
	public boolean checkChannelPassword(String channel, String password){
		boolean valid = false;
		try{
			File file = new File("plugins/channels/"+channel+".PWCHANNEL");
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = in.readLine();
			line = line.replaceAll("\\\r\\\n", "");
			if(line.equals(password)){
				valid = true;
			}
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return valid;
	}
	
	public boolean isChannel(String channel){
		return channelExists(channel);
	}
	
	public void channelBroadcast(Player from, String channel, String message){
		for(Player player : Bukkit.getOnlinePlayers()){
			if(hasChannel(channel, player)){
				player.sendMessage(ChatColor.YELLOW + "["+channel+"] "+getMessage(ChatColor.YELLOW + message, from));
			}
		}
	}
	
	public void sendPlayerMessage(Player player, String msg){
		player.sendMessage(ChatColor.GOLD + "[CHAT] " + ChatColor.AQUA + msg);
	}
	
	public Player getLastMessage(Player sender){
		File log = new File("plugins/PM/"+sender.getName()+".PLAYER");
		if(log.exists()){
			try{
				BufferedReader in = new BufferedReader(new FileReader(log));
				String line = in.readLine();
				in.close();
				return Bukkit.getPlayer(line);
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
				return null;
			}
		}else{
			return null;
		}
	}
	
	public void pmLog(Player sender, Player target){
		File log = new File("plugins/PM/"+target.getName()+".PLAYER");
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(log, false));
			out.write(sender.getName());
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public boolean channelExists(String name){
		boolean exists = false;
		File channel = new File("plugins/channels/"+name+".CHANNEL");
		File pwchannel = new File("plugins/channels/"+name+".PWCHANNEL");
		if(channel.exists() || pwchannel.exists()){
			exists = true;
		}
		return exists;
	}
	
	public boolean hasChannel(String channel, Player player){
		boolean hasChannel = false;
		File file = new File("plugins/channels/"+player.getName()+".PLAYER");
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while((line = in.readLine()) != null){
				if(line.equalsIgnoreCase(channel)){
					hasChannel = true;
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		if(player.isOp()){
			hasChannel = true;
		}
		return hasChannel;
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
	
	public String getMessage(String mess, Player player) {
		boolean colon = true;
		int perms = getPerms(player);
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
			prefixColor = ChatColor.getByCode(0xB);
			nameColor = ChatColor.getByCode(0xA);
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
	
	public int getPerms(Player player){
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
		return perms;
	}
	
	public static String getZone(Player player){
		String zone = "";
		String world = player.getWorld().getName();
		Location location = player.getLocation();
		File dir = new File("plugins/AMS/"+world+"/");
		File files[] = dir.listFiles();
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split(world);
			String name = parts[parts.length-1];
			parts = name.split("\\.\\.");
			double X_MIN = Double.parseDouble(parts[0]);
			double Y_MIN = Double.parseDouble(parts[1]);
			double X_MAX = Double.parseDouble(parts[2]);
			double Y_MAX = Double.parseDouble(parts[3]);
			if(X_MIN>X_MAX){
				double temp = X_MAX;
				X_MAX = X_MIN;
				X_MIN = temp;
			}
			if(Y_MIN>Y_MAX){
				double temp = Y_MAX;
				Y_MAX = Y_MIN;
				Y_MIN = temp;
			}
			double lx = location.getX();
			double ly = location.getZ();
			if(lx>X_MIN && lx<X_MAX && ly>Y_MIN && ly<Y_MAX){
				zone = parts[4];
				zone = zone.replaceAll("\\.ZONE", "");
				break;
			}
		}
		return zone;
	}
	
	public String getAFK(Player player){
		File file = new File("plugins/CurrentlyAFK/"+player.getName()+".PLAYER");
		if(file.exists()){
			return ChatColor.DARK_RED + "<AFK>";
		}else{
			return "";
		}
	}
}
