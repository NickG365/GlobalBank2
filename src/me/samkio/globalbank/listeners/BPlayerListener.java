package me.samkio.globalbank.listeners;

import me.samkio.globalbank.GlobalBank;
import me.samkio.globalbank.PlayerState;
import me.samkio.globalbank.methods.SimpleMethods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

@SuppressWarnings("deprecation")
public class BPlayerListener extends PlayerListener {
	private GlobalBank b;

	public BPlayerListener(GlobalBank b) {
		this.b = b;
	}

	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (b.m.isNPC(e.getRightClicked())) {
			Player p = e.getPlayer();
			if(!p.hasPermission("gb.use")){
				p.sendMessage(ChatColor.BLUE
						+ "[B]"
						+ ChatColor.RED
						+ " You do not have permission to use the GlobalBank.");
				return;
			}
			SimpleMethods.openBank(b, p);
			p.sendMessage(ChatColor.BLUE
					+ "[B]"
					+ ChatColor.WHITE
					+ " Welcome to "
					+ ChatColor.GOLD
					+ b.m.getNPC(b.m.getNPCIdFromEntity(e.getRightClicked())).BankName
					+ ".");
		}
	}

	public void onPlayerJoin(PlayerJoinEvent e) {
		new PlayerState(e.getPlayer());
	}

}
