package economy.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import economy.bank.ChestHandler;
import economy.economy.MoneyHandler;
import economy.economy.Transaction;
import economy.external.StoreHandler;
import economy.inventory.InventoryWriter;

public class WorldLauncher extends JavaPlugin{
	public WorldLauncher plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	public World PVP_WORLD;
	public World SURVIVAL_WORLD;
	
	public final ServerBlockListener blockListener = new ServerBlockListener(this);
	public final ServerPlayerListener playerListener = new ServerPlayerListener(this);
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Highest, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		try{
			File file = new File("plugins/Economy/items.LIST");
			if(!file.exists()){
				BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
				for(int i=0;i<2266;i++){
					if(Material.getMaterial(i) != null){
						out.write(Material.getMaterial(i).name()+": 0\r\n");
					}
				}
				out.close();
			}
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		StoreHandler store = new StoreHandler(this);
		store.scheduleHandle();
	}
	
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmdcmd, String cmd, String[] args){
		ChestHandler chests = new ChestHandler(this);
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(!isAdmin(player) && !isBuilder(player)){
				if(cmd.equalsIgnoreCase("sellchest")){
					Transaction transaction = new Transaction(plugin, player);
					Block target = player.getTargetBlock(null, 10);
					if(target != null){
						if(target.getType() == Material.CHEST){
							Chest chest = (Chest) target.getState();
							transaction.sellChest(chest);
						}else{
							player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.RED + "That's not a chest!");
						}
					}else{
						player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.RED + "No chest targetted!");
					}
				}else if(cmd.equalsIgnoreCase("sellall")){
					Transaction transaction = new Transaction(plugin, player);
					transaction.sellInventory();
				}else if(cmd.equalsIgnoreCase("sell")){
					try{
						if(args.length == 1){
							String item = args[0].toUpperCase();
							int amount = -1;
							ItemStack stack = new ItemStack(Material.getMaterial(item));
							if(stack != null){
								Transaction transaction = new Transaction(plugin, player);
								transaction.sellItem(stack.getType().name(), amount);
							}else{
								player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Item not found!");
							}
						}else if(args.length == 2){
							String item = args[0].toUpperCase();
							int amount = Integer.parseInt(args[1]);
							ItemStack stack = new ItemStack(Material.getMaterial(item), amount);
							if(stack != null){
								Transaction transaction = new Transaction(plugin, player);
								transaction.sellItem(stack.getType().name(), amount);
							}else{
								player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Item not found!");
							}
						}else{
							player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Incorrect number of arguments! /ehelp");
						}
					}catch (Exception e){
						player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Item not found! Misstype?");
						e.printStackTrace();
					}
				}else if(cmd.equalsIgnoreCase("buy")){
					try{
						if(args.length == 1){
							String item = args[0].toUpperCase();
							int amount = 1;
							ItemStack stack = new ItemStack(Material.getMaterial(item), amount);
							if(stack != null){
								Transaction transaction = new Transaction(plugin, player);
								transaction.buyItem(stack.getType().name(), amount);
							}else{
								player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Item not found!");
							}
						}else if(args.length == 2){
							String item = args[0].toUpperCase();
							int amount = Integer.parseInt(args[1]);
							ItemStack stack = new ItemStack(Material.getMaterial(item), amount);
							if(stack != null){
								Transaction transaction = new Transaction(plugin, player);
								transaction.buyItem(stack.getType().name(), amount);
							}else{
								player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Item not found!");
							}
						}else{
							player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Incorrect number of arguments! /ehelp");
						}
					}catch (Exception e){
						player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Item not found! Misstype?");
					}
				}else if(cmd.equalsIgnoreCase("wealth")){
					MoneyHandler wallet = new MoneyHandler(plugin, player);
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.AQUA+"Your current ballance is: "+wallet.getBallanceAsString());
				}else if(cmd.equalsIgnoreCase("mbank")){
					if(chests.hasMBank(player)){
						player.sendMessage(ChatColor.GOLD + "[Bank] " + ChatColor.RED + "You have a mobile bank active at the moment!");
					}else{
						chests.spawnMBank(player);
					}
				}else if(cmd.equalsIgnoreCase("mstore")){
					if(!chests.hasMBank(player)){
						player.sendMessage(ChatColor.GOLD + "[Bank] " + ChatColor.RED + "You do not have a mobile bank active at the moment!");
					}else{
						chests.despawnMBank(player, player.getTargetBlock(null, 10).getLocation());
					}
				}else if(cmd.equalsIgnoreCase("esave")){
					InventoryWriter invwriter = new InventoryWriter(player, this);
					invwriter.write();
					player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.AQUA + "Saved!");
				}else{
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"Help Menu: ");
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/sellall  - Sell your entire inventory");
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/sell <item> [OPT: Amount]  - Sell <item> (Optional: amount, default is all)");
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/buy <item> [OPT: Amount]  - Sell <item> (Optional: amount, default is 1)");
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/sellchest  - Sell the currently-targetted chest");
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/mbank  - Summmons a mobile bank for 30 seconds");
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/esave  - Saves your inventory for use online (also occurs on join/leave)");
					//player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED+"/mstore  - Hides your summoned mobile bank");
				}
			}else{
				if(cmd.equalsIgnoreCase("invget")){
					Player players[] = Bukkit.getOnlinePlayers();
					for(Player onlinePlayer : players){
						InventoryWriter invwriter = new InventoryWriter(onlinePlayer, this);
						invwriter.write();
					}
					player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.AQUA + "Done!");
				}else if(cmd.equalsIgnoreCase("spawnnpc")){
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "Not yet implemented.");
				}else if(cmd.equalsIgnoreCase("mset")){
					chests.setBankChest(player, player.getTargetBlock(null, 10).getLocation());
					player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.AQUA + "Set!");
				}else if(cmd.equalsIgnoreCase("munset")){
					chests.unsetBankChest(player, player.getTargetBlock(null, 10).getLocation());
					player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.AQUA + "Removed!");
				}else{
					player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "You must not be an OP or Builder to use the Economy!");
				}
			}
			if(isAdmin(player)){
				if(cmd.equalsIgnoreCase("mset")){
					
				}
			}
		}else{
			if(cmd.equalsIgnoreCase("invget")){
				Player players[] = Bukkit.getOnlinePlayers();
				for(Player onlinePlayer : players){
					InventoryWriter invwriter = new InventoryWriter(onlinePlayer, plugin);
					invwriter.write();
				}
				logger.log(Level.INFO, "[Economy] Done!");
			}else{
				logger.log(Level.WARNING, "[Economy] You must be in-game to do that!");
			}
			
		}
		return false;
	}
	
	public boolean isAdmin(Player player){
		boolean admin = false;
		try{
			File f = new File("admins.txt");
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line;
			while ((line = in.readLine()) != null){
				if(line.toLowerCase().startsWith(player.getName().toLowerCase())){
					admin = true;
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return admin;
	}
	
	public boolean isBuilder(Player player){
		boolean builder = false;
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/AMS_GrantedPlayers/"+player.getName()+".PLAYER")));
			String line;
			while((line = in.readLine()) != null){
				if(line.toLowerCase().startsWith("global")){
					builder = true;
					break;
				}
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return builder;
	}
}
