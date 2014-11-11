package economy.economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import economy.plugin.WorldLauncher;

public class Transaction {
	
	public WorldLauncher plugin;
	public Player player;
	public MoneyHandler wallet;
	
	public Transaction(WorldLauncher plugin, Player player){
		this.plugin = plugin;
		this.player = player;
		wallet = new MoneyHandler(this.plugin, this.player);
	}
	
	@SuppressWarnings("deprecation")
	public void sellItem(String item, int amount){
		item = item.toUpperCase();
		if(amount == -1){
			ItemStack inventory[] = player.getInventory().getContents();
			for(ItemStack stack : inventory){
				if(stack != null){
					if(stack.getType() == Material.getMaterial(item)){
						amount += stack.getAmount();
					}
				}
			}
		}
		ProductListing listing = new ProductListing(player, plugin);
		if(hasItem(player.getInventory(), new ItemStack(Material.getMaterial(item), amount))){
			double oldBallance = wallet.getBallance();
			ItemStack inventory[] = player.getInventory().getContents();
			int totalSold = 0;
			for(ItemStack stack : inventory){
				if(stack != null){
					if(stack.getType() == Material.getMaterial(item)){
						if(totalSold == amount){
							break;
						}
						int stkamount = stack.getAmount();
						if(totalSold + stkamount <= amount){
							wallet.deposit(listing.getCost(stack)*stkamount);
							player.getInventory().remove(stack);
							totalSold += stkamount;
						}
					}
				}
			}
			double profit = wallet.getBallance() - oldBallance;
			String ballance = ""+profit;
			String parts[] = ballance.split("\\.");
			if(parts[parts.length-1].length() < 2){
				ballance = ballance + "0";
			}
			player.updateInventory();
			History history = new History(player, plugin);
			history.sellItem(item, amount);
			history.ballanceChange(oldBallance, wallet.getBallance());
			player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.GREEN + "You sold "+totalSold+" item(s).");
			player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.GREEN + "Your new ballance is: "+wallet.getBallanceAsString()+" (Profit: +"+ballance+")");
		}else{
			player.sendMessage(ChatColor.GOLD + "[Economy] "+ChatColor.RED + "You do not have that item or have enough to sell.");
		}
	}
	
	public void buyItem(String item, int amount){
		item = item.toUpperCase();
		if(player.getInventory().firstEmpty() > -1){
			ItemStack stack = new ItemStack(Material.getMaterial(item));
			ProductListing listing = new ProductListing(player, plugin);
			double coins = listing.getCost(stack)*amount;
			if(wallet.getBallance() >= coins){
				double oldBallance = wallet.getBallance();
				wallet.withdraw(coins);
				stack.setAmount(amount);
				History history = new History(player, plugin);
				history.buyItem(stack.getType().name(), amount);
				history.ballanceChange(oldBallance, wallet.getBallance());
				player.getInventory().setItem(player.getInventory().firstEmpty(), stack);	
				double ballance = oldBallance - wallet.getBallance();
				player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.GREEN + "Your new ballance is: "+wallet.getBallanceAsString()+" (Deficit: -"+ballance+")");
			}else{
				player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.RED +"You do not have enough money!");
			}
		}else{
			player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.RED +"You do not have enough inventory space!");
		}
	}
	
	private boolean hasItem(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                count += items[i].getAmount();
            }
            if (count >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }
	
	public void sellInventory(){
		ProductListing listing = new ProductListing(player, plugin);
		ItemStack items[] = player.getInventory().getContents();
		double oldBallance = wallet.getBallance();
		History history = new History(player, plugin);
		for(ItemStack item : items){
			if(item != null){
				double worth = listing.getCost(item)*item.getAmount();
				wallet.deposit(worth);
				history.sellItem(item.getType().name(), item.getAmount());
			}
		}
		player.getInventory().clear();
		double profit = wallet.getBallance() - oldBallance;
		String ballance = ""+profit;
		String parts[] = ballance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			ballance = ballance + "0";
		}
		history.ballanceChange(oldBallance, wallet.getBallance());
		player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.GREEN + "Your new ballance is: "+wallet.getBallanceAsString()+" (Profit: +"+ballance+")");
	}
	
	public void sellChest(Chest chest){
		ProductListing listing = new ProductListing(player, plugin);
		ItemStack items[] = chest.getInventory().getContents();
		double oldBallance = wallet.getBallance();
		History history = new History(player, plugin);
		for(ItemStack item : items){
			if(item != null){
				double worth = listing.getCost(item)*item.getAmount();
				wallet.deposit(worth);
				item.setAmount(0);
				history.sellItem(item.getType().name(), item.getAmount());
			}
		}
		wallet.deposit(listing.getCost(new ItemStack(Material.CHEST, 1)));
		double profit = wallet.getBallance() - oldBallance;
		String ballance = ""+profit;
		String parts[] = ballance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			ballance = ballance + "0";
		}
		history.ballanceChange(oldBallance, wallet.getBallance());
		chest.getInventory().clear();
		Block block = chest.getBlock();
		block.setType(Material.AIR);
		player.sendMessage(ChatColor.GOLD + "[Economy] " + ChatColor.GREEN + "Your new ballance is: "+wallet.getBallanceAsString()+" (Profit: +"+ballance+")");
	}
}
