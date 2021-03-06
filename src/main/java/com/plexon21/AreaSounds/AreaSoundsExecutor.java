package com.plexon21.AreaSounds;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaSoundsExecutor implements CommandExecutor {
	private final AreaSounds plugin;
	private final Logger log;

	public AreaSoundsExecutor(AreaSounds plugin) {
		this.plugin = plugin;
		this.log = plugin.getLogger();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// mandatory arguments: name, volume (float), pitch (float),
		// radius (int), x (float), y (float), z (float), loop (true/false)
		// optional arguments: player1, player2....
		if (cmd.getName().equalsIgnoreCase("playAreaSound")) {
			if (args.length < 8) {
				sender.sendMessage("Not enough arguments!");
				return false;
			} else if (args.length > 8) {
				String[] players = new String[args.length - 8];
				for (int i = 8; i < args.length; i++) {
					players[i-8] = args[i];
				}
				plugin.playAreaSound(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], players);
				sender.sendMessage("Sound started");
				return true;
			} else {
				plugin.playAreaSound(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
				sender.sendMessage("Sound Started");
				return true;
			}
		}

		// stop an already playing sound (1 argument which is the taskID)
		else if (cmd.getName().equalsIgnoreCase("stopAreaSound")) {
			if (args.length == 0) {
				plugin.stopAllSounds();
				sender.sendMessage("All sounds stopped.");
				return true;
			}/* Disabled until solution for concurrent mod exception is found 
			else {
				int taskID = Integer.parseInt(args[0]);
				plugin.stopSingleSound(taskID);
				log.info("sound with id " + taskID + " stopped");
				return true;
			}*/
		} else if (cmd.getName().equalsIgnoreCase("saveAreaSound")) {
			plugin.saveSoundsToFile();
			sender.sendMessage("Sounds saved to file.");
		}
		return false;
	}

}
