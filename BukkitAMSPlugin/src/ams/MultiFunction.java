package ams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

//import ChatLogger.ChatLog;

public class MultiFunction {
	
	private static World CONTEST_WORLD = Bukkit.getWorld("contest");
	
	public static boolean isSpawnProtected(Player player){
		if(player.isOp()){
			return false;
		}
		return true;
	}
	
	public static boolean hasPermission(Player player){
		boolean permitted = false;
		File file = new File("plugins/AMS_GrantedPlayers/"+player.getName()+".PLAYER");
		if(!file.exists()){
			permitted = false;
		}else{
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					line = line.replaceAll("\\\\r\\\\n", "");
					String parts[] = line.split("\\.");
					if(parts[0].equalsIgnoreCase("GLOBAL")){
						permitted = true;
						break;
					}else if(parts[0].equalsIgnoreCase(getZone(player.getLocation())) && parts[1].equalsIgnoreCase(player.getWorld().getName())){
						permitted = true;
						break;
					}
				}
				in.close();
			}catch(Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
		if(player.isOp()){
			permitted = true;
		}
		return permitted;
	}
	
	public static boolean isAdmin(Player player){
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
	
	public static boolean hasPermission(Player player, Location location){
		boolean permitted = false;
		File file = new File("plugins/AMS_GrantedPlayers/"+player.getName()+".PLAYER");
		if(!file.exists()){
			permitted = false;
		}else{
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					line = line.replaceAll("\\\\r\\\\n", "");
					String parts[] = line.split("\\.");
					if(line.equalsIgnoreCase("GLOBAL")){
						permitted = true;
						break;
					}else if(parts[0].equalsIgnoreCase(getZone(location)) && parts[1].equalsIgnoreCase(player.getWorld().getName()) && (player.getWorld() != CONTEST_WORLD)){
						permitted = true;
						break;
					}
				}
				in.close();
			}catch(Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
		if(player.isOp()){
			permitted = true;
		}
		return permitted;
	}
	
	public static boolean hasPermission(Player player, Location location, boolean checkSpawn){
		boolean permitted = false;
		File file = new File("plugins/AMS_GrantedPlayers/"+player.getName()+".PLAYER");
		if(!file.exists()){
			permitted = false;
		}else{
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					line = line.replaceAll("\\\\r\\\\n", "");
					String parts[] = line.split("\\.");
					if(line.equalsIgnoreCase("GLOBAL")){
						permitted = true;
						if(getZone(location).equalsIgnoreCase("spawn") || location.getWorld() == CONTEST_WORLD){
							permitted = false;
						}
						break;
					}else if(parts[0].equalsIgnoreCase(getZone(location)) && parts[1].equalsIgnoreCase(player.getWorld().getName())  && (player.getWorld() != CONTEST_WORLD)){
						permitted = true;
						break;
					}
				}
				in.close();
			}catch(Exception e){
				ChatLog.log_error(e.getMessage());
			}
		}
		if(player.isOp()){
			permitted = true;
		}
		return permitted;
	}
	
	public static void reportPlayer(Player player){
		Player players[] = Bukkit.getOnlinePlayers();
		for(Player online : players){
			if(online.isOp()){
				online.sendMessage(ChatColor.GOLD + "[AMS] " + ChatColor.RED + "Player " + player.getName() + " tried to edit " + getZone(player) + " in world "+player.getWorld().getName());
			}
		}
		try{
			DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			Date date = new Date();
	      	String timestamp = dateFormat.format(date);
			BufferedWriter out = new BufferedWriter(new FileWriter("AMS_REPORTED.txt", true));
			out.write("["+timestamp+"] Player " + player.getName() + " tried to do an action in zone " + getZone(player) + "\r\n");
			out.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public static String getZone(Player player){
		String zone = "";
		String world = player.getWorld().getName();
		Location location = player.getLocation();
		File dir = new File("plugins/AMS/"+world+"/");
		File files[] = dir.listFiles();
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split(world);
			String name = parts[parts.length-1];
			parts = name.split("\\.\\.");
			double X_MIN = Double.parseDouble(parts[0]);
			double Y_MIN = Double.parseDouble(parts[1]);
			double X_MAX = Double.parseDouble(parts[2]);
			double Y_MAX = Double.parseDouble(parts[3]);
			if(X_MIN>X_MAX){
				double temp = X_MAX;
				X_MAX = X_MIN;
				X_MIN = temp;
			}
			if(Y_MIN>Y_MAX){
				double temp = Y_MAX;
				Y_MAX = Y_MIN;
				Y_MIN = temp;
			}
			double lx = location.getX();
			double ly = location.getZ();
			if(lx>X_MIN && lx<X_MAX && ly>Y_MIN && ly<Y_MAX){
				zone = parts[4];
				zone = zone.replaceAll("\\.ZONE", "");
				break;
			}
		}
		return zone;
	}
	
	public static String getZone(Location location){
		String world = location.getWorld().getName();
		String zone = "";
		File dir = new File("plugins/AMS/"+world+"/");
		File files[] = dir.listFiles();
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split(world);
			String name = parts[parts.length-1];
			parts = name.split("\\.\\.");
			double X_MIN = Double.parseDouble(parts[0]);
			double Y_MIN = Double.parseDouble(parts[1]);
			double X_MAX = Double.parseDouble(parts[2]);
			double Y_MAX = Double.parseDouble(parts[3]);
			if(X_MIN>X_MAX){
				double temp = X_MAX;
				X_MAX = X_MIN;
				X_MIN = temp;
			}
			if(Y_MIN>Y_MAX){
				double temp = Y_MAX;
				Y_MAX = Y_MIN;
				Y_MIN = temp;
			}
			double lx = location.getX();
			double ly = location.getZ();
			if(lx>X_MIN && lx<X_MAX && ly>Y_MIN && ly<Y_MAX){
				zone = parts[4];
				zone = zone.replaceAll("\\.ZONE", "");
				break;
			}
		}
		return zone;
	}
	
	public static boolean isRestricted(Location location){
		boolean ret = true;
		String world = location.getWorld().getName();
		File dir = new File("plugins/AMS/"+world+"/");
		File files[] = dir.listFiles();
		for (File file : files){
			String fname = file.getName();
			fname.replace("\\\\", "");
			fname.replace("/", "");
			String parts[] = fname.split(world);
			String name = parts[parts.length-1];
			parts = name.split("\\.\\.");
			double X_MIN = Double.parseDouble(parts[0]);
			double Y_MIN = Double.parseDouble(parts[1]);
			double X_MAX = Double.parseDouble(parts[2]);
			double Y_MAX = Double.parseDouble(parts[3]);
			if(X_MIN>X_MAX){
				double temp = X_MAX;
				X_MAX = X_MIN;
				X_MIN = temp;
			}
			if(Y_MIN>Y_MAX){
				double temp = Y_MAX;
				Y_MAX = Y_MIN;
				Y_MIN = temp;
			}
			double lx = location.getX();
			double ly = location.getZ();
			if(lx>X_MIN && lx<X_MAX && ly>Y_MIN && ly<Y_MAX){
				ret = true;
				break;
			}else{
				ret = false;
			}
		}
		return ret;
	}
	
	public static boolean isClaimed(String zone, Player player){
		boolean claimed = true;
		if(player.getWorld() == CONTEST_WORLD){
			File file = new File("plugins/AMS_Claims/"+zone+".ZONE");
			if(!file.exists()){
				claimed = false;
			}
		}
		return claimed;
	}
	
	public static void claim(String zone, Player player){
		File file = new File("plugins/AMS_Claims/"+zone+".ZONE");
		try{
			file.createNewFile();
		}catch (Exception e){
			e.printStackTrace();
		}
		file = new File("plugins/AMS_GrantedPlayers/"+player+".PLAYER");
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.write(zone+"."+player.getWorld().getName());
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
