package afk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ServerPlayerListener extends PlayerListener{

	public AFK plugin;
	
	public ServerPlayerListener(AFK p){
		plugin = p;
	}
	
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		plugin.clearAFK(player);
		File file = new File("plugins/AFK/"+player.getName()+".PLAYER");
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			out.write("0");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void onPlayerChat(PlayerChatEvent event){
		plugin.clearAFK(event.getPlayer());
	}
	
	public void onPlayerComamndPreprocess(PlayerCommandPreprocessEvent event){
		plugin.clearAFK(event.getPlayer());
	}
}
