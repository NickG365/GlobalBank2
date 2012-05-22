package net.ark3l.globalbank2.banker;

import net.ark3l.globalbank2.banker.entity.Banker;
import net.ark3l.globalbank2.banker.nms.NPCEntity;
import net.ark3l.globalbank2.banker.nms.NPCNetworkManager;
import net.ark3l.globalbank2.util.Log;
import net.minecraft.server.Entity;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author martin
 */
public class NPCManager {

	private HashMap<String, Banker> bankers = new HashMap<String, Banker>();
	private int taskid;
	private NPCNetworkManager npcNetworkManager;
	public static JavaPlugin plugin;
	private MinecraftServer mcServer;

	public NPCManager(JavaPlugin plugin) {
		mcServer = ((CraftServer)plugin.getServer()).getServer();

		npcNetworkManager = new NPCNetworkManager();
		NPCManager.plugin = plugin;
		taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				HashSet<String> toRemove = new HashSet<String>();
				for (String i : bankers.keySet()) {
					Entity j = bankers.get(i).getEntity();
					j.aA();
					if (j.dead) {
						toRemove.add(i);
					}
				}
				for (String n : toRemove) {
					bankers.remove(n);
				}
			}
		}, 1L, 1L);
		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
	}

	private WorldServer getWorldServer(World world) {
		return ((CraftWorld)world).getHandle();
	}

	public MinecraftServer getMcServer() {
		return mcServer;
	}

	private class SL implements Listener {
		@SuppressWarnings("unused")
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event) {
			if (event.getPlugin() == plugin) {
				despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(taskid);
			}
		}
	}

	private class WL implements Listener {
		@SuppressWarnings("unused")
		@EventHandler
		public void onChunkLoad(ChunkLoadEvent event) {
			for (Banker banker : bankers.values()) {
				if (banker != null && event.getChunk() == banker.getBukkitEntity().getLocation().getBlock().getChunk()) {
					getWorldServer(event.getWorld()).addEntity(banker.getEntity());
				}
			}
		}
	}

	public Banker spawnBanker(Location l, String bankName) {
		String name = "Banker ";
		int i = 0;
		String id = name;
		while (bankers.containsKey(id)) {
			id = name + i;
			i++;
		}
		return spawnBanker(id, l, id, bankName);
	}

	public Banker spawnBanker(String name, Location l, String id, String bankName) {
		if (bankers.containsKey(id)) {
			Log.warning("NPC with that id already exists, existing NPC returned");
			return bankers.get(id);
		} else {
			if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
				String tmp = name.substring(0, 16);
				Log.warning("NPCs can't have names longer than 16 characters,");
				Log.warning(name + " has been shortened to " + tmp);
				name = tmp;
			}
			WorldServer ws = getWorldServer(l.getWorld());
			NPCEntity npcEntity = new NPCEntity(this, ws, name, new ItemInWorldManager(ws));
			npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
			ws.addEntity(npcEntity); //the right way
			Banker npc = new Banker(npcEntity, bankName);

			if(Bukkit.getPluginManager().isPluginEnabled("Spout")) {
				SpoutPlayer sp = npc.getSpoutPlayer();
				sp.setSkin("http://dl.dropbox.com/u/18216599/images/bankersskin.png");
				sp.setTitle(ChatColor.GOLD + "Banker\n" + ChatColor.WHITE + "[" + bankName + "]");
			}

			bankers.put(id, npc);
			return npc;
		}
	}

	public void despawnById(String id) {
		Banker npc = bankers.get(id);
		if (npc != null) {
			bankers.remove(id);
			npc.removeFromWorld();
		}
	}


	public void despawnAll() {
		for (Banker npc : bankers.values()) {
			if (npc != null) {
				npc.removeFromWorld();
			}
		}
		bankers.clear();
	}

	public Banker getBanker(String id) {
		return bankers.get(id);
	}

	public boolean isNPC(org.bukkit.entity.Entity e) {
		return ((CraftEntity) e).getHandle() instanceof NPCEntity;
	}

	public String getNPCIdFromEntity(org.bukkit.entity.Entity e) {
		if (e instanceof HumanEntity) {
			for (String i : bankers.keySet()) {
				if (bankers.get(i).getBukkitEntity().getEntityId() == e.getEntityId()) {
					return i;
				}
			}
		}
		return null;
	}

	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}

}