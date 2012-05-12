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
	public BPlayerListener playerListener = new BPlayerListener(this);
	public BEntityListener entityListener = new BEntityListener(this);
	public BInventoryListener inventoryListener;
	public NPCManager manager = null;
	public Settings settings = new Settings(this);
	public HashMap<Player, ArrayList<ItemStack>> isk = new HashMap<Player, ArrayList<ItemStack>>();
	public HashMap<Player, Bankventory> bankventories = new HashMap<Player, Bankventory>();
	public ArrayList<Player> punchers = new ArrayList<Player>();
	public Sort sort = new Sort();
	public Economy economy = null;

	public void onEnable() {
		plugin = this;
		this.setupConfig();
		this.manager = new NPCManager(this);
		this.inventoryListener = new BInventoryListener(this);
		this.registerListeners();
		this.setupData();
		this.npcSetup();
		if (getServer().getPluginManager().getPlugin("Vault") != null
				&& settings.useEconomy) {
			setupEconomy();
		}

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e1) {
			Log.warning("Error submitting usage statistics");
		}

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
			manager.spawnBankerNPC(entry.getValue(), entry.getKey(), entry.getValue());
		}
	}

	public void onDisable() {
		Log.info(this + " disabled!");
		manager.despawnAll();
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
					manager.spawnBankerNPC(args[1], ((Player) sender).getLocation(), args[1]);
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