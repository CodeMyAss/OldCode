package spawns;

import org.bukkit.entity.CreatureType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityListener;

public class ServerMobListener extends EntityListener{
	
	public static BlockMobs plugin;
	
	public ServerMobListener(BlockMobs cl){
		plugin = cl;
	}
	
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getCreatureType() == CreatureType.ENDERMAN){
			event.setCancelled(true);
		}
		if(event.getCreatureType() == CreatureType.GHAST){
			event.setCancelled(true);
		}
		if(event.getCreatureType() == CreatureType.CREEPER){
			event.setCancelled(true);
		}
		if(event.getCreatureType() == CreatureType.SILVERFISH){
			event.setCancelled(true);
		}
	}
}
