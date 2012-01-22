package me.samkio.globalbank.listeners;

import me.samkio.globalbank.GlobalBank;
import me.samkio.globalbank.util.SqliteDB;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

@SuppressWarnings("deprecation")
public class BEntityListener extends EntityListener {
	public GlobalBank b;

	public BEntityListener(GlobalBank b) {
		this.b = b;
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		if (b.m.isNPC(event.getEntity())) {
			if (event instanceof EntityDamageByEntityEvent) {
				if (b.punchers.contains(((EntityDamageByEntityEvent) event)
						.getDamager())) {
					SqliteDB.delBanker(b.m.getNPC(b.m.getNPCIdFromEntity(event
							.getEntity())).BankName);
					b.m.despawnById(b.m.getNPCIdFromEntity(event.getEntity()));
					((Player) ((EntityDamageByEntityEvent) event).getDamager())
							.sendMessage(ChatColor.BLUE + "[GlobalBank]"
									+ ChatColor.WHITE
									+ " Banker has been removed.");
					b.punchers.remove(((EntityDamageByEntityEvent) event)
							.getDamager());
				}
			}
			event.setCancelled(true);
			return;
		}

	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;
		if (b.m.isNPC(event.getTarget())) {
			event.setCancelled(true);
			return;
		}

	}
}
