package economy.bank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import economy.inventory.InventoryWriter;
import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

@SuppressWarnings("deprecation")
public class ChestHandler {
	
	public WorldLauncher plugin;
	private int tid = -1;
	private int DESPAWN_SECONDS = 30;
	
	public ChestHandler(WorldLauncher plugin){
		this.plugin = plugin;
	}
	
	public void setBankChest(Player player, Location location){
		String loc = location.getX()+".."+location.getY()+".."+location.getZ();
		File file = new File("plugins/Economy/bank_chests/"+loc+".BANK");
		if(file.exists()){
			player.sendMessage(ChatColor.GOLD + "[Bank] " + ChatColor.RED + "Chest location already in use!");
		}else{
			try {
				file.createNewFile();
				player.sendMessage(ChatColor.GOLD + "[Bank] " + ChatColor.GREEN + "Chest location created!");
			} catch (IOException e) {
				ChatLog.log_error(e.getMessage());
			}
		}
	}
	
	public void unsetBankChest(Player player, Location location){
		String loc = location.getX()+".."+location.getY()+".."+location.getZ();
		File file = new File("plugins/Economy/bank_chests/"+loc+".BANK");
		if(file.exists()){
			file.delete();
			player.sendMessage(ChatColor.GOLD + "[Bank] " + ChatColor.GREEN + "Chest location removed!");
		}else{
			player.sendMessage(ChatColor.GOLD + "[Bank] " + ChatColor.RED + "No bank chest there!");
		}
	}
	
	public Chest getBankChestContents(){
		Chest chest = null;
		File files[] = new File("plugins/Economy/bank_chests/").listFiles();
		for(File file : files){
			String parts[] = file.toString().replaceAll("\\\\", "").replaceAll("/", "").split("bank_chests");
			parts = parts[parts.length-1].replaceAll("\\.BANK", "").split("\\.\\.");
			double x = Double.parseDouble(parts[0]);
			double y = Double.parseDouble(parts[1]);
			double z = Double.parseDouble(parts[2]);
			List<World> worlds = Bukkit.getWorlds();
			Location location = new Location(worlds.get(0), x, y, z);
			chest = (Chest) worlds.get(0).getBlockAt(location).getState();
			break;
		}
		return chest;
	}
	
	public void setBankContents(File file, Player player){
		try{
			Configuration config = new Configuration(file);
		    config.load();
		    Integer i = 0;
		    Integer size = 27;
		    for(i=0; i<size; i++) {
		        ItemStack item = new ItemStack(0, 0);
		        if(config.getInt(i.toString() + ".amount", 0) !=0) {
		            Integer amount = config.getInt(i.toString() + ".amount", 0);
		            Integer durability = config.getInt(i.toString() + ".durability", 0);
		            Integer type = config.getInt(i.toString() + ".type", 0);
		            item.setAmount(amount);
		            item.setTypeId(type);
		            item.setDurability(Short.parseShort(durability.toString()));
		            File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + "_BANK.yml");
		            Configuration config2 = new Configuration(saveFile);
		            config2.load();
	    	        if(item.getAmount() !=0) {
	    	            config.setProperty(i.toString() + ".amount", item.getAmount());
	    	            Short durab = item.getDurability();
	    	            config2.setProperty(i.toString() + ".durability", durab.intValue());
	    	            config2.setProperty(i.toString() + ".type", item.getTypeId());
	    	            config2.save();
	    	        }
		            
		        }
		    }
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public boolean hasMBank(Player player){
		return new File("plugins/Economy/mbanks/"+player.getName()+".MBANK").exists();
	}
	
	public void spawnMBank(Player player){
		Block target = player.getTargetBlock(null, 10);
		Location spawnto = target.getLocation();
		spawnto.setY(spawnto.getY()+1);
		if(player.getWorld().getBlockAt(spawnto).getType() != Material.AIR){
			player.getWorld().getBlockAt(spawnto).setType(Material.AIR);
			Location spawnto2 = spawnto;
			spawnto2.setY(spawnto2.getY()+1);
			player.getWorld().getBlockAt(spawnto2).setType(Material.AIR);
		}
		target = player.getWorld().getBlockAt(spawnto);
		File tempFile = new File("plugins/Economy/mbanks/"+player.getName()+".MBANK");
		try {
			tempFile.createNewFile();
		} catch (IOException e) {
			ChatLog.log_error(e.getMessage());
		}
		target.setType(Material.CHEST);
		Chest chest = (Chest) target.getState();
		loadInventory(chest, player);
		player.sendMessage(ChatColor.GOLD + "[Mobile Bank] " + ChatColor.DARK_AQUA + "You have "+DESPAWN_SECONDS+" seconds to use this chest.");
		scheduleMBankDespawn(spawnto, player);
	}
	
	public void despawnMBank(Player player, Location location){
		Chest chest = (Chest) player.getWorld().getBlockAt(location).getState();
		saveInventory(chest, player);
		chest.getInventory().clear();
		player.getWorld().getBlockAt(location).setType(Material.AIR);
		File tempFile = new File("plugins/Economy/mbanks/"+player.getName()+".MBANK");
		tempFile.delete();
		player.sendMessage(ChatColor.GOLD + "[Mobile Bank] " + ChatColor.DARK_AQUA + "Bank saved!");
		loadIntoBank();
		Bukkit.getServer().getScheduler().cancelTask(tid);
	}
	
	public void scheduleMBankDespawn(final Location location, final Player pl){
		tid = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			Location chest = location;
			Player player = pl;
			
		    public void run() {
		    	despawnMBank(player, chest);
		    }
		}, DESPAWN_SECONDS*20); //20 ticks/second
	}
	
	public void loadIntoBank(){
		//File saveFiles[] = new File(plugin.getDataFolder() + File.separator + "inventories").listFiles();
	}
	
	public void exportBank(Player player){
		InventoryWriter invwriter = new InventoryWriter(player, plugin);
		//String line = invwriter.getItemImage(new ItemStack(Material.APPLE));
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("F:\\xampp\\htdocs\\mc\\players\\"+player.getName()+".PLAYER"), true));
			out.write("<hr><h1>Bank:</h1>");
			File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + "_BANK.yml");
		    Configuration config = new Configuration(saveFile);
		    config.load();
		    Integer i = 0;
		    Integer size = 27;
		    for(i=0; i<size; i++) {
		        ItemStack item = new ItemStack(0, 0);
		        if(config.getInt(i.toString() + ".amount", 0) !=0) {
		            Integer amount = config.getInt(i.toString() + ".amount", 0);
		            Integer durability = config.getInt(i.toString() + ".durability", 0);
		            Integer type = config.getInt(i.toString() + ".type", 0);
		            item.setAmount(amount);
		            item.setTypeId(type);
		            item.setDurability(Short.parseShort(durability.toString()));
		            out.write("<img src='"+invwriter.getItemImage(item)+"' style='width:25px;height:25px;'>" + item.getType().name() + " x" + item.getAmount() + " ("+item.getDurability()+" durability)<br>\r\n");
		            
		        }
		    }
		    out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void loadInventory(Chest chest, Player player) {
		try{
			File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + "_BANK.yml");
		    if(!saveFile.exists()){
		    	try {
					saveFile.createNewFile();
				} catch (IOException e) {
					ChatLog.log_error(e.getMessage());
				}
		    }
	    	Configuration config = new Configuration(saveFile);
		    config.load();
		    Integer i = 0;
		    Integer size = chest.getInventory().getSize();
		    for(i=0; i<size; i++) {
		        ItemStack item = new ItemStack(0, 0);
		        if(config.getInt(i.toString() + ".amount", 0) !=0) {
		            Integer amount = config.getInt(i.toString() + ".amount", 0);
		            Integer durability = config.getInt(i.toString() + ".durability", 0);
		            Integer type = config.getInt(i.toString() + ".type", 0);
		            item.setAmount(amount);
		            item.setTypeId(type);
		            item.setDurability(Short.parseShort(durability.toString()));
		            chest.getInventory().setItem(i, item);
		            chest.update();
		        }
		    }
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void saveInventory(Chest chest, Player player) {
	    Integer size = chest.getInventory().getSize();
	    Integer i = 0;
	    File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + "_BANK.yml");
	    Configuration config = new Configuration(saveFile);
	    for(i=0; i<size; i++) {
	        ItemStack item = chest.getInventory().getItem(i);
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
