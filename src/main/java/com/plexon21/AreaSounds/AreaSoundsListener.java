package com.plexon21.AreaSounds;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

public class AreaSoundsListener implements Listener {
	Server server;
	
	public AreaSoundsListener(Server server){
		this.server = server;
	}
	
	
	@EventHandler
	public void onBucketUse(PlayerBucketEmptyEvent event) {
		event.setItemStack(new ItemStack(Material.APPLE));
		event.getPlayer().updateInventory();
		server.broadcastMessage(event.getItemStack().toString());
		/*Material liquid = event.getBlockClicked().getType();
		if ( liquid == Material.WATER || liquid == Material.STATIONARY_WATER) {
			
		}*/
	}
}
