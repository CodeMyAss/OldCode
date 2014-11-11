package tp;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Teleport extends JavaPlugin{
	public static Teleport plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public Vector<String> flags = new Vector<String>();
	public ServerPlayerListener playerListener = new ServerPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		Player player = (Player) sender;
		if(args.length > 0 && player.isOp()){
			if(cmd.equalsIgnoreCase("tp")){
				String player1 = args[0];
				String player2 = args[1];
				List<Player> matches = Bukkit.matchPlayer(player1);
				Player p1 = matches.get(0);
				matches = Bukkit.matchPlayer(player2);
				Player p2 = matches.get(0);
				p1.teleport(p2);
				Player p1s = (Player) sender;
				p1s.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + p1.getName()+" has been moved to "+p2.getName());
				p1.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + "You were brought to "+p2.getName());
				p2.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + "You were brought to "+p1.getName());
			}else if(cmd.equalsIgnoreCase("goto")){
				Player p1 = (Player) sender;
				List<Player> matches = Bukkit.matchPlayer(args[0]);
				Player p2 = matches.get(0);
				p1.teleport(p2);
				p1.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + "You were brought to "+p2.getName());
				p2.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + p1.getName()+" moved to your location.");
			}else if(cmd.equalsIgnoreCase("summon")){
				Player p1 = (Player) sender;
				List<Player> matches = Bukkit.matchPlayer(args[0]);
				Player p2 = matches.get(0);
				p2.teleport(p1);
				p1.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + "You summoned "+p2.getName());
				p2.sendMessage(ChatColor.GOLD + "[TP] "+ChatColor.AQUA + "You were brought to "+p1.getName());
			}else{
				player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "Help Menu: ");
				player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "/tp <player1> <player2>  - Teleport player 1 to player 2");
				player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "/goto <player> - Go to player");
				player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "/summon <player> - Summon player");
			}
		}else{
			player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "Help Menu: ");
			player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "/tp <player1> <player2>  - Teleport player 1 to player 2");
			player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "/goto <player> - Go to player");
			player.sendMessage(ChatColor.GOLD + "[TP] " + ChatColor.GREEN + "/summon <player> - Summon player");
		}
		return false;
	}
}
