package broadcaster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Launcher extends JavaPlugin{

	public static Launcher plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public static int currentLine = 0;
	public static int tid = 0;
	public static int running = 1;
	public static long interval = 600; //Seconds
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}

	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File("plugins/Broadcast/interval.txt")));
			String line = in.readLine();
			interval = Integer.parseInt(line);
			in.close();
		}catch (Exception e){
			ChatLog.log_error(e.getMessage());
		}
		
		tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run(){
				try{
					broadcastMessage("plugins/Broadcast/messages.txt");
				}catch (IOException e){
					ChatLog.log_error(e.getMessage());
				}
			}
		}, 0, interval*20);
	}
	
	public void broadcastMessage(String file) throws IOException{
		FileInputStream fs = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
		for(int i=0;i<currentLine;++i){
			reader.readLine();
		}
		String line = reader.readLine();
		String line_output = line;
		line = line.replaceAll("&f", ChatColor.WHITE + "");
		line = line.replaceAll("&e", ChatColor.YELLOW + "");
		line = line.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
		line = line.replaceAll("&a", ChatColor.GREEN + "");
		
		line_output = line_output.replaceAll("&f", "");
		line_output = line_output.replaceAll("&e", "");
		line_output = line_output.replaceAll("&d", "");
		line_output = line_output.replaceAll("&a", "");
		line_output = line_output.replaceAll("\\>", "&gt;");
		line_output = line_output.replaceAll("\\<", "&lt;");
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "[Broadcast] " + ChatColor.AQUA + line);
		ChatLog.log_console_message("[Broadcast] "+line_output);
		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(file)));
		lnr.skip(Long.MAX_VALUE);
		int lastLine = lnr.getLineNumber();
		if(currentLine + 1 == lastLine + 1){
			currentLine = 0;
		}else{
			currentLine++;
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("BC_STOP")){
			if(running == 1){
				Bukkit.getServer().getScheduler().cancelTask(tid);
				Player player = (Player) sender;
				player.sendMessage("Broadcasts disabled.");
				running = 0;
			}else{
				Player player = (Player) sender;
				player.sendMessage("Broadcasts already off.");
			}
		}else if(commandLabel.equalsIgnoreCase("BC_START")){
			if(running == 0){
				tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					public void run(){
						try{
							broadcastMessage("plugins/Broadcast/messages.txt");
						}catch (IOException e){
							ChatLog.log_error(e.getMessage());
						}
					}
				}, 0, interval*20);
				Player player = (Player) sender;
				player.sendMessage("Broadcasts started.");
				running = 1;
			}else{
				Player player = (Player) sender;
				player.sendMessage("Broadcasts already on.");
			}
		}else if(commandLabel.equalsIgnoreCase("BC_TIME")){
			interval = Integer.parseInt(args[0]);
			try{
				BufferedWriter out = new BufferedWriter(new FileWriter(new File("plugins/Broadcast/interval.txt"), false));
				out.write((int) interval);
				out.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			if(running == 0){
				tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					public void run(){
						try{
							broadcastMessage("plugins/Broadcast/messages.txt");
						}catch (IOException e){
							ChatLog.log_error(e.getMessage());
						}
					}
				}, 0, interval*20);
				Player player = (Player) sender;
				player.sendMessage("Broadcasts restarted (Interval = "+interval+" seconds)");
				running = 1;
			}else{
				Bukkit.getServer().getScheduler().cancelTask(tid);
				tid = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
					public void run(){
						try{
							broadcastMessage("plugins/Broadcast/messages.txt");
						}catch (IOException e){
							ChatLog.log_error(e.getMessage());
						}
					}
				}, 0, interval*20);
				Player player = (Player) sender;
				player.sendMessage("Broadcasts restarted (Interval = "+interval+" seconds)");
			}
		}
		return false;
	}
}
