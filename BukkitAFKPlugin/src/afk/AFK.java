package afk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AFK extends JavaPlugin{
	
	public static AFK plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ServerPlayerListener playerListener = new ServerPlayerListener(this);

	public static int tid = 0;
	public static long interval = 60; //Seconds
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {	
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal, this);
		
		File afkFromPrevious = new File("plugins/CurrentlyAFK/");
		File afkFromPreviousListed[] = afkFromPrevious.listFiles();
		for(File file : afkFromPreviousListed){
			file.delete();
		}
		
		tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run(){
				try{
					checkAFK();
				}catch (Exception e){
					ChatLog.log_error(e.getMessage());
				}
			}
		}, 0, interval*20);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		Player player = (Player) sender;
		if(cmd.equalsIgnoreCase("afk")){
			if(args.length > 0){
				setAFK(player, args);
			}else{
				setAFK(player);
			}
		}else if(cmd.equalsIgnoreCase("whoafk")){
			String message = "Currently AFK Players: "+allAFK();
			sendPlayerMessage(player, message);
		}else{
			sendPlayerMessage(player, "Help Menu: ");
			sendPlayerMessage(player, "/afk  - Turn on/off AFK mode");
			sendPlayerMessage(player, "/afk <reason>  - Turn on AFK mode for a reason.");
			sendPlayerMessage(player, "Tip: You can move to clear AFK status.");
		}
		return false;
	}
	
	public void checkAFK(){
		File dir = new File("plugins/AFK/");
		File files[] = dir.listFiles();
		Player players[] = Bukkit.getOnlinePlayers();
		for(Player player : players){
			File file = new File("plugins/AFK/"+player.getName()+".PLAYER");
			try{
				long lastActive = 0;
				if(file.exists()){
					BufferedReader in = new BufferedReader(new FileReader(file));
					lastActive = Long.parseLong(in.readLine());
					in.close();
				}
				lastActive = lastActive + 60000;
				BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
				out.write(lastActive+"");
				out.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
		for(File file : files){
			String parts[] = file.toString().split("AFK");
			String filepart = parts[parts.length-1];
			filepart = filepart.replaceAll("\\\\", "");
			filepart = filepart.replaceAll("/", "");
			String filename = filepart.replaceAll(".PLAYER", "");
			if(inArray(players, filename)){
				try{
					BufferedReader in = new BufferedReader(new FileReader(file));
					long lastActive = Long.parseLong(in.readLine());
					in.close();
					if(lastActive >= 1800000){
						Bukkit.getPlayer(filename).kickPlayer("AFK for 30+ minutes");
						clearAFK(Bukkit.getPlayer(filename));
					}else if(lastActive >= 300000){
						setAFK(Bukkit.getPlayer(filename));
					}
				}catch (Exception e){
					ChatLog.log_error(e.getMessage());
				}
			}else{
				file.delete();
			}
		}
	}
	
	public boolean isAFK(Player player){
		File file = new File("plugins/CurrentlyAFK/"+player.getName()+".PLAYER");
		if(file.exists()){
			return true;
		}else{
			return false;
		}
	}
	
	public String allAFK(){
		String afks = "";
		File dir = new File("plugins/CurrentlyAFK/");
		File files[] = dir.listFiles();
		for(File file : files){
			String parts[] = file.toString().split("AFK");
			String filepart = parts[parts.length-1];
			filepart = filepart.replaceAll("\\\\", "");
			filepart = filepart.replaceAll("/", "");
			String filename = filepart.replaceAll(".PLAYER", "");
			afks = afks + filename + ", ";
		}
		return afks;
	}
	
	public boolean inArray(Player array[], String search){
		for(Player player : array){
			if(player.getName().equalsIgnoreCase(search)){
				return true;
			}
		}
		return false;
	}
	
	public void setAFK(Player player){
		String message = "Away From Keyboard";
		if(!isAFK(player)){
			try{
				File file = new File("plugins/CurrentlyAFK/"+player.getName()+".PLAYER");
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				out.write(message);
				out.close();
				ChatLog.log_warning(player.getName() + " went AFK. ("+message+")");
				sendGlobalMessage("is now AFK ("+message+").", player);
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
	}
	
	public void setAFK(Player player, String why[]){
		String message = "";
		for(String word : why){
			message = message + word + " ";
		}
		if(!isAFK(player)){
			try{
				File file = new File("plugins/CurrentlyAFK/"+player.getName()+".PLAYER");
				BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
				out.write(message);
				out.close();
				ChatLog.log_warning(player.getName() + " went AFK. ("+message+")");
				sendGlobalMessage("is now AFK ("+message+").", player);
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
	}
	
	public void clearAFK(Player player){
		File file = new File("plugins/CurrentlyAFK/"+player.getName()+".PLAYER");
		if(file.exists()){
			file.delete();
			ChatLog.log_warning(player.getName() + " is no longer AFK.");
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/AFK/"+player.getName()+".PLAYER"), false));
				out.write("0");
				out.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			sendGlobalMessage("is no longer AFK.", player);
		}
	}
	
	public void sendPlayerMessage(Player player, String message){
		player.sendMessage(ChatColor.GOLD + "[AFK] " + ChatColor.AQUA + message);
	}
	
	public void sendGlobalMessage(String message, Player isAFK){
		Player players[] = Bukkit.getOnlinePlayers();
		for(Player player : players){
			player.sendMessage(getMessage(message, isAFK));
		}
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
	
	public String getMessage(String mess, Player player) {
		boolean colon = false;
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

}
