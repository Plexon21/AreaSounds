package com.plexon21.AreaSounds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaSoundsExecutor implements CommandExecutor {
	private final AreaSounds plugin;

	public AreaSoundsExecutor(AreaSounds plugin) {
		this.plugin = plugin;
	}

	// mandatory arguments: name, volume (float), pitch (float), 
	// radius (int), x (float), y (float), z (float), loop (true/false)
	// optional arguments: player1, player2....
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 8)
			sender.sendMessage("Not enough arguments!");
			plugin.playAreaSound(args[0], args[1], args[2], args[3], args[4], args[5], args[6],args[7]);
		if (args.length > 8) {
			String[] players = new String[args.length-8];
			for(int i = 0; i+7<args.length;i++){
				players[i] = args[i+7];
			}
			plugin.playAreaSound(args[0], args[1], args[2], args[3], args[4], args[5], args[6],args[7],players);
		}
		return false;
	}

}
