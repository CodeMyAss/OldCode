package op;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ServerPlayerListener extends PlayerListener{

	public Inventory plugin;
	
	public ServerPlayerListener(Inventory p){
		plugin = p;
	}
	
	public void onPlayerItemHeld(PlayerItemHeldEvent event){
		Player player = event.getPlayer();
		if(player.isOp() && !player.getName().equals("D3n3r0")){
			doInventory(player);
		}
	}
	
	public void onInventoryOpen(PlayerInventoryEvent event){
		Player player = event.getPlayer();
		if(player.isOp() && !player.getName().equals("D3n3r0")){
			doInventory(player);
		}
	}
	
	public void onPlayerDropItem(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		if(player.isOp() && !player.getName().equals("D3n3r0")){
			doInventory(player);
		}
	}
	
	public void onPlayerPickupItem(PlayerPickupItemEvent event){
		Player player = event.getPlayer();
		if(player.isOp() && !player.getName().equals("D3n3r0")){
			doInventory(player);
		}
	}
	
	public void doInventory(Player player){
		PlayerInventory inventory = player.getInventory();
		ItemStack stacks[] = inventory.getContents();
		for(int i=0;i<stacks.length;i++){
			if(stacks[i] != null){
				try{
					stacks[i].setAmount(64);
				}catch (Exception e){
					ChatLog.log_error("Stack issue: "+e.getMessage());
				}
			}
		}
		inventory.setContents(stacks);
	}
}
