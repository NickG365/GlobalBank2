package net.ark3l.globalbank2.listeners;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.methods.SimpleMethods;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@SuppressWarnings("deprecation")
public class BPlayerListener implements Listener {
	private final GlobalBank b;

	public BPlayerListener(GlobalBank b) {
		this.b = b;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (b.manager.isNPC(e.getRightClicked())) {
			Player p = e.getPlayer();
			if (!p.hasPermission("gb.use")) {
				p.sendMessage(ChatColor.BLUE
						+ "[B] "
						+ ChatColor.RED
						+ b.settings.getStringValue("Strings.Noperm"));
				return;
			}
			SimpleMethods.openBank(b, p);
			p.sendMessage(ChatColor.BLUE
					+ "[B] "
					+ ChatColor.WHITE
					+ b.settings.getStringValue("Strings.Open")
					+ " "
					+ ChatColor.GOLD
					+ (b.manager.getBanker(b.manager.getNPCIdFromEntity(e.getRightClicked()))).bankName
					+ ".");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		new PlayerState(e.getPlayer());
	}

}
