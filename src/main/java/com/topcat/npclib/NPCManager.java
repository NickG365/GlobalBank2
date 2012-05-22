package com.topcat.npclib;

import com.topcat.npclib.entity.Banker;
import com.topcat.npclib.nms.BServer;
import com.topcat.npclib.nms.BWorld;
import com.topcat.npclib.nms.NPCEntity;
import com.topcat.npclib.nms.NPCNetworkManager;
import net.minecraft.server.Entity;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author martin
 */
public class NPCManager {

	private HashMap<String, Banker> bankers = new HashMap<String, Banker>();
	private BServer server;
	private int taskid;
	private Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
	private NPCNetworkManager npcNetworkManager;
	public static JavaPlugin plugin;

	public NPCManager(JavaPlugin plugin) {
		server = BServer.getInstance();

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

	public BWorld getBWorld(World world) {
		BWorld bworld = bworlds.get(world);
		if (bworld != null) {
			return bworld;
		}
		bworld = new BWorld(world);
		bworlds.put(world, bworld);
		return bworld;
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
					BWorld world = getBWorld(event.getWorld());
					world.getWorldServer().addEntity(banker.getEntity());
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
			server.getLogger().log(Level.WARNING, "NPC with that id already exists, existing NPC returned");
			return bankers.get(id);
		} else {
			if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
				String tmp = name.substring(0, 16);
				server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
				server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
				name = tmp;
			}
			BWorld world = getBWorld(l.getWorld());
			NPCEntity npcEntity = new NPCEntity(this, world, name, new ItemInWorldManager(world.getWorldServer()));
			npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
			world.getWorldServer().addEntity(npcEntity); //the right way
			Banker npc = new Banker(npcEntity, bankName);

			if(Bukkit.getPluginManager().isPluginEnabled("Spout")) {
				((SpoutPlayer)npc.getBukkitEntity()).setSkin("http://dl.dropbox.com/u/18216599/images/bankersskin.png");
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

	public void despawnHumanByName(String npcName) {
		if (npcName.length() > 16) {
			npcName = npcName.substring(0, 16); //Ensure you can still despawn
		}
		HashSet<String> toRemove = new HashSet<String>();
		for (String n : bankers.keySet()) {
			Banker npc = bankers.get(n);

			if (npc != null && npc.getName().equals(npcName)) {
				toRemove.add(n);
				npc.removeFromWorld();
			}
		}
		for (String n : toRemove) {
			bankers.remove(n);
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

	public List<Banker> getHumanNPCByName(String name) {
		List<Banker> ret = new ArrayList<Banker>();
		Collection<Banker> i = bankers.values();
		for (Banker e : i) {
				if (e.getName().equalsIgnoreCase(name)) {
					ret.add(e);
				}
		}
		return ret;
	}

	public List<Banker> getBankers() {
		return new ArrayList<Banker>(bankers.values());
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

	public void rename(String id, String name) {
		if (name.length() > 16) { // Check and nag if name is too long, spawn NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		Banker npc = getBanker(id);
		npc.setName(name);
		BWorld b = getBWorld(npc.getBukkitEntity().getLocation().getWorld());
		WorldServer s = b.getWorldServer();
		try {
			Method m = s.getClass().getDeclaredMethod("d", new Class[] {Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
			m = s.getClass().getDeclaredMethod("c", new Class[] {Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}

	public BServer getServer() {
		return server;
	}

	public NPCNetworkManager getNPCNetworkManager() {
		return npcNetworkManager;
	}

}