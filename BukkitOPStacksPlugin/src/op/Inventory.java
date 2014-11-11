package op;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Inventory extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	public ServerPlayerListener playerListener = new ServerPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {	
		PluginManager pm = getServer().getPluginManager();
		//pm.registerEvent(Event.Type.PLAYER_INVENTORY, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM_HELD, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.INVENTORY_OPEN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, this.playerListener, Event.Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
