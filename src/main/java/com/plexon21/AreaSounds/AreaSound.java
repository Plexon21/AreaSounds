package com.plexon21.AreaSounds;

import java.io.Serializable;

public class AreaSound {
	private static final long serialVersionUID = 7012304631946917404L;
	public String name;
	public long ID;
	public float volume, pitch;
	public double x, y, z;
	public boolean loop;
	public String[] players;

	public AreaSound(String name, float volume, float pitch, double x, double y, double z, boolean loop,
			String[] players) {
		this.name = name;
		this.volume = volume;
		this.pitch = pitch;
		this.x = x;
		this.y = y;
		this.z = z;
		this.loop = loop;
		this.players = players;
	}

	// Copy-Constructor
	public AreaSound(AreaSound sound) {
		this.name = sound.name;
		this.volume = sound.volume;
		this.pitch = sound.pitch;
		this.x = sound.x;
		this.y = sound.y;
		this.z = sound.z;
		this.loop = sound.loop;
		if(sound.players!=null){
		this.players = new String[sound.players.length];
		for (int i = 0; i < sound.players.length; i++) {
			this.players[i] = new String(sound.players[i]);
		}}
		else this.players = null;
	}
}
