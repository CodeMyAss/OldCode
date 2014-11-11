package commands;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

//import ChatLogger.ChatLog;

public class Commands extends JavaPlugin{

	public static int MAX_SPAWN = 300;
	
	public static Commands plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public World PVP_WORLD;
	public World SURVIVAL_WORLD;
	
	public void onDisable() {
		Player online[] = Bukkit.getOnlinePlayers();
		for(Player player_online : online){
			player_online.sendMessage(ChatColor.GOLD + "[Server Notification] " + ChatColor.LIGHT_PURPLE + "Server is reloading. Lag will occur for about 5-30 seconds.");
		}
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		Player online[] = Bukkit.getOnlinePlayers();
		for(Player player_online : online){
			player_online.sendMessage(ChatColor.GOLD + "[Server Notification] " + ChatColor.LIGHT_PURPLE + "Server is done reloading.");
		}
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/MultiWorld/specworlds.txt")));
			String line;
			while((line = in.readLine()) != null){
				if(line.startsWith("PVP:")){
					String parts[] = line.replaceAll("\\\n\\\r", "").split(":");
					PVP_WORLD = Bukkit.getWorld(parts[parts.length-1]);
				}else if(line.startsWith("SURVIVAL:")){
					String parts[] = line.replaceAll("\\\n\\\r", "").split(":");
					SURVIVAL_WORLD = Bukkit.getWorld(parts[parts.length-1]);
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = null;
		if(sender instanceof Player){
			p = (Player) sender;
		}
		if(commandLabel.equalsIgnoreCase("home")){
			Player player = (Player) sender;
			World world = player.getWorld();
			player.teleport(world.getSpawnLocation());
		}else if(commandLabel.equalsIgnoreCase("helpme")){
			p.sendMessage(ChatColor.GOLD + "[HELP] " + ChatColor.DARK_GREEN + "Asking for OP help... ");
			int numOP = 0;
			Player players[] = Bukkit.getOnlinePlayers();
			for(Player op : players){
				if(op.isOp()){
					op.sendMessage(ChatColor.GOLD + "[HELP] " + ChatColor.DARK_GREEN + p.getName()+" needs help! Type /pm "+p.getName()+" <MSG> to help them out.");
					numOP++;
				}
			}
			if(numOP==0){
				p.sendMessage(ChatColor.GOLD + "[HELP] " + ChatColor.DARK_GREEN + "No OPs are online at the moment, if you are dealing with a major issue, email us: minecraft@turt2live.com");
			}else{
				p.sendMessage(ChatColor.GOLD + "[HELP] " + ChatColor.DARK_GREEN + "An OP should message you shortly if they don't, send us an email at minecraft@turt2live.com");
			}
			try{
			    DateFormat dateFormat = new SimpleDateFormat("EEE__d_MMM_yyyy_HH_mm_ss_Z");
			    Date date = new Date();
			    String timestamp = dateFormat.format(date);
				File opfile = new File("HELP_REQUESTS/"+p.getName()+"_"+timestamp+".txt");
				BufferedWriter out = new BufferedWriter(new FileWriter(opfile, false));
				out.write("OPs Online: \r\n");
				players = Bukkit.getOnlinePlayers();
				for(Player op : players){
					if(op.isOp()){
						out.write(op.getName() + "\r\n");
					}
				}
				out.write("\r\nPlayer Information: \r\n");
				out.write("Name: "+p.getName()+"\r\n");
				out.write("Location: "+p.getLocation().toString()+"\r\n");
				out.write("IP: "+p.getAddress().toString()+"\r\n");
				out.close();
				ChatLog.log_server_message(p.getName() + " has asked for help. OP File: "+opfile.toString());
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}else if(commandLabel.equalsIgnoreCase("todo")){
			try{
				BufferedReader in = new BufferedReader(new FileReader("_TODO.list"));
				String line;
				while((line = in.readLine()) != null){
					if(line != ""){
						line = line.replaceAll("\\\t", "    ");
						line = line.replaceAll("	", "    ");
						p.sendMessage(ChatColor.GOLD + "[TODO] " + ChatColor.AQUA + line);
					}
				}
				in.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}else if(commandLabel.equalsIgnoreCase("gm") || commandLabel.equalsIgnoreCase("gamemode")){
			if(p.getWorld() != PVP_WORLD && p.getWorld() != SURVIVAL_WORLD && isAuthorizedGMChange(p)){
				if(args.length == 0 || args[0].equalsIgnoreCase("t") || args[0].equalsIgnoreCase("toggle")){
					GameMode mode = p.getGameMode();
					if(mode == GameMode.SURVIVAL){
						p.setGameMode(GameMode.CREATIVE);
					}else{
						p.setGameMode(GameMode.SURVIVAL);
					}
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "You are now on " + p.getGameMode().toString());
				}else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1")){
					p.setGameMode(GameMode.CREATIVE);
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "You are now on " + p.getGameMode().toString());
				}else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0")){
					p.setGameMode(GameMode.SURVIVAL);
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "You are now on " + p.getGameMode().toString());
				}else{
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "To set your GM type /gm or /gm t  to toggle (Switch Creative/Survival)");
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "/gm c or /gm creative for Creative Mode");
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "/gm s or /gm survival for Survival Mode");
				}
			}else{
				if(p.isOp()){
					if(args.length == 0 || args[0].equalsIgnoreCase("t") || args[0].equalsIgnoreCase("toggle")){
						GameMode mode = p.getGameMode();
						if(mode == GameMode.SURVIVAL){
							p.setGameMode(GameMode.CREATIVE);
						}else{
							p.setGameMode(GameMode.SURVIVAL);
						}
						p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "You are now on " + p.getGameMode().toString());
					}else if(args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("1")){
						p.setGameMode(GameMode.CREATIVE);
						p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "You are now on " + p.getGameMode().toString());
					}else if(args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("0")){
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "You are now on " + p.getGameMode().toString());
					}else{
						p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "To set your GM type /gm or /gm t  to toggle (Switch Creative/Survival)");
						p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "/gm c or /gm creative for Creative Mode");
						p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.GREEN + "/gm s or /gm survival for Survival Mode");
					}
				}else{
					p.sendMessage(ChatColor.GOLD + "[Game Mode] " + ChatColor.RED + "You cannot do that in this world.");
				}
			}
		}else{
			if(p != null){
				if(p.isOp()){
					World world = p.getWorld();
					if(commandLabel.equalsIgnoreCase("weather")){
						Player player = (Player) sender;
						if(args[0].equalsIgnoreCase("rain")){
							world.setStorm(false);
							world.setWeatherDuration(120*20);
						}else if(args[0].equalsIgnoreCase("snow")){
							world.setStorm(false);
							world.setWeatherDuration(120*20);
						}else if(args[0].equalsIgnoreCase("storm")){
							world.setStorm(true);
							world.setThundering(true);
							world.setThunderDuration(120*20);
							world.setWeatherDuration(120*20);
						}else if(args[0].equalsIgnoreCase("clear")){
							world.setWeatherDuration(0);
							world.setThunderDuration(0);
							world.setStorm(false);
							world.setThundering(false);
						}
						player.sendMessage(ChatColor.GOLD + "[Weather] " + ChatColor.GREEN + "The weather will change shortly.");
						ChatLog.log_console_message(player.getName()+" changed the weather to "+args[0]);
					}else if(commandLabel.equalsIgnoreCase("spawn") && isAdmin(p)){
						Player player = (Player) sender;
						CreatureType mob = CreatureType.SHEEP;
						if(args[0].equalsIgnoreCase("pig")){
							mob = CreatureType.PIG;
						}else if(args[0].equalsIgnoreCase("cow")){
							mob = CreatureType.COW;
						}else if(args[0].equalsIgnoreCase("chicken")){
							mob = CreatureType.CHICKEN;
						/*}else if(args[0].equalsIgnoreCase("ghast")){
							mob = CreatureType.GHAST;*/
						}else if(args[0].equalsIgnoreCase("pigman")){
							mob = CreatureType.PIG_ZOMBIE;
						/*}else if(args[0].equalsIgnoreCase("creeper")){
							mob = CreatureType.CREEPER;*/
						}else if(args[0].equalsIgnoreCase("skeleton")){
							mob = CreatureType.SKELETON;
						}else if(args[0].equalsIgnoreCase("zombie")){
							mob = CreatureType.ZOMBIE;
						/*}else if(args[0].equalsIgnoreCase("enderman")){
							mob = CreatureType.ENDERMAN;*/
						}else if(args[0].equalsIgnoreCase("spider")){
							mob = CreatureType.SPIDER;
						}else if(args[0].equalsIgnoreCase("cavespider")){
							mob = CreatureType.CAVE_SPIDER;
						}else if(args[0].equalsIgnoreCase("squid")){
							mob = CreatureType.SQUID;
						/*}else if(args[0].equalsIgnoreCase("silverfish")){
							mob = CreatureType.SILVERFISH;*/
						}else if(args[0].equalsIgnoreCase("slime")){
							mob = CreatureType.SLIME;
						}else if(args[0].equalsIgnoreCase("giant")){
							mob = CreatureType.GIANT;
						}else if(args[0].equalsIgnoreCase("wolf")){
							mob = CreatureType.WOLF;
						}
						if(Integer.parseInt(args[1]) > MAX_SPAWN){
							args[1] = ""+MAX_SPAWN;
						}
						for(int i=0;i<Integer.parseInt(args[1]);i++){
							world.spawnCreature(player.getLocation(), mob);
						}
						ChatLog.log_console_message(player.getName()+" spawned "+args[1]+" of "+args[0]);
					}else if(commandLabel.equalsIgnoreCase("killall") && isAdmin(p)){
						Player player = (Player) sender;
						List<Entity> living = player.getNearbyEntities(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
						for(Entity alive : living){
							alive.setFireTicks(9000);
						}
					}else if(commandLabel.equalsIgnoreCase("setspawn") && isAdmin(p)){
						Player player = (Player) sender;
						player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
						player.sendMessage("Spawn point set.");
					}else if(commandLabel.equalsIgnoreCase("rl")){
						Player player = (Player) sender;
						Player online[] = Bukkit.getOnlinePlayers();
						for(Player player_online : online){
							player_online.sendMessage(ChatColor.GOLD + "[Server Notification] " + ChatColor.LIGHT_PURPLE + player.getName()+" told the server to reload. Lag will occur for a 5-30 seconds!");
						}
						try{
							Thread.sleep(1000);
							Bukkit.reload();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
							player.sendMessage(ChatColor.GOLD + "[Error] " + ChatColor.RED + "Reload failed. See log.");
						}
					}else if(commandLabel.equalsIgnoreCase("spawnmax") && isAdmin(p)){
						Player player = (Player) sender;
						int value = Integer.parseInt(args[0]);
						if(value > 700){
							value = 700;
						}
						player.sendMessage("New spawn max set ("+value+", was "+MAX_SPAWN+").");
						MAX_SPAWN = value;
					}else if(commandLabel.equalsIgnoreCase("admin")){
						Player player = null;
						if(sender instanceof Player){
							player = (Player) sender;
						}
						if(player != null){
							String newAdmin = args[0];
							try{
								BufferedWriter out = new BufferedWriter(new FileWriter("admins.txt", true));
								out.write(newAdmin+"\r\n");
								out.close();
							}catch (Exception e){
								ChatLog.log_error(e.getMessage());
							}
							player.sendMessage(newAdmin+" is now an admin.");
						}else{
							String newAdmin = args[0];
							try{
								BufferedWriter out = new BufferedWriter(new FileWriter("admins.txt", true));
								out.write(newAdmin+"\r\n");
								out.close();
							}catch (Exception e){
								ChatLog.log_error(e.getMessage());
							}
							logger.log(Level.INFO, "New admin set!");
						}
					}else if(commandLabel.equalsIgnoreCase("server")){
						if(sender instanceof Player){
							if(isAdmin(p)){
								String message = "";
								for(String line : args){
									message = message + line + " ";
								}
								Bukkit.broadcastMessage(ChatColor.GOLD + "[Server Notification] "+ ChatColor.DARK_PURPLE + message);
							}
						}else{
							String message = "";
							for(String line : args){
								message = message + line + " ";
							}
							Bukkit.broadcastMessage(ChatColor.GOLD + "[Server Notification] "+ ChatColor.DARK_PURPLE + message);
						}
					}else if(commandLabel.equalsIgnoreCase("time") && isAdmin(p)){
						if(args.length < 1){
							p.sendMessage(ChatColor.GOLD + "[TIME] " + ChatColor.RED + "Not enough arguments. (Try /time <day|night>)");
						}else{
							String time = args[0];
							if(time.equalsIgnoreCase("day")){
								p.getWorld().setFullTime(6);
							}else{
								p.getWorld().setFullTime(90000);
							}
						}
					}else if(commandLabel.equalsIgnoreCase("op")){
						if(!(sender instanceof Player)){
							String player = args[0];
							if(player != null){
								Player pl = Bukkit.getPlayer(player);
								pl.setOp(true);
								p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.GREEN + player + " is now a [Mod]");
							}else{
								p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.RED + "No player chosen!");
							}
						}else{
							if(isAdmin(p)){
								String player = args[0];
								if(player != null){
									Player pl = Bukkit.getPlayer(player);
									pl.setOp(true);
									p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.GREEN + player + " is now a [Mod]");
								}else{
									p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.RED + "No player chosen!");
								}
							}
						}
					}else if(commandLabel.equalsIgnoreCase("deop")){
						if(!(sender instanceof Player)){
							String player = args[0];
							if(player != null){
								Player pl = Bukkit.getPlayer(player);
								pl.setOp(false);
								p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.GREEN + player + " is no longer a [Mod]");
							}else{
								p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.RED + "No player chosen!");
							}
						}else{
							if(isAdmin(p)){
								String player = args[0];
								if(player != null){
									Player pl = Bukkit.getPlayer(player);
									pl.setOp(false);
									p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.GREEN + player + " is no longer a [Mod]");
								}else{
									p.sendMessage(ChatColor.GOLD + "[OP Manager] " + ChatColor.RED + "No player chosen!");
								}
							}
						}
					}else if(commandLabel.equalsIgnoreCase("builder") && isAdmin(p)){
						String player = args[0];
						if(player != null){
							try{
								BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER"), false));
								out.write("GLOBAL");
								out.close();
							}catch (Exception e){
								ChatLog.log_error(e.getMessage());
							}
							p.sendMessage(ChatColor.GOLD + "[Builder Manager] " + ChatColor.GREEN + player + " is now a [Builder]");
						}else{
							p.sendMessage(ChatColor.GOLD + "[Builder Manager] " + ChatColor.RED + "No player chosen!");
						}
					}else if(commandLabel.equalsIgnoreCase("dbuilder") && isAdmin(p)){
						String player = args[0];
						if(player != null){
							try{
								BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER"), false));
								out.write("");
								out.close();
							}catch (Exception e){
								ChatLog.log_error(e.getMessage());
							}
							p.sendMessage(ChatColor.GOLD + "[Builder Manager] " + ChatColor.GREEN + player + " is no longer a [Builder]");
						}else{
							p.sendMessage(ChatColor.GOLD + "[Builder Manager] " + ChatColor.RED + "No player chosen!");
						}
					}else if(commandLabel.equalsIgnoreCase("stop")){
						if(sender instanceof Player){
							if(isAdmin(p)){
								Bukkit.getServer().shutdown();
							}
						}else{
							Bukkit.getServer().shutdown();
						}
					}
				}else{
					p.sendMessage(ChatColor.RED + "You cannot do that: You are not an OP.");
				}
			}else{
				if(commandLabel.equalsIgnoreCase("builder")){
					String player = args[0];
					if(player != null){
						try{
							BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER"), false));
							out.write("GLOBAL");
							out.close();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
						}
						logger.log(Level.INFO, "[Builder Manager] " + player + " is now a [Builder]");
					}else{
						logger.log(Level.INFO, "[Builder Manager] " + "No player chosen!");
					}
				}else if(commandLabel.equalsIgnoreCase("dbuilder")){
					String player = args[0];
					if(player != null){
						try{
							BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER"), false));
							out.write("");
							out.close();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
						}
						logger.log(Level.INFO, "[Builder Manager] " + player + " is no longer a [Builder]");
					}else{
						logger.log(Level.INFO, "[Builder Manager] " + "No player chosen!");
					}
				}else if(commandLabel.equalsIgnoreCase("stop")){
					Bukkit.getServer().shutdown();
				}else if(commandLabel.equalsIgnoreCase("admin")){
					Player player = null;
					if(sender instanceof Player){
						player = (Player) sender;
					}
					if(player != null){
						String newAdmin = args[0];
						try{
							BufferedWriter out = new BufferedWriter(new FileWriter("admins.txt", true));
							out.write(newAdmin+"\r\n");
							out.close();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
						}
						player.sendMessage(newAdmin+" is now an admin.");
					}else{
						String newAdmin = args[0];
						try{
							BufferedWriter out = new BufferedWriter(new FileWriter("admins.txt", true));
							out.write(newAdmin+"\r\n");
							out.close();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
						}
						logger.log(Level.INFO, "New admin set!");
					}
				}else if(commandLabel.equalsIgnoreCase("server")){
					String message = "";
					for(String line : args){
						message = message + line + " ";
					}
					Bukkit.broadcastMessage(ChatColor.GOLD + "[Server Notification] "+ ChatColor.DARK_PURPLE + message);
				}else if(commandLabel.equalsIgnoreCase("op")){
					String player = args[0];
					if(player != null){
						Player pl = Bukkit.getPlayer(player);
						pl.setOp(true);
						logger.log(Level.INFO, "[OP Manager] " + player + " is now a [Mod]");
					}else{
						logger.log(Level.INFO, "[OP Manager] " + "No player chosen!");
					}
				}else if(commandLabel.equalsIgnoreCase("deop")){
					String player = args[0];
					if(player != null){
						Player pl = Bukkit.getPlayer(player);
						pl.setOp(false);
						logger.log(Level.INFO, "[OP Manager] " + player + " is no longer a [Mod]");
					}else{
						logger.log(Level.INFO, "[OP Manager] " + "No player chosen!");
					}
				}
			}
		}
		return false;
	}
	
	public boolean isAuthorizedGMChange(Player p) {
		boolean isAllowed = false;
		if(isBuilder(p)){
			isAllowed = true;
		}
		if(p.isOp()){
			isAllowed = true;
		}
		//Removed for Economy
		/*List<World> worlds = Bukkit.getWorlds();
		if(p.getWorld() == worlds.get(0)){
			isAllowed = true;
		}*/
		return isAllowed;
	}
	
	public boolean isBuilder(Player player){
		boolean builder = false;
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/AMS_GrantedPlayers/"+player.getName()+".PLAYER")));
			String line;
			while((line = in.readLine()) != null){
				if(line.toLowerCase().startsWith("global")){
					builder = true;
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return builder;
	}

	public boolean isAdmin(Player player){
		boolean admin = false;
		try{
			File f = new File("admins.txt");
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line;
			while ((line = in.readLine()) != null){
				if(line.toLowerCase().startsWith(player.getName().toLowerCase())){
					admin = true;
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return admin;
	}
}
