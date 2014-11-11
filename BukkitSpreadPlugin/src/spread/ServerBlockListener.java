package spread;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class ServerBlockListener extends BlockListener{
	
	public static BlockSpread plugin;
	
	public ServerBlockListener(BlockSpread bs){
		plugin = bs;
	}
	
	public void onBlockPlace(BlockPlaceEvent event){
		if(!event.getPlayer().isOp()){
			Block block = event.getBlock();
			if(block.getType() == Material.TNT){
				block.setType(Material.YELLOW_FLOWER);
			}else if(block.getType() == Material.LAVA){
				block.setType(Material.YELLOW_FLOWER);
			}else if(block.getType() == Material.FIRE){
				block.setType(Material.YELLOW_FLOWER);
			}
		}
	}
	
	public void onBlockSpread(BlockSpreadEvent event){
		if(event.getBlock().getType() != Material.GRASS &&
			event.getBlock().getType() != Material.DIRT){
			event.setCancelled(true);
		}
	}
	
	public void onBlockBurn(BlockBurnEvent event){
		event.setCancelled(true);
	}
	
	public void onBlockIgnite(BlockIgniteEvent event){
		event.setCancelled(true);
	}
}
