package redstone;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Devices extends JavaPlugin{
	public Devices plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public World PVP_WORLD;
	public World SURVIVAL_WORLD;
	
	public final ServerBlockListener blockListener = new ServerBlockListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, this.blockListener, Event.Priority.Highest, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
