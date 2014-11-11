package server.file;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Loader extends JavaPlugin{

	public static Loader plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static long interval = 10; //Seconds
	public final ServerChatPlayerListener playerListener = new ServerChatPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
		playerListener.offline();
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");

		playerListener.online();
	}
}
