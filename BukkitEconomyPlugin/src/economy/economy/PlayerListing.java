package economy.economy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.bukkit.entity.Player;

import economy.bank.ChestHandler;
import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

public class PlayerListing {
	
	public WorldLauncher plugin;
	public Player player;
	
	public PlayerListing(Player player, WorldLauncher plugin){
		this.player = player;
		this.plugin = plugin;
	}
	
	public boolean hasVisited(){
		return new File("plugins/Economy/players/"+player.getName()+".PLAYER").exists();
	}
	
	public void createPlayer(){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/players/"+player.getName()+".PLAYER"), false));
			out.write("250");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		player.getInventory().clear();
	}
	
	public boolean hasBank(){
		return new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + "_BANK.yml").exists();
	}
	
	public void createPlayerBank(){
		ChestHandler ch = new ChestHandler(plugin);
		ch.saveInventory(ch.getBankChestContents(), player);
	}
}
