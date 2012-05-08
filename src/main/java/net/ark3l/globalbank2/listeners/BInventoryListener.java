package net.ark3l.globalbank2.listeners;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.PlayerState.PlayerStatus;
import net.ark3l.globalbank2.methods.SimpleMethods;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;

public class BInventoryListener extends InventoryListener {
	private GlobalBank b;

	public BInventoryListener(GlobalBank b) {
		this.b = b;
	}

	public void onInventoryClose(InventoryCloseEvent e) {
		int i = PlayerState.getPlayerState(e.getPlayer()).getSlot();
		b.getServer()
				.getScheduler()
				.scheduleSyncDelayedTask(
						b,
						new InventoryClose(e.getPlayer(), e.getInventory(), e
								.getBottomInventory(), i), 2);
	}

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
