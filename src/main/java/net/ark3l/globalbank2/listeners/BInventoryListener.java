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
		if (!(e.getPlayer() instanceof Player)) return;
		int i = PlayerState.getPlayerState((Player) e.getPlayer()).getSlot();
		b.getServer().getScheduler().scheduleSyncDelayedTask(b, new InventoryClose((Player) e.getPlayer(), e.getInventory(), i), 2);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) return;
		Player p = ((Player) e.getWhoClicked());
		PlayerStatus ps = PlayerState.getPlayerState(p).getPs();
		if (e.getCurrentItem() == null)
			return;
		if (!b.isk.containsKey(p))
			return;
		if (b.isk.get(p).contains(e.getCurrentItem())) {
			if (ps.equals(PlayerStatus.CHEST_SELECT)) {
				if (e.getCurrentItem().getType() == Material.CHEST) {
					// player has selected a slot in the slot selection screen
					e.setCancelled(SimpleMethods.handleBank(b, p, e.getSlot()));
				} else {
					e.setCancelled(true);
				}
			} else if (ps.equals(PlayerStatus.SLOT)) {
				// player has done something inside the slot
				e.setCancelled(SimpleMethods.handleSlot(e.getCurrentItem(), p,
						e.getInventory(), b));
			}

		} else {
			if (e.isShiftClick()) {
				if (ps.equals(PlayerStatus.CHEST_SELECT)) {
					e.setCancelled(true);
				} else if ((e.getCurrentItem().getType() == Material.CHEST || e
						.getCurrentItem().getType() == Material.PAPER)
						&& ps.equals(PlayerStatus.SLOT)) {
					e.setCancelled(true);
				}
			}
		}

	}

}
