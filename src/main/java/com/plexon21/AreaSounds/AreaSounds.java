package com.plexon21.AreaSounds;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.gson.Gson;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.*;

public class AreaSounds extends JavaPlugin implements Listener {
	FileConfiguration config;
	World adventureWorld;
	Logger log;
	List<Integer> tasks = new ArrayList<Integer>();
	List<AreaSound> activeSounds = new ArrayList<AreaSound>();
	// List<Integer> tasks = new CopyOnWriteArrayList<Integer>();
	// List<AreaSound> activeSounds = new CopyOnWriteArrayList<AreaSound>();
	BukkitScheduler scheduler;
	File soundsFile;

	@Override
	public void onEnable() {
		log = this.getLogger();
		saveDefaultConfig();
		config = getConfig();
		adventureWorld = getServer().getWorld(config.getString("WorldName"));
		this.getCommand("playAreaSound").setExecutor(new AreaSoundsExecutor(this));
		this.getCommand("stopAreaSound").setExecutor(new AreaSoundsExecutor(this));
		this.getCommand("saveAreaSound").setExecutor(new AreaSoundsExecutor(this));
		getServer().getPluginManager().registerEvents(this, this);
		tasks = new ArrayList<Integer>();
		scheduler = getServer().getScheduler();
		soundsFile = new File(getDataFolder(), config.getString("LoopFile"));
		try {
			readSoundsFromFile();
		} catch (IOException e) {
			log.info("Could not read AreaSounds-File");
		}
	}

	@Override
	public void onDisable() {
		saveSoundsToFile();
	}

	public void playAreaSound(String name, String volume, String pitch, String radius, String x, String y, String z,
			String loop) {
		playAreaSound(name, volume, pitch, radius, x, y, z, loop, null);
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
			float radF = (float) rad / 16f;
			vol = vol * radF;

			sound = new AreaSound(name, vol, pit, xParsed, yParsed, zParsed, players);
			if (!loopSound) {
				playAreaSoundOnce(sound);
			}

			else {
				loopAreaSound(sound);
			}
		} catch (Exception e) {
			log.info(e.getMessage());
			log.info("AreaSound could not be played, check your command syntax.");
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
		sound.ID = taskID;
		activeSounds.add(sound);
	}

	public void playAreaSoundOnce(AreaSound sound) {
		Location location = new Location(adventureWorld, sound.x, sound.y, sound.z);
		if (sound.players == null) {
			adventureWorld.playSound(location, sound.name, sound.volume, sound.pitch);
		} else {
			for (String p : sound.players) {
				// only if player is online
				Player pl = getServer().getPlayer(p);
				if (pl != null)
					pl.playSound(location, sound.name, sound.volume, sound.pitch);
			}
		}
	}
	/*
	 * disabled until solution for concurrentmodificationException is found
	 * public void stopSingleSound(int taskID) { scheduler.cancelTask(taskID);
	 * int soundID = 0; for (AreaSound sound : activeSounds) { if (sound.ID ==
	 * taskID) activeSounds.remove(soundID); soundID++; } int taskListID = 0;
	 * for (int task : tasks) { if (task == taskID) tasks.remove(taskListID);
	 * taskListID++; } }
	 */

	public void stopAllSounds() {
		scheduler.cancelAllTasks();
		soundsFile = new File(getDataFolder(), config.getString("LoopFile"));
		soundsFile.delete();
		activeSounds = new ArrayList<AreaSound>();
		tasks = new ArrayList<Integer>();
	}

	@EventHandler
	public void onWorldSave(WorldSaveEvent event) {
		saveSoundsToFile();
	}


	public void saveSoundsToFile() {

		Gson gson = new Gson();
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(soundsFile);
			for (AreaSound sound : activeSounds) {
				writer.println(gson.toJson(sound));
			}
			log.info("all sounds written into file successfully");
		} catch (FileNotFoundException e) {
			log.info("Sound-file not found");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void readSoundsFromFile() throws IOException {
		Gson gson = new Gson();
		log.info("Path to soundfile " + soundsFile.getAbsolutePath());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(soundsFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				loopAreaSound(gson.fromJson(line, AreaSound.class));
			}
		} catch (FileNotFoundException e) {
			log.info("Sound-file not found");
		} catch (IOException e) {
			log.info(e.getMessage());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
