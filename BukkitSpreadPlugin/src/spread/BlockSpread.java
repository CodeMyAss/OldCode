package spread;

import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockSpread extends JavaPlugin{
	public static BlockSpread plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final ServerBlockListener blockListener = new ServerBlockListener(this);
	public final ServerPlayerListener playerListener = new ServerPlayerListener(this);
		
	public Vector<String> ams = new Vector<String>();
	public boolean locked = false;
	public String lockedBy = "";
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_SPREAD, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Highest, this);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
}
