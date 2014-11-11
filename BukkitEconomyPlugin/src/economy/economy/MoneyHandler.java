package economy.economy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

import economy.plugin.ChatLog;
import economy.plugin.WorldLauncher;

@SuppressWarnings("deprecation")
public class MoneyHandler {
		
	public WorldLauncher plugin;
	public Player player;
	
	public MoneyHandler(WorldLauncher plugin, Player player){
		this.plugin = plugin;
		this.player = player;
	}
	
	public void export(){
		File saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", "coins.yml");
	    Configuration config = new Configuration(saveFile);
	    config.load();
	    config.setProperty(player.getName()+".ballance", getBallanceAsString());
	    config.save();
	}
	
	public void withdraw(double coin){
		try{
			double newBallance = getBallance() - coin;
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/players/"+player.getName()+".PLAYER"), false));
			out.write(""+newBallance);
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void deposit(double coin){
		try{
			double newBallance = coin + getBallance();
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/players/"+player.getName()+".PLAYER"), false));
			out.write(""+newBallance);
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void setBallance(double newBallance){
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/players/"+player.getName()+".PLAYER"), false));
			out.write(""+newBallance);
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void takeAmount(double coin){
		try{
			double newBallance = getBallance() - coin;
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/players/"+player.getName()+".PLAYER"), false));
			out.write(""+newBallance);
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		History history = new History(player, plugin);
		history.ballanceChange(getBallance()+coin, getBallance());
	}
	
	public void giveAmount(double coin){
		try{
			double newBallance = coin + getBallance();
			BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Economy/players/"+player.getName()+".PLAYER"), false));
			out.write(""+newBallance);
			out.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		History history = new History(player, plugin);
		history.ballanceChange(getBallance()-coin, getBallance());
	}
	
	public double getBallance(){
		double ballance = 0.0;
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/Economy/players/"+player.getName()+".PLAYER")));
			String line;
			while((line = in.readLine()) != null){
				ballance = Double.parseDouble(line.replaceAll("\\\r\\\n", ""));
			}
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		return ballance;
	}
	
	public String getBallanceAsString(){
		String ballance = ""+getBallance();
		String parts[] = ballance.split("\\.");
		if(parts[parts.length-1].length() < 2){
			ballance = ballance + "0";
		}
		return ballance;
	}
	
	public void payPlayer(Player player, double coin){
		this.withdraw(coin);
		MoneyHandler merchant = new MoneyHandler(plugin, player);
		merchant.deposit(coin);
	}
}
