package mw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("deprecation")
public class WorldLauncher extends JavaPlugin{
	public WorldLauncher plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public World PVP_WORLD;
	public World SURVIVAL_WORLD;
	private World CONTEST_WORLD;
	
	public final ServerBlockListener blockListener = new ServerBlockListener(this);
	public final ServerPlayerListener playerListener = new ServerPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Highest, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("worlds.txt")));
			String line;
			while((line = in.readLine()) != null){
				line = line.replaceAll("\\\r\\\n", "");
				Bukkit.createWorld(WorldCreator.name(line));
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
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
				}else if(line.startsWith("CONTEST:")){
					String parts[] = line.replaceAll("\\\n\\\r", "").split(":");
					CONTEST_WORLD = Bukkit.getWorld(parts[parts.length-1]);
				}
				System.out.println(line);
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmdcmd, String cmd, String[] args){
		Player player = (Player) sender;
		List<World> worlds = Bukkit.getWorlds();
		if(cmd.equalsIgnoreCase("warp")){
			String gotoWorld = getAlias(args[0]);
			port(gotoWorld, player);
		}else{
			if(player.isOp() && isAdmin(player)){
				if(cmd.equalsIgnoreCase("create")){
					String worldName = args[0];
					if(worlds.contains(worldName)){
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.RED + "World name already in use!");
					}else{
						World created = Bukkit.getServer().createWorld(WorldCreator.name(worldName).seed(System.currentTimeMillis()));
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + created.getName() + " has been created.");
						try{
							BufferedWriter out = new BufferedWriter(new FileWriter(new File("worlds.txt"), true));
							out.write(worldName+"\r\n");
							out.close();
						}catch (Exception e){
							ChatLog.log_error(e.getMessage());
						}
					}
				}else if(cmd.equalsIgnoreCase("w")){
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("interact")){
							String world = player.getWorld().getName();
							String toWorld = args[1];
							String locationString = "";
							Location target = player.getTargetBlock(null, 100).getLocation();
							locationString = target.getX()+".."+target.getY()+".."+target.getZ()+".."+world;
							try{
								File worldFile = new File("plugins/MultiWorld/"+toWorld+".PORTAL");
								BufferedWriter out = new BufferedWriter(new FileWriter(worldFile, true));
								out.write(locationString+"\r\n");
								out.close();
							}catch (Exception e){
								ChatLog.log_error(e.getMessage());
							}
							player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.AQUA + "The interact location has been set.");
						}
					}else{
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "Help Menu: ");
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/warp <world>  - Warp to a world");
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/create <worldname>  - Create a world");
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/w interact <world>  - Create an interaction point for <world>");
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/where <player>  - Tells you where a player is");
					}
			}else if(cmd.equalsIgnoreCase("where")){
				if(args.length > 0){
					List<Player> players = Bukkit.matchPlayer(args[0]);
					for(Player pl : players){
						player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.AQUA + pl.getName() + " : "+pl.getWorld().getName());
					}
				}else{
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "Help Menu: ");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/warp <world>  - Warp to a world");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/create <worldname>  - Create a world");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/w interact <world>  - Create an interaction point for <world>");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/where <player>  - Tells you where a player is");
				}
			}else{
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "Help Menu: ");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/warp <world>  - Warp to a world");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/create <worldname>  - Create a world");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/w interact <world>  - Create an interaction point for <world>");
					player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.GREEN + "/where <player>  - Tells you where a player is");
				}
			}else{
				player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.RED + "You must be an OP to do that.");
			}
		}
		return false;
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
	
	public String getAlias(String command){
		String world = command;
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/MultiWorld/alias.txt")));
			String line;
			while((line = in.readLine()) != null){
				line = line.replaceAll("\\\r\\\n", "");
				String parts[] = line.split("\\.");
				if(parts[1].equalsIgnoreCase(command)){
					world = parts[0];
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return world;
	}
	
	public void port(String gotoWorld, Player player){
		boolean found = false;
		String fromWorld = player.getWorld().getName();
		gotoWorld = getAlias(gotoWorld);
		checkGameMode(player, gotoWorld);
		if(!gotoWorld.equals("") && gotoWorld != null){
			List<World> worlds = Bukkit.getWorlds();
			for(World w : worlds){
				if(w.getName().equalsIgnoreCase(gotoWorld)){
					player.teleport(w.getSpawnLocation());
					found = true;
				}
			}
			if(!found){
				player.sendMessage(ChatColor.GOLD + "[WORLDS] " + ChatColor.RED + gotoWorld + " is not a world.");
			}else{
				try{
					inventory(player, gotoWorld, fromWorld);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkGameMode(Player player, String world){
		if(world.equalsIgnoreCase(PVP_WORLD.getName()) || world.equalsIgnoreCase(SURVIVAL_WORLD.getName()) || world.equalsIgnoreCase(CONTEST_WORLD.getName())){
			if(!isBuilder(player)){
				if(!player.isOp()){
					player.setGameMode(GameMode.SURVIVAL);
				}
			}
			if(world.equalsIgnoreCase(CONTEST_WORLD.getName())){
				player.setGameMode(GameMode.CREATIVE);
			}
		}
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

	public void inventory(Player player, String world, String fromWorld){
		saveInventory(player, fromWorld);
		checkForLoadInventory(player);
		loadInventory(player, world);
	}
	
	public void loadInventory(Player player, String world) {
		File saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_"+world+".yml");
	    Configuration config = new Configuration(saveFile);
	    config.load();
	    Integer i = 0;
	    Integer size = player.getInventory().getSize();
	    for(i=0; i<size; i++) {
	        ItemStack item = new ItemStack(0, 0);
	        if(config.getInt(i.toString() + ".amount", 0) !=0) {
	            Integer amount = config.getInt(i.toString() + ".amount", 0);
	            Integer durability = config.getInt(i.toString() + ".durability", 0);
	            Integer type = config.getInt(i.toString() + ".type", 0);
	            item.setAmount(amount);
	            item.setTypeId(type);
	            item.setDurability(Short.parseShort(durability.toString()));
	            player.getInventory().setItem(i, item);
	            player.updateInventory();
	        }
	    }
	}
	
	public void checkForLoadInventory(Player player){
		player.getInventory().clear();
	}
	
	public void saveInventory(Player player, String world) {
	    Integer size = player.getInventory().getSize();
	    Integer i = 0;
	    File saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_"+world+".yml");
	    Configuration config = new Configuration(saveFile);
	    for(i=0; i<size; i++) {
	        ItemStack item = player.getInventory().getItem(i);
	        if(item.getAmount() !=0) {
	            config.setProperty(i.toString() + ".amount", item.getAmount());
	            Short durab = item.getDurability();
	            config.setProperty(i.toString() + ".durability", durab.intValue());
	            config.setProperty(i.toString() + ".type", item.getTypeId());
	            config.save();
	        }
	    }
	}
}
