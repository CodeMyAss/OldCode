package channel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ServerPlayerListener extends PlayerListener{

	public Chat plugin;
	
	public ServerPlayerListener(Chat p){
		plugin = p;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		File file = new File("plugins/channels/"+player.getName()+".PLAYER");
		if(!file.exists()){
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
				out.write("");
				out.close();
			}catch(Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
	}
}
