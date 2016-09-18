package com.plexon21.AreaSounds;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AreaSounds extends JavaPlugin {
	FileConfiguration config;
	World adventureWorld;
	Logger log;
	ArrayList<Integer> tasks;
	BukkitScheduler scheduler;

	@Override
	public void onEnable() {
		log = this.getLogger();
		saveDefaultConfig();
		config = getConfig();
		adventureWorld = getServer().getWorld(config.getString("WorldName"));
		this.getCommand("areasound").setExecutor(new AreaSoundsExecutor(this));
		tasks = new ArrayList<Integer>();
		scheduler = getServer().getScheduler();
		
		// TODO: read looped sounds from config and start them
	}

	@Override
	public void onDisable() {
		
	}

	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop) {
		playAreaSound(name, volume, pitch, radius, x, y, z, null);
	}

	// volume can be between 0.0 and 1.0
	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop, String[] players) {
		try {
			float vol = Float.parseFloat(volume);
			float pit = Float.parseFloat(pitch);
			int rad = Integer.parseInt(radius);
			double xParsed = Double.parseDouble(x);
			double yParsed = Double.parseDouble(y);
			double zParsed = Double.parseDouble(z);
			Boolean loopSound = Boolean.parseBoolean(loop);

			// calculate volume, because playSound has no radius parameter
			// volume of 1 can be heard 16 blocks, volume of 10 can be heard 160
			// blocks.
			// cut of higher or lower volumes
			vol = (vol > 1.0f) ? 1.0f : vol;
			vol = (vol < 0.0f) ? 0.0f : vol;
			float radF = rad / 16;
			vol = vol * radF;

			if (!loopSound) {
				Location loc = new Location(adventureWorld, xParsed, yParsed, zParsed);
				playAreaSoundOnce(name, vol, pit, loc, players);
			}

			else {
				loopAreaSound(name, vol, pit, xParsed, yParsed, zParsed, players);
				// TODO: write to config
			}
		} catch (Exception e) {
			log.info("AreaSound could not be played, check your command syntax.");
		}

	}

	public void loopAreaSound(final String name, final float volume, final float pitch, double x, double y, double z, final String[] players) {

		// duration in 1/10 seconds -> times 2 equals number of ticks
		long length = 2 * (Integer.parseInt(name.substring(name.indexOf('_') + 1)));

		final Location loc = new Location(adventureWorld, x, y, z);	
		
		int taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable(){
			public void run() {
				playAreaSoundOnce(name,volume,pitch,loc,players);		
			}				
		}, 0L, length);
		log.info("Sound "+taskID+" started.");
		tasks.add(taskID);
	}

	public void playAreaSoundOnce(String name, float volume, float pitch, Location location, String[] players) {
		if (players == null) {
			adventureWorld.playSound(location, name, volume, pitch);
		} else {
			for (String p : players) {
				// only if player is online
				if (p != null)
					getServer().getPlayer(p).playSound(location, name, volume, pitch);
			}
		}
	}

	public void stopSingleSound(int taskID) {
		scheduler.cancelTask(taskID);
		//TODO: remove from config
		
	}

	public void stopAllSounds() {
		scheduler.cancelAllTasks();		
		//TODO: remove from config
	}
}
