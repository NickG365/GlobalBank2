package net.ark3l.globalbank2;

import com.topcat.npclib.NPCManager;
import net.ark3l.globalbank2.listeners.BEntityListener;
import net.ark3l.globalbank2.listeners.BInventoryListener;
import net.ark3l.globalbank2.listeners.BPlayerListener;
import net.ark3l.globalbank2.util.Log;
import net.ark3l.globalbank2.util.Metrics;
import net.ark3l.globalbank2.util.Sort;
import net.ark3l.globalbank2.util.SqliteDB;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GlobalBank extends JavaPlugin {
	public static GlobalBank plugin;

	public final BPlayerListener playerListener = new BPlayerListener(this);
	public final BEntityListener entityListener = new BEntityListener(this);
	public final BInventoryListener inventoryListener = new BInventoryListener(this);
	public final Settings settings = new Settings(this);

	public final HashMap<Player, ArrayList<ItemStack>> isk = new HashMap<Player, ArrayList<ItemStack>>();
	public final HashMap<Player, Bankventory> bankventories = new HashMap<Player, Bankventory>();
	public final ArrayList<Player> punchers = new ArrayList<Player>();
	public final Sort sort = new Sort();

	public Economy economy = null;
	public NPCManager manager = null;

	public void onEnable() {
		plugin = this;
		manager = new NPCManager(this);

		setupConfig();
		Log.info("Loaded settings");
		setupData();
		Log.info("Loaded SQLite database");
		npcSetup();
		Log.info("Loaded NPCs");

		if (getServer().getPluginManager().getPlugin("Vault") != null && settings.useEconomy) {
			setupEconomy();
			Log.info("Economy enabled");
		}

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e1) {
			Log.warning("Error submitting usage statistics");
		}

		registerListeners();

		Log.info(this + " enabled!");
	}

	private void setupConfig() {
		settings.loadSettings();
		settings.getSettings();
	}

	private void npcSetup() {
		this.manager = new NPCManager(this);
		HashMap<Location, String> hm = SqliteDB.getBankers();
		for (Map.Entry<Location, String> entry: hm.entrySet()) {
			manager.spawnBanker(entry.getKey(), entry.getValue());
		}
	}

	public void onDisable() {
		manager.despawnAll();
		Log.info(this + " disabled!");
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
				if (args[0].equalsIgnoreCase("create") && args.length > 1 && sender.hasPermission("gb.create")) {
					if(args[1].length() > 16) {
						sender.sendMessage(ChatColor.BLUE + "[GlobalBank2] "
								+ ChatColor.WHITE + "Bank names must be no longer than 16 letters");
						return true;
					}
					SqliteDB.newBanker(args[1], ((Player) sender).getLocation());
					manager.spawnBanker(((Player) sender).getLocation(), args[1]);
					sender.sendMessage(ChatColor.BLUE + "[GlobalBank2] "
							+ ChatColor.WHITE + "Bank: " + ChatColor.GOLD
							+ args[1] + ChatColor.WHITE + " has been created.");
				} else if (args[0].equalsIgnoreCase("delete") && sender.hasPermission("gb.delete")) {
					sender.sendMessage(ChatColor.BLUE + "[GlobalBank2] "
							+ ChatColor.WHITE
							+ "Please punch a Banker to remove them.");
					this.punchers.add((Player) sender);
				} else {
					sender.sendMessage(ChatColor.BLUE
							+ "[GlobalBank2] "
							+ ChatColor.WHITE
							+ " You do not have permission to use this command or it was poorly formatted.");
				}
			} else {
				sender.sendMessage(ChatColor.BLUE + this.toString());
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

	private void registerListeners() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(inventoryListener, this);
		pm.registerEvents(entityListener, this);
	}

	private void setupData() {
		File maindir = new File(this.getDataFolder() + "/Data/");
		maindir.mkdirs();
		SqliteDB.prepare();
	}
}