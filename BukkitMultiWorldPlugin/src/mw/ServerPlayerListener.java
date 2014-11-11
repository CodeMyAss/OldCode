package mw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ServerPlayerListener extends PlayerListener{
	public static WorldLauncher plugin;
	
	public ServerPlayerListener(WorldLauncher ams){
		plugin = ams;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getClickedBlock() != null){
			Location location = event.getClickedBlock().getLocation();
			if(isPortBlock(location)){
				String worldname = getPortBlock(location);
				if(worldname!=null){
					plugin.port(worldname, event.getPlayer());
				}
			}
		}
	}
	
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE){
			if(!isAuthorizedGMChange(player)){
				player.setGameMode(GameMode.SURVIVAL);
			}
		}
	}
	
	public boolean isAuthorizedGMChange(Player p) {
		boolean isAllowed = false;
		if(isBuilder(p)){
			isAllowed = true;
		}
		if(p.isOp()){
			isAllowed = true;
		}
		return isAllowed;
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

	public boolean isPortBlock(Location location){
		boolean isPort = false;
		File dir = new File("plugins/MultiWorld/");
		File files[] = dir.listFiles(new FileFilter(){
			public boolean accept(File file){
				boolean acc = false;
				String parts[] = file.toString().split("\\.");
				if(parts[parts.length-1].equalsIgnoreCase("portal")){
					acc = true;
				}
				return acc;
			}
		});
		boolean found = false;
		for(File file : files){
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					if(line.endsWith(location.getWorld().getName())){
						String parts[] = line.split("\\.\\.");
						double x = Double.parseDouble(parts[0]);
						double y = Double.parseDouble(parts[1]);
						double z = Double.parseDouble(parts[2]);
						Location port = new Location(location.getWorld(), x, y, z);
						if(port.distance(location) < 1){
							found = true;
							break;
						}
					}
				}
				in.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			if(found){
				break;
			}
		}
		isPort = found;
		return isPort;
	}
	
	public String getPortBlock(Location location){
		File dir = new File("plugins/MultiWorld/");
		File files[] = dir.listFiles(new FileFilter(){
			public boolean accept(File file){
				boolean acc = false;
				String parts[] = file.toString().split("\\.");
				if(parts[parts.length-1].equalsIgnoreCase("portal")){
					acc = true;
				}
				return acc;
			}
		});
		String retWorld = "";
		boolean found = false;
		for(File file : files){
			try{
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while((line = in.readLine()) != null){
					if(line.endsWith(location.getWorld().getName())){
						String parts[] = line.split("\\.\\.");
						double x = Double.parseDouble(parts[0]);
						double y = Double.parseDouble(parts[1]);
						double z = Double.parseDouble(parts[2]);
						Location port = new Location(location.getWorld(), x, y, z);
						if(port.distance(location) < 1){
							String world = "";
							parts = file.toString().split("MultiWorld");
							world = parts[parts.length-1].replaceAll("\\.PORTAL", "").replaceAll("\\\\", "").replaceAll("/", "");
							retWorld = world;
							found = true;
							break;
						}
					}
				}
				in.close();
			}catch (Exception e){
				ChatLog.log_error(e.getMessage());
			}
			if(found){
				break;
			}
		}
		return retWorld;
	}
}
