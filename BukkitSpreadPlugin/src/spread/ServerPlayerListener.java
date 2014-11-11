package spread;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class ServerPlayerListener extends PlayerListener{
	
	public static BlockSpread plugin;
	
	public ServerPlayerListener(BlockSpread bs){
		plugin = bs;
	}
	
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
		if(!event.getPlayer().isOp()){
			if(event.getItemStack().getType() == Material.LAVA ||
				event.getItemStack().getType() == Material.LAVA_BUCKET){
				event.setCancelled(true);
			}
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		if(!event.getPlayer().isOp()){
			if(event.hasItem()){
				ItemStack item = event.getItem();
				if(item.getType() == Material.FLINT_AND_STEEL){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.LAVA){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.TNT){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.FIRE){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.LAVA_BUCKET){
					item.setType(Material.YELLOW_FLOWER);
				}
			}
			if(event.hasBlock()){
				Block item = event.getClickedBlock();
				if(item.getType() == Material.FLINT_AND_STEEL){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.LAVA){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.TNT){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.FIRE){
					item.setType(Material.YELLOW_FLOWER);
				}else if(item.getType() == Material.LAVA_BUCKET){
					item.setType(Material.YELLOW_FLOWER);
				}
			}
		}
	}
}
