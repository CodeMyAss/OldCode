package server.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

//import ChatLogger.ChatLog;

public class FWrite {
	
	public static String worldname;
	public static String server_filename;
	
	public FWrite(){
		List<World> worlds = Bukkit.getWorlds();
		if(worlds.size() > 0){
			worldname = worlds.get(0).getName();
		}else{
			worldname = "NotLoaded";
		}
		server_filename = "F:\\xampp\\htdocs\\mc\\servers\\"+worldname+".SERVER";
	}
	
	public void wipe(){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(server_filename, false));
			writer.write("");
			writer.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}

	public void writeIP(String ip){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(server_filename, true));
			writer.write(ip + " : ");
			writer.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void writeState(String state){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(server_filename, true));
			writer.write(state + " : ");
			writer.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void writeName(String name){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(server_filename, true));
			writer.write(name + " : ");
			writer.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void writeAccountType(boolean supportsHacked){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(server_filename, true));
			writer.write(supportsHacked + " : ");
			writer.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
	public void writePopulation(String population){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(server_filename, true));
			writer.write(population);
			writer.close();
		}catch(Exception e){
			ChatLog.log_error(e.getMessage());
		}
	}
	
}
