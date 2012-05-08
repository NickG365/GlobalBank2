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
	public Inventory TopInv, BotInv;
	public int slot;

	public InventoryClose(Player p, Inventory t, Inventory b, int slot) {
		this.p = p;
		this.TopInv = t;
		this.BotInv = b;
		this.slot = slot;
	}

	public void run() {
		CraftPlayer craftPlayer = (CraftPlayer) p;
		EntityPlayer entityPlayer = craftPlayer.getHandle();
		if (entityPlayer == null
				|| entityPlayer.activeContainer == entityPlayer.defaultContainer) {
			PlayerState.getPlayerState(p).setBuyingSlot(0);
			if (PlayerState.getPlayerState(p).getPs() == PlayerStatus.SLOT) {
				p.getInventory().setContents(this.BotInv.getContents().clone());
				SlotDataMethods.saveBank(p, TopInv.getContents().clone(), slot);
				p.sendMessage(ChatColor.BLUE + "[B]" + ChatColor.WHITE
						+ " Thank you " + ChatColor.GOLD + p.getName()
						+ ChatColor.WHITE + ". Have a Great Day!");
				PlayerState.getPlayerState(p).setPs(PlayerStatus.DEFAULT);
			} else if (PlayerState.getPlayerState(p).getPs() == PlayerStatus.CHEST_SELECT) {
				p.sendMessage(ChatColor.BLUE + "[B]" + ChatColor.WHITE
						+ " Thank you " + ChatColor.GOLD + p.getName()
						+ ChatColor.WHITE + ". Have a Great Day!");
				PlayerState.getPlayerState(p).setPs(PlayerStatus.DEFAULT);
				GlobalBank.plugin.removeContents(p);
			}

		}
	}

}
