package economy.economy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

@SuppressWarnings("deprecation")
public class ProductListing {
	
	public WorldLauncher plugin;
	public Player player;
	
	public ProductListing(Player player, WorldLauncher plugin){
		this.player = player;
		this.plugin = plugin;
	}
	
	public double getCost(ItemStack item){
		double coins = 0;
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/Economy/items.LIST")));
			String line;
			while((line = in.readLine()) != null){
				line = line.toLowerCase();
				if(line.startsWith(item.getType().name().toLowerCase())){
					line = line.replaceAll(" ", "").replaceAll("\\\r\\\n", "");
					String parts[] = line.split(":");
					coins = Double.parseDouble(parts[1]);
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return coins;
	}
	
	public void export(){
		File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", "items.yml");
	    Configuration config = new Configuration(saveFile);
	    try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/Economy/items.LIST")));
			String line;
			while((line = in.readLine()) != null){
				line = line.toLowerCase();
					line = line.replaceAll(" ", "").replaceAll("\\\r\\\n", "");
					String parts[] = line.split(":");
					double coins = Double.parseDouble(parts[1]);
					config.setProperty(parts[0]+".value", coins+"");
					config.save();
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
}
