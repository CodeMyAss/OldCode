package chatlog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Grabber extends JavaPlugin {
	
	public static Grabber plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
		
	public void onDisable() {
		try{	
			List<World> worlds = Bukkit.getWorlds();
			String worldname = worlds.get(0).getName();
			String filename = "F:\\xampp\\htdocs\\mc\\logs\\"+worldname+".LOG";
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(filename+".old"), true));
			BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while((line = in.readLine()) != null){
				out.write(line.replaceAll("\\\\r\\\\n", "")+"\r\n");
			}
			in.close();
			out.close();
			out = new BufferedWriter(new FileWriter(new File(filename), false));
			out.write("");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
