package me.voidrunnerv.minigamemanagerprototype;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

public class LobbyBoard {
	
	private static Map<UUID, Integer> TASKS = new HashMap<UUID, Integer>();
	private final UUID uuid;
	
	public LobbyBoard(UUID uuid) {
		this.uuid = uuid;
	}
	
	public void setID(int id) {
		TASKS.put(uuid, id);
	}
	
	public int getID() {
		return TASKS.get(uuid);
	}
	
	public boolean hasID() {
		if(TASKS.containsKey(uuid)) {
			return true;
		}
		
		return false;
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(TASKS.get(uuid));
		TASKS.remove(uuid);
	}
	
}
