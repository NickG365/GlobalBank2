package net.ark3l.globalbank2.listeners;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.PlayerState.PlayerStatus;
import net.ark3l.globalbank2.methods.SimpleMethods;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BInventoryListener implements Listener {
	private GlobalBank b;

	public BInventoryListener(GlobalBank b) {
		this.b = b;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent e) {
		int i = PlayerState.getPlayerState(e.getPlayer()).getSlot();
		b.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(
						b,
						new InventoryClose(e.getPlayer(), e.getInventory(), e.getBottomInventory(), i), 2);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent e) {
		PlayerStatus ps = PlayerState.getPlayerState(e.getPlayer()).getPs();
		Player p = e.getPlayer();
		if (e.getItem() == null)
			return;
		if (!b.isk.containsKey(p))
			return;
		if (b.isk.get(p).contains(e.getItem())) {
			if (ps.equals(PlayerStatus.CHEST_SELECT)) {
				if (e.getItem().getType() == Material.CHEST) {
					e.setCancelled(SimpleMethods.handleBank(b, p, e.getSlot()));
				}
			} else if (ps.equals(PlayerStatus.SLOT)) {
				e.setCancelled(SimpleMethods.handleSlot(e.getItem(), p,
						e.getInventory(), b));
			}

		} else {
			if (e.isShiftClick()) {
				if (ps.equals(PlayerStatus.CHEST_SELECT)) {
					e.setCancelled(true);
				} else if ((e.getItem().getType() == Material.CHEST || e
						.getItem().getType() == Material.PAPER)
						&& ps.equals(PlayerStatus.SLOT)) {
					e.setCancelled(true);
				}
			}
		}

	}

}
