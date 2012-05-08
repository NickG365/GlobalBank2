package net.ark3l.globalbank2;

import net.ark3l.globalbank2.listeners.BEntityListener;
import net.ark3l.globalbank2.listeners.BInventoryListener;
import net.ark3l.globalbank2.listeners.BPlayerListener;
import net.ark3l.globalbank2.util.Sort;
import net.ark3l.globalbank2.util.SqliteDB;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.martin.bukkit.npclib.NPCEntity;
import org.martin.bukkit.npclib.NPCManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class GlobalBank extends JavaPlugin {
	public static GlobalBank plugin;
	public static Logger log = Logger.getLogger("Minecraft");
	public BPlayerListener p = new BPlayerListener(this);
	public BEntityListener e = new BEntityListener(this);
	public BInventoryListener i;
	public NPCManager m = null;
	public Settings s = new Settings(this);
	public HashMap<Player, ArrayList<ItemStack>> isk = new HashMap<Player, ArrayList<ItemStack>>();
	public HashMap<Player,Bankventory> bankventories = new HashMap<Player,Bankventory>();
	public ArrayList<Player> punchers = new ArrayList<Player>();
	public Sort sort = new Sort();
	public Economy economy = null;

	public void onEnable() {
		plugin = this;
		this.setupConfig();
		this.m = new NPCManager(this);
		this.i = new BInventoryListener(this);
		this.registerListeners();
		this.setupData();
		this.npcSetup();
		if (getServer().getPluginManager().getPlugin("Vault") != null
				&& s.useEconomy) {
			setupEconomy();
		}
		log.info("[GB] GlobalBank v." + this.getDescription().getVersion()
				+ " Enabled");
	}

	private void setupConfig() {
		s.loadSettings();
		s.getSettings();
	}

	private void npcSetup() {
		this.m = new NPCManager(this);
		this.getServer().getScheduler()
				.scheduleSyncRepeatingTask(this, new NPCLookers(m), 5, 5);
		HashMap<Location, String> hm = SqliteDB.getBankers();
		for (Location l : hm.keySet()) {
			NPCEntity t = m.spawnNPC("Banker", l, hm.get(l));
			t.setItemInHand(Material.PAPER);
//			t.getSpoutPlayer().setSkin(
//					"http://dl.dropbox.com/u/19653570/bankersskin.png");
		}

	}

	public void onDisable() {
		log.info("[GB] GlobalBank v." + this.getDescription().getVersion()
				+ " Disabled");
		m.despawnAll();
	}

	private Boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (commandLabel.equalsIgnoreCase("gb") && sender instanceof Player) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("create") && args.length > 1
						&& sender.hasPermission("gb.create")) {
					SqliteDB.newBanker(args[1], ((Player) sender).getLocation());
					NPCEntity t = this.m.spawnNPC("Banker",
							((Player) sender).getLocation(), args[1]);
					t.setItemInHand(Material.PAPER);
//					t.getSpoutPlayer().setSkin(
//							"http://dl.dropbox.com/u/19653570/bankersskin.png");
					sender.sendMessage(ChatColor.BLUE + "[GlobalBank] "
							+ ChatColor.WHITE + "Bank: " + ChatColor.GOLD
							+ args[1] + ChatColor.WHITE + " has been created.");
				} else if (args[0].equalsIgnoreCase("delete")
						&& sender.hasPermission("gb.delete")) {
					sender.sendMessage(ChatColor.BLUE + "[GlobalBank] "
							+ ChatColor.WHITE
							+ "Please punch a Banker to remove them.");
					this.punchers.add((Player) sender);
				}  else {
					sender.sendMessage(ChatColor.BLUE
							+ "[GlobalBank] "
							+ ChatColor.WHITE
							+ " You do not have permission to use this command or it was poorly formatted.");
				}
			} else {
				sender.sendMessage(ChatColor.BLUE + "[GlobalBank]"
						+ ChatColor.WHITE + " v."
						+ this.getDescription().getVersion() + " by "
						+ ChatColor.GOLD + "Samkio" + ChatColor.WHITE + ".");
			}
			return true;
		} else {
			return false;
		}
	}

	public void removeContents(Player p) {
		if (isk.containsKey(p)) {
			isk.remove(p);
		}
	}

	@SuppressWarnings("deprecation")
	private void registerListeners() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(p, this);
		pm.registerEvents(i, this);
		pm.registerEvents(e, this);
	}

	private void setupData() {
		File maindir = new File(this.getDataFolder() + "/Data/");
		maindir.mkdirs();
		SqliteDB.prepare();
	}
}