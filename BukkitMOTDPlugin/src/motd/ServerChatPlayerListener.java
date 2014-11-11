package motd;


import java.io.BufferedReader;
import java.io.FileReader;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

//import ChatLogger.ChatLog;

public class ServerChatPlayerListener extends PlayerListener {
	public static MOTD plugin;
	
	public ServerChatPlayerListener(MOTD MOTD){
		plugin = MOTD;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		try{
			BufferedReader reader = new BufferedReader(new FileReader("plugins/motd/motd.txt"));
			String line;
			while((line = reader.readLine()) != null){
				player.sendMessage(ChatColor.GOLD + "[MOTD] " + ChatColor.GREEN + line);
			}
			reader.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
}

