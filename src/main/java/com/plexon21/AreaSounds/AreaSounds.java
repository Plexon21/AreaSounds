package com.plexon21.AreaSounds;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	ArrayList<AreaSound> activeSounds;
	BukkitScheduler scheduler;
	String filePath;
	File soundsFile;

	@Override
	public void onEnable() {
		log = this.getLogger();
		saveDefaultConfig();
		config = getConfig();
		adventureWorld = getServer().getWorld(config.getString("WorldName"));
		this.getCommand("playAreaSound").setExecutor(new AreaSoundsExecutor(this));
		this.getCommand("stopAreaSound").setExecutor(new AreaSoundsExecutor(this));
		tasks = new ArrayList<Integer>();
		scheduler = getServer().getScheduler();
		filePath = config.getString("LoopFile");

		try {
			FileInputStream fis = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			while (true) {
				AreaSound sound = (AreaSound) ois.readObject();
				loopAreaSound(sound);
			}

		} catch (FileNotFoundException e) {
			log.info("AreaSounds file not found");
		} catch (IOException e) {
			log.info("Reading of AreaSounds file failed");
		} catch (ClassNotFoundException e) {
			
		}
	}

	@Override
	public void onDisable() {

	}

	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop) {
		playAreaSound(name, volume, pitch, radius, x, y, z,loop, null);
	}

	// volume can be between 0.0 and 1.0
	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop, String[] players) {
		try {
			AreaSound sound;
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

			sound = new AreaSound(name, vol, pit, xParsed, yParsed, zParsed, loopSound, players);
			if (!loopSound) {
				playAreaSoundOnce(sound);
			}

			else {
				loopAreaSound(sound);

				// write AreaSound into file
				FileOutputStream fos = new FileOutputStream(filePath);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(sound);
				oos.close();
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}

	}

	public void loopAreaSound(final AreaSound sound) {

		// duration in 1/10 seconds -> times 2 equals number of ticks
		long length = 2 * (Integer.parseInt(sound.name.substring(sound.name.indexOf('_') + 1)));

		int taskID = scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				playAreaSoundOnce(new AreaSound(sound));
			}
		}, 0L, length);
		log.info("Sound " + taskID + " started.");
		tasks.add(taskID);
		activeSounds.add(sound);
	}

	public void playAreaSoundOnce(AreaSound sound) {
		Location location = new Location(adventureWorld, sound.x, sound.y, sound.z);
		if (sound.players == null) {
			adventureWorld.playSound(location, sound.name, sound.volume, sound.pitch);
		} else {
			for (String p : sound.players) {
				// only if player is online
				if (p != null)
					getServer().getPlayer(p).playSound(location, sound.name, sound.volume, sound.pitch);
			}
		}
	}

	public void stopSingleSound(int taskID) {
		scheduler.cancelTask(taskID);
	}

	public void stopAllSounds() {
		scheduler.cancelAllTasks();
		soundsFile = new File(filePath);
		soundsFile.delete();
	}
}
