package admin;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ServerAdminLoginListener extends PlayerListener{

	public Protection plugin;
	
	public ServerAdminLoginListener(Protection p){
		plugin = p;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.getName().toLowerCase().equals("turt2live")){
			String raw_ip = player.getAddress().toString();
			String parts[] = raw_ip.split(":");
			String ip = parts[0].replace("/", "");
			if(!ip.equals("REDACTED")){
				player.kickPlayer("Attempted hacking of an Admin's Account.");
			}
		}else if(player.getName().toLowerCase().equals("sayshal")){
			String raw_ip = player.getAddress().toString();
			String parts[] = raw_ip.split(":");
			String ip = parts[0].replace("/", "");
			if(!ip.equals("REDACTED")){
				player.kickPlayer("Attempted hacking of a Mod's Account.");
			}
		}else if(player.getName().toLowerCase().equals("lonewolf93")){
			String raw_ip = player.getAddress().toString();
			String parts[] = raw_ip.split(":");
			String ip = parts[0].replace("/", "");
			if(!ip.equals("REDACTED")){
				player.kickPlayer("Attempted hacking of a Mod's Account.");
			}
		}
	}
	
}
