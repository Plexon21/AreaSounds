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
	@Override
	public void onEnable() {
		AreaSounds();
		saveDefaultConfig();
		config = getConfig();
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
		World adventureWorld = getServer().getWorld(config.getString("WorldName"));
		float vol = Float.parseFloat(volume);
		int rad = Integer.parseInt(radius);
		Location loc = new Location(adventureWorld, Double.parseDouble(x), Double.parseDouble(y),
				Double.parseDouble(z));
		Boolean loopSound = Boolean.parseBoolean(loop);

	}

	public void playAreaSound(String name, float volume, float pitch, int radius, Location location, Boolean loop,
			int duration, String[] players) {
		if (players == null) {

		} else {

		}
	}

	public void AreaSounds() {
		//getServer().getPluginManager().registerEvents(new AreaSoundsListener(getServer()), this);
	}
}
