package redstone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class ServerBlockListener extends BlockListener{

	public static Devices plugin;
	
	public ServerBlockListener(Devices ams){
		plugin = ams;
	}
	
	public void onBlockRedstoneChange(BlockRedstoneEvent event){
		if(event.getBlock() != null){
			Block block = event.getBlock();
			//ChatLog.log_server_message(block.getType().name());
			checkArea(block);
		}
	}
	
	public void checkArea(Block wire){
		Vector<Block> blocks = getBlocks(wire);
		for(Block block : blocks){
			//ChatLog.log_server_message(block.getBlockPower()+" ("+block.getType().name()+")");
			if(wire.getBlockPower() == 0 && isWire(wire)){
				if(block.getType() == Material.GLASS){
					block.setType(Material.GLOWSTONE);
				}else if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
					sign(block);
				}else{
					//getSigns(block);
				}
			}else{
				if(block.getType() == Material.GLOWSTONE){
					block.setType(Material.GLASS);
				}else if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
					sign(block);
				}
			}
		}
	}
	
	public void getSigns(Block wire){
		Vector<Block> blocks = getBlocks(wire);
		for(Block block : blocks){
			//ChatLog.log_server_message(block.getBlockPower()+" ("+block.getType().name()+")");
			if(wire.getBlockPower() == 0){
				if(block.getType() == Material.GLASS){
					block.setType(Material.GLOWSTONE);
				}else if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
					sign(block);
				}
			}else{
				if(block.getType() == Material.GLOWSTONE){
					block.setType(Material.GLASS);
				}else if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
					sign(block);
				}
			}
		}
	}
	
	public void sign(Block block){
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		String timeStr = timeFormat.format(date);
		Sign sign = (Sign) block.getState();
		if(sign.getLine(0).equalsIgnoreCase("[Server Time]")){
			sign.setLine(0, "[Server Time]");
			sign.setLine(1, ChatColor.AQUA + timeStr);
			sign.setLine(2, dateStr);
			sign.setLine(3, "");
		}
	}
	
	public boolean isWire(Block block){
		boolean wire = false;
		if(block.getType() == Material.REDSTONE_TORCH_OFF){
			wire = true;
		}else if(block.getType() == Material.REDSTONE_TORCH_ON){
			wire = true;
		}else if(block.getType() == Material.REDSTONE_WIRE){
			wire = true;
		}else if(block.getType() == Material.REDSTONE){
			wire = true;
		}else if(block.getType() == Material.DIODE_BLOCK_ON){
			wire = true;
		}else if(block.getType() == Material.DIODE_BLOCK_OFF){
			wire = true;
		}
		return wire;
	}
	
	public Vector<Block> getBlocks(Block wire){
		Vector<Block> blocks = new Vector<Block>();
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY()+1,  //TOP
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY()-1,  //BOTTOM
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY(),    //LEFT
				wire.getLocation().getBlockZ()-1)));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY(),    //RIGHT
				wire.getLocation().getBlockZ()+1)));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX()-1, 
				wire.getLocation().getBlockY(),    //FRONT
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX()+1, 
				wire.getLocation().getBlockY(),    //BACK
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY()-1,    //BOTTOM LEFT
				wire.getLocation().getBlockZ()-1)));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY()-1,    //BOTTOM RIGHT
				wire.getLocation().getBlockZ()+1)));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX()-1, 
				wire.getLocation().getBlockY()-1,    //BOTTOM FRONT
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX()+1, 
				wire.getLocation().getBlockY()-1,    //BOTTOM BACK
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY()+1,    //TOP LEFT
				wire.getLocation().getBlockZ()-1)));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX(), 
				wire.getLocation().getBlockY()+1,    //TOP RIGHT
				wire.getLocation().getBlockZ()+1)));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX()-1, 
				wire.getLocation().getBlockY()+1,    //TOP FRONT
				wire.getLocation().getBlockZ())));
		blocks.add(wire.getLocation().getWorld().getBlockAt(new Location(wire.getLocation().getWorld(),
				wire.getLocation().getBlockX()+1, 
				wire.getLocation().getBlockY()+1,    //TOP BACK
				wire.getLocation().getBlockZ())));
		return blocks;
	}
}
