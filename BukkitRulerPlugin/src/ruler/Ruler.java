package ruler;

import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Ruler extends JavaPlugin{
	public static Ruler plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public Vector<String> flags = new Vector<String>();
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args){
		Player player = (Player) sender;
		if(cmd.equalsIgnoreCase("dto")){
			if(args.length == 0){
				player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.RED + "You need to define a player. /dto <player>");
			}else{
				Location ploc = player.getLocation();
				Location dloc = null;
				Player dplayer = null;
				Player online[] = Bukkit.getOnlinePlayers();
				for(Player option : online){
					if(option.getName().equalsIgnoreCase(args[0])){
						dloc = option.getLocation();
						dplayer = option;
						break;
					}
				}
				if(dloc != null){
					int distance = (int) Math.round(ploc.distance(dloc));
					player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.AQUA + "You are "+distance+" blocks away from \""+dplayer.getName()+"\". (This includes VERTICAL distance!)");
					dplayer.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.AQUA + "\""+player.getName()+"\" did a distance check using you as a target. You are "+distance+" blocks away from \""+player.getName()+"\". (This includes VERTICAL distance!) For a distance check, type: /dto <player>");
				}else{
					player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.RED + "Player \"" + args[0] + "\" was not found or not online!");
				}
			}
		}else if(cmd.equalsIgnoreCase("dflag")){
			Location location = player.getLocation();
			flags.add(player.getName()+".."+location.getX()+".."+location.getY()+".."+location.getZ());
			player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.GREEN + "Flag set!");
		}else if(cmd.equalsIgnoreCase("dtoflag")){
			boolean flagfound = false;
			String pflag = "";
			for(int i=flags.size();i>-1;i--){
				String flag = flags.get(i);
				if(flag.startsWith(player.getName())){
					flagfound = true;
					pflag = flag;
					break;
				}
			}
			if(flagfound){
				String parts[] = pflag.split("\\.\\.");
				double x = Double.parseDouble(parts[1]);
				double y = Double.parseDouble(parts[2]);
				double z = Double.parseDouble(parts[3]);
				Location loc = new Location(player.getWorld(), x, y, z);
				int distance = (int) Math.round(player.getLocation().distance(loc));
				player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.AQUA + "You are "+distance+" blocks from your flag! (/dflag to set a new flag)");
			}else{
				player.sendMessage(ChatColor.GOLD+"[Ruler] "+ChatColor.RED + "You have no flag set! Type /dflag to set a flag.");
			}
		}else{
			player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.GREEN + "Help Menu: ");
			player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.GREEN + "/dto <player> - Tells you the distance TO a player.");
			player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.GREEN + "/dflag - Sets a flag at your current position (for your use)");
			player.sendMessage(ChatColor.GOLD + "[Ruler] " + ChatColor.GREEN + "/dtoflag - Tells you the distance from your set flag.");
		}
		return false;
	}
}
