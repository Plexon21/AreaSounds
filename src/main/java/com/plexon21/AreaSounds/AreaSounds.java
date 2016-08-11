package com.plexon21.AreaSounds;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AreaSounds extends JavaPlugin {
	FileConfiguration config;
	World adventureWorld;

	@Override
	public void onEnable() {
		AreaSounds();
		saveDefaultConfig();
		config = getConfig();
		adventureWorld = getServer().getWorld(config.getString("WorldName"));
		this.getCommand("areasound").setExecutor(new AreaSoundsExecutor(this));
	}

	@Override
	public void onDisable() {

	}

	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop) {
		playAreaSound(name, volume, pitch, radius, x, y, z, null);
	}

	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop, String[] players) {
		// TODO: World in config
		float vol = Float.parseFloat(volume);
		float pit = Float.parseFloat(pitch);
		int rad = Integer.parseInt(radius);
		Location loc = new Location(adventureWorld, Double.parseDouble(x), Double.parseDouble(y),
				Double.parseDouble(z));
		Boolean loopSound = Boolean.parseBoolean(loop);

		if (!loopSound) {
			playAreaSoundOnce(name, vol, pit, rad, loc, players);
		}

		else {

		}

	}

	public void loopAreaSound(String name, float volume, float pitch, int radius, Location location, int duration,
			String[] players) {

		// duration in 1/10 seconds -> times 2 equals number of ticks
		int length = 2 * (Integer.parseInt(name.substring(name.indexOf('_') + 1)));

		// TODO: Loop logic

	}

	public void playAreaSoundOnce(String name, float volume, float pitch, int radius, Location location,
			String[] players) {
		if (players == null) {
			adventureWorld.playSound(location, name, volume, pitch);
		} else {
			for (String p : players) {
				//only if player is online
				if (p != null)
					getServer().getPlayer(p).playSound(location, name, volume, pitch);
			}
		}
	}

	public void AreaSounds() {
		// getServer().getPluginManager().registerEvents(new
		// AreaSoundsListener(getServer()), this);
	}
}
