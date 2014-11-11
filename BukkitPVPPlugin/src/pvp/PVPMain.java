package pvp;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PVPMain extends JavaPlugin{
	
	public static PVPMain plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ServerPlayerListener playerListener = new ServerPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		Player player = (Player) sender;
		if(cmd.equalsIgnoreCase("pvp")){
			
		}else if(cmd.equalsIgnoreCase("leaveclan")){
			
		}else if(cmd.equalsIgnoreCase("joinclan")){
			
		}else if(cmd.equalsIgnoreCase("createclan")){
			
		}else if(cmd.equalsIgnoreCase("clankick")){
			
		}else if(cmd.equalsIgnoreCase("clan")){
			
		}else if(cmd.equalsIgnoreCase("listclans")){
			
		}else{
			sendPlayerMessage(player, "Help Menu: ");
			sendPlayerMessage(player, "/pvp  - Toggle PVP Mode");
			sendPlayerMessage(player, "/clan  - Speak in [CLAN] chat");
			sendPlayerMessage(player, "/listclans  - List all clans");
			sendPlayerMessage(player, "/leaveclan  - Leave current clan");
			sendPlayerMessage(player, "/joinclan <clan name>  - Join a clan");
			sendPlayerMessage(player, "/createclan <clan name>  - Create a new clan");
			sendPlayerMessage(player, "/joinclan <clan> <password>  - Join a password protected clan");
			sendPlayerMessage(player, "/createclan <clan> <password>  - Create a new password protected clan");
		}
		return false;
	}		
	
	public void sendPlayerMessage(Player player, String msg){
		player.sendMessage(ChatColor.GOLD + "[CLANS] " + ChatColor.AQUA + msg);
	}
}
