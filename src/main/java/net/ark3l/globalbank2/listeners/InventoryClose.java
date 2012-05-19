package net.ark3l.globalbank2.listeners;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.PlayerState.PlayerStatus;
import net.ark3l.globalbank2.methods.SlotDataMethods;
import net.minecraft.server.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InventoryClose implements Runnable {
	public Player p;
	public Inventory Inv;
	public int slot;

	public InventoryClose(Player p, Inventory t, int slot) {
		this.p = p;
		this.Inv = t;
		this.slot = slot;
	}


	public void run() {
		CraftPlayer craftPlayer = (CraftPlayer) p;
		EntityPlayer entityPlayer = craftPlayer.getHandle();
		if (entityPlayer == null
				|| entityPlayer.activeContainer == entityPlayer.defaultContainer) {
			PlayerState.getPlayerState(p).setBuyingSlot(0);
			if (PlayerState.getPlayerState(p).getPs() == PlayerStatus.SLOT) {
				SlotDataMethods.saveBank(p, Inv.getContents().clone(), slot);
				p.sendMessage(ChatColor.BLUE + "[B] " + ChatColor.WHITE
						+ GlobalBank.plugin.settings.getStringValue("Strings.Closed"));
				PlayerState.getPlayerState(p).setPs(PlayerStatus.DEFAULT);
			} else if (PlayerState.getPlayerState(p).getPs() == PlayerStatus.CHEST_SELECT) {
				p.sendMessage(ChatColor.BLUE + "[B] " + ChatColor.WHITE
						+ GlobalBank.plugin.settings.getStringValue("Strings.Closed"));
				PlayerState.getPlayerState(p).setPs(PlayerStatus.DEFAULT);
				GlobalBank.plugin.removeContents(p);
			}

		}
	}

}
