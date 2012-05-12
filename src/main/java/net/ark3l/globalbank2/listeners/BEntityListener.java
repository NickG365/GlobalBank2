package net.ark3l.globalbank2.listeners;

import com.topcat.npclib.entity.BankerNPC;
import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.util.SqliteDB;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

@SuppressWarnings("deprecation")
public class BEntityListener implements Listener {
	public GlobalBank b;

	public BEntityListener(GlobalBank b) {
		this.b = b;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}
		if (b.m.isNPC(event.getEntity())) {
			if (event instanceof EntityDamageByEntityEvent) {
				if (b.punchers.contains(((EntityDamageByEntityEvent) event)
						.getDamager())) {
					BankerNPC banker = (BankerNPC) b.m.getNPC(b.m.getNPCIdFromEntity(event.getEntity()));
					SqliteDB.delBanker(banker.bankName);
					b.m.despawnById(b.m.getNPCIdFromEntity(banker.getBukkitEntity()));
					((Player) ((EntityDamageByEntityEvent) event).getDamager())
							.sendMessage(ChatColor.BLUE + "[GlobalBank]"
									+ ChatColor.WHITE
									+ " Banker has been removed.");
					b.punchers.remove(((EntityDamageByEntityEvent) event)
							.getDamager());
				}
			}
			event.setCancelled(true);
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;
		if (b.m.isNPC(event.getTarget())) {
			event.setCancelled(true);
		}

	}
}
