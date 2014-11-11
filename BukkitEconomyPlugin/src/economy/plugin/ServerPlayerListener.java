package economy.plugin;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import economy.economy.MoneyHandler;
import economy.economy.PlayerListing;
import economy.external.StoreHandler;
import economy.inventory.InventoryWriter;


public class ServerPlayerListener extends PlayerListener{
	public static WorldLauncher plugin;
	
	public ServerPlayerListener(WorldLauncher ams){
		plugin = ams;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		InventoryWriter invwriter = new InventoryWriter(player, plugin);
		invwriter.write();
		PlayerListing pl = new PlayerListing(player, plugin);
		if(!pl.hasVisited()){
			pl.createPlayer();
		}
		if(!pl.hasBank()){
			pl.createPlayerBank();
		}
		MoneyHandler wallet = new MoneyHandler(plugin, player);
		player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.AQUA + "Hello! Your current ballance is "+wallet.getBallanceAsString());
		StoreHandler store = new StoreHandler(plugin);
		store.handleOfflinePlayer(player);
	}
	
	public void onPlayerQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		InventoryWriter invwriter = new InventoryWriter(player, plugin);
		invwriter.write();
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		Player player = event.getPlayer();
		InventoryWriter invwriter = new InventoryWriter(player, plugin);
		invwriter.write();
	}
}
