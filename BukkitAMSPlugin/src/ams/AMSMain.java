package ams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Vector;
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

//import ChatLogger.ChatLog;


public class AMSMain extends JavaPlugin{
	public static AMSMain plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final ServerBlockListener blockListener = new ServerBlockListener(this);
	public final ServerPlayerListener playerListener = new ServerPlayerListener(this);
		
	public Vector<String> ams = new Vector<String>();
	public boolean locked = false;
	public String lockedBy = "";
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		Bukkit.setSpawnRadius(1);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_SPREAD, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Highest, this);
		//pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this.playerListener, Event.Priority.Highest, this);
		
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
		loadOP();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		if(player.isOp()){
			if(commandLabel.equalsIgnoreCase("ams")){
				if(args.length == 0){
					listAMS(player);
				}else{
					if(args[0].equalsIgnoreCase("set") && MultiFunction.isAdmin(player)){
						if(args[1].equalsIgnoreCase("a")){
							if(!isLocked(player)){
								if(ams.size() == 0){
									locked = true;
									lockedBy = player.getName();
									ams = new Vector<String>();
									Location location = player.getLocation();
									ams.add(""+location.getX()+".."+location.getZ());
									player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Point A set!");
								}else{
									player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Point A defined already! (/ams info)");
								}
							}else{
								player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Sorry! " + lockedBy + " is currently creating an AMS zone!");
							}
						}else if(args[1].equalsIgnoreCase("b")){
							if(!isLocked(player)){
								if(ams.size() == 1){
									Location location = player.getLocation();
									ams.add(""+location.getX()+".."+location.getZ());
									player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Point B set!");
								}else{
									player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Either point B is set or point A is not yet defined! (/ams info)");
								}
							}else{
								player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Sorry! " + lockedBy + " is currently creating an AMS zone!");
							}
						}else{
							player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You misstyped or something went wrong. Try /ams help");
						}
					}else if(args[0].equalsIgnoreCase("save") && MultiFunction.isAdmin(player)){
						if(!isLocked(player)){
							if(args.length > 1){
								saveAMS(player, args);
							}else{
								player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "No save name provided. Try again!");
							}
						}else{
							player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Sorry! " + lockedBy + " is currently making a new AMS zone!");
						}
					}else if(args[0].equalsIgnoreCase("list")){
						listAMS(player);
					}else if(args[0].equalsIgnoreCase("info")){
						infoAMS(player);
					}else if(args[0].equalsIgnoreCase("grant")){
						grantAMS(player, args);
					}else if(args[0].equalsIgnoreCase("deny")){
						denyAMS(player, args);
					}else if(args[0].equalsIgnoreCase("loadop") && MultiFunction.isAdmin(player)){
						loadOP();
					}else if(args[0].equalsIgnoreCase("remove") && MultiFunction.isAdmin(player)){
						if(args.length > 1){
							removeByNameAMS(args[1], player);
						}else{
							removeByPositionAMS(player);
						}
					}else if(args[0].equalsIgnoreCase("help")){
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "AMS Help:");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "To make a new zone:");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams set a  - Sets the current point as point A (FIRST)");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams set b  - Sets the current point as point B (SECOND)");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams save <Zone Name>  - Sets the zone name and saves it (THIRD)");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Other Stuff:");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams OR /ams list  - List all zones");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams remove <Zone Name:OPTIONAL>  - Removes zone (either by name or current position)");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams info  - Shows current save info");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams grant <player> <zone>  - Grants a player to build in <zone>");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "/ams deny <player> <zone>  - Denies a player to build in <zone> (By default ALL zones are denied)");
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "NOTE: Areas are proccessed in ALPHABETIC order (zone 'A' will be checked before 'B')");
					}else{
						player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "AMS is broken, or we haven't given you admin rights. (Think it's broken? Type #bug <description>)");
					}
				}
			}else if(commandLabel.equalsIgnoreCase("claim")){
				String zone = MultiFunction.getZone(player);
				if(!MultiFunction.isClaimed(zone, player)){
					MultiFunction.claim(zone, player);
					player.sendMessage(ChatColor.GOLD +"[AMS] "+ChatColor.GREEN+"You have claimed this plot!");
				}else{
					player.sendMessage(ChatColor.GOLD+"[AMS] "+ChatColor.RED+"This zone cannot be claimed!");
				}
			}
		}else{
			player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You are not aloud to do that.");
		}
		return false;
	}
	
	public boolean isLocked(Player player){
		if(locked == true){
			if(lockedBy.equalsIgnoreCase(player.getName())){
				return false;
			}else{
				return true;
			}
		}else{
			return false;
		}
	}
	
	public void grantAMS(Player op, String args[]){
		if(args.length < 3){
			op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Not enough arguments: /ams grant <player> <zone>");
		}else{
			String player = args[1];
			String zone = "";
			for(int i=2;i<args.length;i++){
				zone = zone + args[i];
			}
			try{
				File file = new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER");
				boolean zoneAssigned = false;
				if(file.exists()){
					BufferedReader in = new BufferedReader(new FileReader(file));
					String line;
					while((line = in.readLine()) != null){
						line = line.replaceAll("\\\\r\\\\n", "");
						String parts[] = line.split("\\.");
						if(line.equalsIgnoreCase("global")){
							op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Player \"" + player + "\" has been already granted GLOBAL.");
							zoneAssigned = true;
							break;
						}else if(parts[0].equalsIgnoreCase(zone) && Bukkit.getPlayer(player).getWorld().getName().equalsIgnoreCase(parts[1])){
							op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Player \"" + player + "\" has been already granted \""+zone+"\".");
							zoneAssigned = true;
							break;
						}
					}
					in.close();
				}
				if(!zoneAssigned){
					if(!zone.equalsIgnoreCase("global")){
						BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
						out.write(zone+"."+op.getWorld().getName());
						out.close();
						op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "You have granted \""+player+"\" to build in zone \""+zone+"\".");
					}else{
						if(MultiFunction.isAdmin(op)){
							BufferedWriter out2 = new BufferedWriter(new FileWriter(file, false));
							out2.write("GLOBAL");
							out2.close();
							op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "You have granted \""+player+"\" to build in zone \""+zone+"\".");
						}else{
							op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Sorry, you must be an admin to assign a builder!");
						}
					}
					Player online[] = Bukkit.getOnlinePlayers();
					for(Player ponline : online){
						if(ponline.getName().equalsIgnoreCase(player)){
							ponline.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.AQUA + op.getName() + " has allowed you to build in zone \""+zone+"\". Enjoy!");
							break;
						}
					}
				}
			}catch(Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
	}
	
	public void denyAMS(Player op, String args[]){
		if(args.length < 3){
			op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Not enough arguments: /ams deny <player> <zone>");
		}else{
			String player = args[1];
			String zone = "";
			for(int i=2;i<args.length;i++){
				zone = zone + args[i];
			}
			try{
				File file = new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER");
				boolean zoneAssigned = false;
				Vector<String> zones = new Vector<String>();
				if(zone.equalsIgnoreCase("global") && !MultiFunction.isAdmin(op)){
					op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Sorry, you must be an admin to assign a builder!");
					return;
				}
				if(file.exists()){
					BufferedReader in = new BufferedReader(new FileReader(file));
					String line;
					while((line = in.readLine()) != null){
						line = line.replaceAll("\\\\r\\\\n", "");
						String parts[] = line.split("\\.");
						if(line.equalsIgnoreCase("global") && !zone.equalsIgnoreCase("global")){
							op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Player \"" + player + "\" has been already granted GLOBAL. You must deny GLOBAL to remove this.");
							break;
						}else if(line.equalsIgnoreCase("global") && zone.equalsIgnoreCase("global")){
							zoneAssigned = true;
						}else if(!parts[0].equalsIgnoreCase(zone) && Bukkit.getPlayer(player).getWorld().getName().equalsIgnoreCase(parts[1])){
							zones.add(line);
						}else if(parts[0].equalsIgnoreCase(zone) && Bukkit.getPlayer(player).getWorld().getName().equalsIgnoreCase(parts[1])){
							zoneAssigned = true;
						}
					}
					in.close();
				}
				if(!zoneAssigned && !zone.equalsIgnoreCase("global")){
					op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Zone \"" + zone + "\" was not assigned to \""+player+"\".");
				}else{
					try{
						BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
						for(int i=0;i<zones.size();i++){
							out.write(zones.get(i)+"\r\n");
						}
						out.close();
					}catch (Exception e){
						ChatLog.log_error(e.getMessage());
					}	
					op.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Zone \"" + zone + "\" was removed from \""+player+"\"'s permissions.");
					Player online[] = Bukkit.getOnlinePlayers();
					for(Player ponline : online){
						if(ponline.getName().equalsIgnoreCase(player)){
							ponline.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + op.getName() + " has removed you from building in zone \""+zone+"\".");
							break;
						}
					}
				}
			}catch(Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
	}
	
	public void listAMS(Player player){
		File dir = new File("plugins/AMS/"+player.getWorld().getName()+"/");
		File files[] = dir.listFiles();
		player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + files.length + " zone(s) found! (World: "+player.getWorld().getName()+")");
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split(player.getWorld().getName());
			String name = parts[parts.length-1];
			parts = name.split("\\.\\.");
			parts = parts[4].split("\\.");
			String zonename = parts[0];
			player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + zonename);
		}
	}
	
	public void saveAMS(Player player, String rawname[]){
		String name = "";
		for(int i=1;i<rawname.length;i++){
			name = name+rawname[i]+" ";
		}
		if(ams.size() == 2){
			String filename = ams.get(0)+".."+ams.get(1)+".."+name+".ZONE";
			if(!isZone(name, player)){
				try{
					BufferedWriter out = new BufferedWriter(new FileWriter("plugins/AMS/"+player.getWorld().getName()+"/"+filename));
					out.write("Zone set by "+player.getName());
					out.close();
					player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Zone saved!");
					locked = false;
					lockedBy = "";
					ams = new Vector<String>();
				}catch(Exception e){
					ChatLog.log_error(e.getMessage());
				}
			}else{
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Zone name already in use, zone not saved! (/ams list)");
			}
			
		}else{
			player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "AMS Zone not saved! You haven't supplied point B yet!");
		}
	}
	
	public void removeByNameAMS(String zonename, Player player){
		boolean zonefound = false;
		File dir = new File("plugins/AMS/"+player.getWorld().getName()+"/");
		File files[] = dir.listFiles();
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split("AMS");
			String name = parts[parts.length-1];
			parts = name.split("\\.\\.");
			if(parts[4].equalsIgnoreCase(zonename + ".ZONE")){
				file.delete();
				zonefound = true;
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Zone "+zonename+" removed!");
				break;
			}
		}
		if(!zonefound){
			player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Zone not removed! (/ams list)");
		}
	}
	
	public void removeByPositionAMS(Player player){
		String zone = MultiFunction.getZone(player);
		removeByNameAMS(zone, player);
	}
	
	public void infoAMS(Player player){
		if(locked){
			player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Zone creation is in progress by "+lockedBy);
			if(ams.size() == 1){
				String fparts[] = ams.get(0).split("\\.\\.");
				String parts[] = fparts[0].split("\\.");
				String pointMsg = "(" + parts[0] + ", " + parts[1] +")";
 				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Point A: " + pointMsg);
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "No Point B set!");
			}else if(ams.size() == 2){
				String fparts[] = ams.get(0).split("\\.\\.");
				String parts[] = fparts[0].split("\\.");
				String pointAMsg = "(" + parts[0] + ", " + parts[1] +")";
				parts = fparts[1].split("\\.");
				String pointBMsg = "(" + parts[0] + ", " + parts[1] +")";
 				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Point A: " + pointAMsg);
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "Point B: " + pointBMsg);
			}else{
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "No AMS information, please type \"#bug AMS Lock Info Error\" so we can fix this!");
			}
		}else{
			player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + "AMS is currently not handling a new zone! (Unlocked)");
		}
		player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.GREEN + numZones(player) + " zone(S) are listed in AMS. (/ams list)");
	}
	
	public boolean isZone(String zonename, Player p){
		boolean iszone = false;
		String world = p.getWorld().getName();
		File dir = new File("plugins/AMS/"+world+"/");
		File files[] = dir.listFiles();
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split(world);
			String name = parts[parts.length-1];
			if(name.toLowerCase().endsWith((zonename + ".ZONE").toLowerCase())){
				iszone = true;
				break;
			}
		}
		return iszone;
	}
	
	public int numZones(Player player){
		int zones = 0;
		File dir = new File("plugins/AMS/"+player.getWorld().getName()+"/");
		File listing[] = dir.listFiles(new FileFilter() {
			public boolean accept(File f){
				return f.isFile();
			}
		});
		zones = listing.length;
		return zones;
	}
	
	public void loadOP(){
		try{
			Player players[] = Bukkit.getOnlinePlayers();
			for(Player onlinep : players){
				String perms = "";
				String zone = MultiFunction.getZone(onlinep);
				String line = onlinep.getName();
				File f1 = new File("plugins/AMS_GrantedPlayers/"+line+".PLAYER");
				File f2 = new File("plugins/AMS_RegisteredPlayers/"+line+".PLAYER");
				if(!f1.exists()){
					f1.createNewFile();
				}
				if(!f2.exists()){
					f2.createNewFile();
				}
				BufferedReader in = new BufferedReader(new FileReader(f1));
				String ln;
				while((ln = in.readLine()) != null){
					ln = ln.replaceAll("\\\\r\\\\n", "");
					perms = perms+ln+"\r\n";
				}
				in.close();
				BufferedWriter out = new BufferedWriter(new FileWriter(f1, false));
				out.write(perms);
				out.close();
				out = new BufferedWriter(new FileWriter(f2, false));
				out.write(zone);
				out.close();
			}
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
}
