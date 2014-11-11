package blockFix;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;

public class ServerBlockListener extends BlockListener{

	public static WorldLauncher plugin;
	
	public ServerBlockListener(WorldLauncher ams){
		plugin = ams;
	}
	
	public void onBlockFromTo(BlockFromToEvent event){
		Block destination = event.getToBlock();
		boolean sponge = checkForSponge(destination);
		if(sponge){
			event.setCancelled(true);
		}
	}
	
	public boolean checkForSponge(Block from){
		boolean sponge = false;
		Location block = from.getLocation();
		for(int x=0;x<7;x++){
			for(int z=0;z<7;z++){
				for(int y=0;y<7;y++){
					Location testLocation = new Location(from.getWorld(), block.getX()+x, block.getY()+y, block.getZ()+z);
					Block testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()-x, block.getY()+y, block.getZ()+z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()+x, block.getY()-y, block.getZ()+z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()+x, block.getY()+y, block.getZ()-z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()-x, block.getY()+y, block.getZ()-z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()+x, block.getY()-y, block.getZ()-z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()-x, block.getY()-y, block.getZ()-z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
					testLocation = new Location(from.getWorld(), block.getX()-x, block.getY()-y, block.getZ()+z);
					testBlock = from.getWorld().getBlockAt(testLocation);
					if(testBlock.getType() == Material.SPONGE){
						sponge = true;
						break;
					}
				}
				if(sponge){
					break;
				}
			}
			if(sponge){
				break;
			}
		}
		return sponge;
	}
}
