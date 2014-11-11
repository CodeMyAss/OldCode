package ams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class ServerBlockListener extends BlockListener{

	public static AMSMain plugin;
	
	public ServerBlockListener(AMSMain ams){
		plugin = ams;
	}

	public void onBlockDamage(BlockDamageEvent event){
		if(MultiFunction.isRestricted(event.getPlayer().getTargetBlock(null, 100).getLocation()) && !MultiFunction.hasPermission(event.getPlayer(), event.getPlayer().getTargetBlock(null, 100).getLocation(), true)){
			event.setCancelled(true);
			if(event.getPlayer() != null){
				Player player = event.getPlayer();
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You can't edit things in " + MultiFunction.getZone(player) + ".");
				MultiFunction.reportPlayer(player);
			}
		}
	}
	
	public void onBlockBreak(BlockBreakEvent event){
		if(MultiFunction.isRestricted(event.getPlayer().getTargetBlock(null, 100).getLocation()) && !MultiFunction.hasPermission(event.getPlayer(), event.getPlayer().getTargetBlock(null, 100).getLocation(), true)){
			event.setCancelled(true);
			if(event.getPlayer() != null){
				Player player = event.getPlayer();
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You can't break things in " + MultiFunction.getZone(event.getBlock().getLocation()) + ".");
				MultiFunction.reportPlayer(player);
			}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event){
		if(MultiFunction.isRestricted(event.getPlayer().getTargetBlock(null, 100).getLocation()) && !MultiFunction.hasPermission(event.getPlayer(), event.getPlayer().getTargetBlock(null, 100).getLocation(), true)){
			event.setCancelled(true);
			if(event.getPlayer() != null){
				Player player = event.getPlayer();
				player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You are not allowed to place in " + MultiFunction.getZone(player) + ".");
				MultiFunction.reportPlayer(player);
			}
		}
	}
	
	public void onBlockIgnite(BlockIgniteEvent event){
		if(event.getPlayer() != null){
			if(MultiFunction.isRestricted(event.getPlayer().getTargetBlock(null, 100).getLocation()) && !MultiFunction.hasPermission(event.getPlayer(), event.getPlayer().getTargetBlock(null, 100).getLocation(), true)){
				event.setCancelled(true);
				if(event.getPlayer() != null){
					Player player = event.getPlayer();
					player.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "You are not allowed to place in " + MultiFunction.getZone(player) + ".");
					MultiFunction.reportPlayer(player);
				}
			}
		}
	}
	
	public void onBlockBurn(BlockBurnEvent event){
		if(MultiFunction.isRestricted(event.getBlock().getLocation())){
			event.setCancelled(true);
		}
	}
	
	public void onBlockSpread(BlockSpreadEvent event){
		if(MultiFunction.isRestricted(event.getBlock().getLocation())){
			event.setCancelled(true);
		}
	}
}
