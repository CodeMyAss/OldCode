package economy.plugin;


import org.bukkit.event.block.BlockListener;

public class ServerBlockListener extends BlockListener{

	public static WorldLauncher plugin;
	
	public ServerBlockListener(WorldLauncher ams){
		plugin = ams;
	}
}
