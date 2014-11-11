package economy.external;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import economy.bank.ChestHandler;
import economy.economy.MoneyHandler;
import economy.inventory.InventoryHandler;
import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

public class StoreHandler {
	
	private WorldLauncher plugin;
	private int STORE_READ = 60;
	
	public StoreHandler(WorldLauncher plugin){
		this.plugin = plugin;
	}
	
	public void handle(){
		copyFile();
		Thread thread = new Thread(new StoreHandlerThread(plugin, this));
		thread.start();
	}
	
	public void handleOfflinePlayer(Player player){
		Thread thread = new Thread(new StoreOfflineHandlerThread(plugin, this, player));
		thread.start();
	}
	
	public void sendInventoriesToStore(){
		try{
			File files[] = new File(plugin.getDataFolder() + File.separator + "inventories" + File.separator).listFiles();
			for(File file : files){
				String name = file.getName();
				BufferedWriter out = new BufferedWriter(new FileWriter(new File("F:\\xampp\\htdocs\\mc\\store\\"+name), false));
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					out.write(line.replaceAll("\\\r\\\n", "") + "\r\n");
				}
				in.close();
				out.close();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void sendInventoryToStore(Player player){
		try{
			File file = new File(plugin.getDataFolder() + File.separator + "inventories" + File.separator, player.getName()+".yml");
				String name = file.getName();
				BufferedWriter out = new BufferedWriter(new FileWriter(new File("F:\\xampp\\htdocs\\mc\\store\\"+name), false));
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					out.write(line.replaceAll("\\\r\\\n", "") + "\r\n");
				}
				in.close();
				out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void copyFile(){
		File original = new File("F:\\xampp\\htdocs\\mc\\store\\purchases.txt");
		File localFile = new File("plugins\\Economy\\purchases.LIST");
		try{
			BufferedReader in = new BufferedReader(new FileReader(original));
			BufferedWriter out = new BufferedWriter(new FileWriter(localFile, false));
			String line;
			while((line = in.readLine()) != null){
				out.write(line.replaceAll("\\\r\\\n", "") + "\r\n");
			}
			out.close();
			in.close();
			out = new BufferedWriter(new FileWriter(original, false));
			out.write("");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	private void copyOfflineFile(){
		File original = new File("plugins\\Economy\\OfflinePurchases.txt");
		File localFile = new File("plugins\\Economy\\purchases_offline.LIST");
		try{
			BufferedReader in = new BufferedReader(new FileReader(original));
			BufferedWriter out = new BufferedWriter(new FileWriter(localFile, false));
			String line;
			while((line = in.readLine()) != null){
				out.write(line.replaceAll("\\\r\\\n", "") + "\r\n");
			}
			out.close();
			in.close();
			out = new BufferedWriter(new FileWriter(original, false));
			out.write("");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void scheduleHandle(){
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
		        handle();
		    }
		}, STORE_READ*20, 200L); //20 ticks/second
	}
	
	private void alertWealth(Player player){
		MoneyHandler wallet = new MoneyHandler(plugin, player);
		String ballance = wallet.getBallanceAsString();
		player.sendMessage(ChatColor.GOLD + "[Store] " + ChatColor.DARK_AQUA + "Your online purchases have been proccessed. Your current ballance is: "+ballance);
	}
	
	private void addToOfflineList(String line){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/OfflinePurchases.txt"), true));
			out.write(line);
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}

	private class StoreHandlerThread implements Runnable{

		private WorldLauncher plugin;
		private StoreHandler superClass;
		
		public StoreHandlerThread(WorldLauncher plugin, StoreHandler superClass){
			this.plugin = plugin;
			this.superClass = superClass;
		}
		
		public void run() {
			try{
				BufferedReader in = new BufferedReader(new FileReader(new File("plugins/Economy/purchases.LIST")));
				String line;
				while((line = in.readLine()) != null){
					String parts[] = line.replaceAll("\\\r\\\n", "").split(":");
					String playerName = parts[0];
					String inventoryYML = parts[1];
					String bankYML = parts[2];
					double newBallance = Double.parseDouble(parts[3]);
					Player player = Bukkit.getServer().getPlayer(playerName);
					if(player.isOnline()){
						MoneyHandler wallet = new MoneyHandler(plugin, player);
						wallet.setBallance(newBallance);
						ChestHandler bank = new ChestHandler(plugin);
						bank.setBankContents(new File(bankYML), player);
						InventoryHandler inventory = new InventoryHandler(plugin, player);
						inventory.setInventory(new File(inventoryYML));
						superClass.alertWealth(player);
					}else{
						superClass.addToOfflineList(line.replaceAll("\\\r\\\n", "")+"\r\n");
					}
				}
				in.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			superClass.sendInventoriesToStore();
			plugin.logger.log(Level.INFO, "Inventories Pinged.");
			ChatLog.log_console_message("Inventories Pinged.");
		}
		
	}

	private class StoreOfflineHandlerThread implements Runnable{

		private WorldLauncher plugin;
		private StoreHandler superClass;
		private Player player;
		
		public StoreOfflineHandlerThread(WorldLauncher plugin, StoreHandler superClass, Player player){
			this.plugin = plugin;
			this.superClass = superClass;
			this.superClass.copyOfflineFile();
			this.player = player;
		}
		
		public void run() {
			try{
				BufferedReader in = new BufferedReader(new FileReader(new File("plugins/Economy/purchases_offline.LIST")));
				String line;
				while((line = in.readLine()) != null){
					String parts[] = line.replaceAll("\\\r\\\n", "").split(":");
					String playerName = parts[0];
					String inventoryYML = parts[1];
					String bankYML = parts[2];
					double newBallance = Double.parseDouble(parts[3]);
					Player player = Bukkit.getServer().getPlayer(playerName);
					if(player.isOnline()){
						if(player.getName() == this.player.getName()){
							MoneyHandler wallet = new MoneyHandler(plugin, player);
							wallet.setBallance(newBallance);
							ChestHandler bank = new ChestHandler(plugin);
							bank.setBankContents(new File(bankYML), player);
							InventoryHandler inventory = new InventoryHandler(plugin, player);
							inventory.setInventory(new File(inventoryYML));
							superClass.alertWealth(player);
						}
					}else{
						superClass.addToOfflineList(line.replaceAll("\\\r\\\n", "")+"\r\n");
					}
				}
				in.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			superClass.sendInventoriesToStore();
		}
		
	}
}
