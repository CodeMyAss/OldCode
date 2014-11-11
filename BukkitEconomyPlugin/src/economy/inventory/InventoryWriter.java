package economy.inventory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import economy.bank.ChestHandler;
import economy.economy.MoneyHandler;
import economy.economy.ProductListing;
import economy.external.StoreHandler;
import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

public class InventoryWriter {
	
	public WorldLauncher plugin;
	public Player player;
	
	public InventoryWriter(Player player, WorldLauncher plugin){
		this.player = player;
		this.plugin = plugin;
	}
	
	public void write(){
		File file = new File("F:\\xampp\\htdocs\\mc\\players\\"+player.getName()+".PLAYER");
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			MoneyHandler wallet = new MoneyHandler(plugin, player);
			out.write("Current Ballance: "+wallet.getBallanceAsString()+"<br><br>\r\n");
			ItemStack inventory[] = player.getInventory().getContents();
			for(ItemStack item : inventory){
				if(item != null){
					out.write("<img src='"+getItemImage(item)+"' style='width:25px;height:25px;'> "+item.getType().name()+" x"+item.getAmount()+" ("+item.getDurability()+" durability)<br>\r\n");
				}
			}
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
			e.printStackTrace();
		}
		ChestHandler chHandler = new ChestHandler(plugin);
		chHandler.exportBank(player);
		InventoryHandler handler = new InventoryHandler(plugin, player);
		handler.export();
		ProductListing products = new ProductListing(player, plugin);
		products.export();
		MoneyHandler wallet = new MoneyHandler(plugin, player);
		wallet.export();
		StoreHandler store = new StoreHandler(plugin);
		store.sendInventoryToStore(player);
	}
	
	public String getItemImage(ItemStack item){
		String name = "";
		name = "icons\\"+item.getTypeId()+".png";
		return name;
	}
}
