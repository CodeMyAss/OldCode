package economy.economy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

public class History {
	
	public WorldLauncher plugin;
	public Player player;
	
	public History(Player player, WorldLauncher plugin){
		this.player = player;
		this.plugin = plugin;
	}
	
	public void sellItem(String itemName, int amount){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/player_history/"+player.getName()+".HISTORY"), true));
			DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			Date date = new Date();
			String timestamp = dateFormat.format(date);
			double cost = new ProductListing(player, plugin).getCost(new ItemStack(Material.getMaterial(itemName)))*amount;
			double pper = new ProductListing(player, plugin).getCost(new ItemStack(Material.getMaterial(itemName)));
			String value = ""+cost;
			String parts[] = value.split("\\.");
			if(parts[parts.length-1].length() < 2){
				value = value + "0";
			}
			String pricePer = ""+pper;
			parts = pricePer.split("\\.");
			if(parts[parts.length-1].length() < 2){
				pricePer = pricePer + "0";
			}
			out.write("["+timestamp+"] Sold item "+itemName+" x"+amount+" for "+value+" ("+pricePer+" each).\r\n");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void buyItem(String itemName, int amount){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/player_history/"+player.getName()+".HISTORY"), true));
			DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			Date date = new Date();
			String timestamp = dateFormat.format(date);
			double cost = new ProductListing(player, plugin).getCost(new ItemStack(Material.getMaterial(itemName)))*amount;
			double pper = new ProductListing(player, plugin).getCost(new ItemStack(Material.getMaterial(itemName)));
			String value = ""+cost;
			String parts[] = value.split("\\.");
			if(parts[parts.length-1].length() < 2){
				value = value + "0";
			}
			String pricePer = ""+pper;
			parts = pricePer.split("\\.");
			if(parts[parts.length-1].length() < 2){
				pricePer = pricePer + "0";
			}
			out.write("["+timestamp+"] Bought item "+itemName+" x"+amount+" for "+value+" ("+pricePer+" each).\r\n");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void ballanceChange(double oldBallance, double newBallance){

		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/player_history/"+player.getName()+".HISTORY"), true));
			DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			Date date = new Date();
			String timestamp = dateFormat.format(date);
			String nBallance = ""+newBallance;
			String parts[] = nBallance.split("\\.");
			if(parts[parts.length-1].length() < 2){
				nBallance = nBallance + "0";
			}
			String oBallance = ""+oldBallance;
			parts = oBallance.split("\\.");
			if(parts[parts.length-1].length() < 2){
				oBallance = oBallance + "0";
			}
			out.write("["+timestamp+"] Ballance change from "+oBallance+" to "+nBallance+".\r\n");
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
}
