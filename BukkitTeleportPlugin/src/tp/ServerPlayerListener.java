package tp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class ServerPlayerListener extends PlayerListener{
	public static Teleport plugin;
	
	public ServerPlayerListener(Teleport ams){
		plugin = ams;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getItemInHand().getType() == Material.COMPASS){
			if(player.isOp()){
				Block target = player.getTargetBlock(null, 250);
				player.teleport(new Location(player.getWorld(),
						target.getLocation().getX(),
						target.getLocation().getY()+2,
						target.getLocation().getZ()
						));
			}
		}
	}
	
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(player.isOp()){
			if(player.getItemInHand().getType() == Material.STICK){
				double defaultSpeed = 4.0;
				//player.setVelocity(player.getVelocity().multiply(0.5f));
				//Vector dir = player.getLocation().getDirection().multiply(defaultSpeed).setY(0.1);
				Vector dir = player.getLocation().getDirection().setY(0); //player.getVelocity();
				Vector veloc = player.getVelocity();
				dir.setX(Math.max(Math.min((dir.getX() * defaultSpeed)+(veloc.getX()*0.3), 2), -2));
				dir.setZ(Math.max(Math.min((dir.getZ() * defaultSpeed)+(veloc.getZ()*0.3), 2), -2));
				dir.setY(veloc.getY());
				player.setVelocity(dir);
			}
		}
	}
}
