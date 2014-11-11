package spawns;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class BlockMobs extends JavaPlugin{

	public final Logger logger = Logger.getLogger("Minecraft");
	public final ServerMobListener mobListener = new ServerMobListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.CREATURE_SPAWN, this.mobListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
