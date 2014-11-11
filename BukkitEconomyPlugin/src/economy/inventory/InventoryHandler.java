package economy.inventory;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;

import economy.plugin.WorldLauncher;

@SuppressWarnings("deprecation")
public class InventoryHandler {
	
	private WorldLauncher plugin;
	private Player player;
	
	public InventoryHandler(WorldLauncher plugin, Player player){
		this.player = player;
		this.plugin = plugin;
	}
	
	public void setInventory(File file){
		try{
	    	Configuration config = new Configuration(file);
		    config.load();
		    Integer i = 0;
		    Integer size = player.getInventory().getSize();
		    for(i=0; i<size; i++) {
		        ItemStack item = new ItemStack(0, 0);
		        if(config.getInt(i.toString() + ".amount", 0) !=0) {
		            Integer amount = config.getInt(i.toString() + ".amount", 0);
		            Integer durability = config.getInt(i.toString() + ".durability", 0);
		            Integer type = config.getInt(i.toString() + ".type", 0);
		            item.setAmount(amount);
		            item.setTypeId(type);
		            item.setDurability(Short.parseShort(durability.toString()));
		            player.getInventory().setItem(i, item);
		    		player.updateInventory();
		        }
		    }
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void export(){
		Integer size = player.getInventory().getSize();
	    Integer i = 0;
	    File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
	    Configuration config = new Configuration(saveFile);
	    for(i=0; i<size; i++) {
	        ItemStack item = player.getInventory().getItem(i);
	        if(item.getAmount() !=0) {
	            config.setProperty(i.toString() + ".amount", item.getAmount());
	            Short durab = item.getDurability();
	            config.setProperty(i.toString() + ".durability", durab.intValue());
	            config.setProperty(i.toString() + ".type", item.getTypeId());
	            config.save();
	        }
	    }
	}
}
