package server.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class ChatLog {
	
  public static void log_whisper(String to, String from, String message, char color_to, char color_from) {
    try {
      List<World> worlds = Bukkit.getWorlds();
      String worldname = worlds.get(0).getName();
      String filename = worldname+".LOG";
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
      out.write("Whisper: " + from + " &gt;&gt;&gt; " + to + ": " + message + "<br>\r\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void log_message(String from, String message, boolean color) {
    try {
      List<World> worlds = Bukkit.getWorlds();
      String worldname = worlds.get(0).getName();
      String filename = worldname+".LOG";
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
      String op = "";
      if(color){
    	  op = " [OP]";
      }
      DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
      Date date = new Date();
      String timestamp = dateFormat.format(date);
      out.write("["+timestamp + "] &lt;" + from + "&gt;"+op+" : " + message + "<br>\r\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void log_server_message(String message) {
    try {
      List<World> worlds = Bukkit.getWorlds();
      String worldname = worlds.get(0).getName();
      String filename = worldname+".LOG";
      DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
      Date date = new Date();
      String timestamp = dateFormat.format(date);
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
      out.write("["+timestamp + "] &lt;SERVER&gt; : " + message + "<br>\r\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void log_console_message(String message) {
    try {
      List<World> worlds = Bukkit.getWorlds();
      String worldname = worlds.get(0).getName();
      String filename = worldname+".LOG";
      DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
      Date date = new Date();
      String timestamp = dateFormat.format(date);
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
      out.write("["+timestamp + "] &lt;CONSOLE&gt; : " + message + "<br>\r\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void log_warning(String message) {
    try {
      List<World> worlds = Bukkit.getWorlds();
      String worldname = worlds.get(0).getName();
      String filename = worldname+".LOG";
      DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
      Date date = new Date();
      String timestamp = dateFormat.format(date);
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
      out.write("["+timestamp + "] &lt;WARNING&gt; : " + message + "<br>\r\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void log_error(String message) {
    try {
      List<World> worlds = Bukkit.getWorlds();
      String worldname = worlds.get(0).getName();
      String filename = worldname+".LOG";
      DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
      Date date = new Date();
      String timestamp = dateFormat.format(date);
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
      out.write("["+timestamp + "] &lt;ERROR&gt; : " + message + "<br>\r\n");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


