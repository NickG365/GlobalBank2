package me.samkio.globalbank;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.martin.bukkit.npclib.NPCEntity;
import org.martin.bukkit.npclib.NPCManager;

public class NPCLookers implements Runnable {

	NPCManager m;

	public NPCLookers(NPCManager m) {
		this.m = m;
	}

	@Override
	public void run() {
		for (NPCEntity e : m.getNPCs()) {
			Location l = e.getSpoutPlayer().getLocation();
			for (Player p : GlobalBank.plugin.getServer().getOnlinePlayers()) {
				if(!m.isNPC(p)) {
				if (l.distance(p.getLocation()) < 10) {
					e.lookAtPoint(p.getLocation().clone().add(0, 1, 0));
					break;
				}
				}
			}
		}

	}

}
