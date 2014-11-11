package pvp;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ServerPlayerListener extends PlayerListener{
	public static PVPMain plugin;
	
	public ServerPlayerListener(PVPMain ams){
		plugin = ams;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		
	}
}
