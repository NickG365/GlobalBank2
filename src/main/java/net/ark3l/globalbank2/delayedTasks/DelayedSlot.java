package net.ark3l.globalbank2.delayedTasks;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.PlayerState.PlayerStatus;
import net.ark3l.globalbank2.methods.SlotDataMethods;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DelayedSlot implements Runnable {
	private final int i;
	private final Player p;
	private final GlobalBank b;

	public DelayedSlot(Player p, int i, GlobalBank b) {
		this.p = p;
		this.i = i;
		this.b = b;
	}

	public void run() {
		b.removeContents(p);
		ItemStack[] content = new ItemStack[54];
		ArrayList<ItemStack> iss = new ArrayList<ItemStack>();
		ItemStack ChestBack = new ItemStack(Material.CHEST);
		ItemStack PaperSort = new ItemStack(Material.PAPER);
		iss.add(ChestBack);
		iss.add(PaperSort);
		b.isk.put(p, iss);
		content[0] = ChestBack;
		content[1] = PaperSort;
		ItemStack[] is;
		if (SlotDataMethods.getAccContent(p, i + 1) != null) {
			is = SlotDataMethods.getAccContent(p, i + 1).clone();
			for (int i = 0; i < is.length; i++) {
				if (is[i] != null) {
					content[i + 2] = is[i].clone();
				} else {
					content[i + 2] = null;
				}
			}
		}

		Inventory inv = b.getServer().createInventory(p, 54);
		inv.setContents(content);
		p.openInventory(inv);
		//CustomInventory ci = new CustomInventory(content.clone(), "Slot: " + (i + 1));
		//sp.openInventoryWindow(ci);
		PlayerState.getPlayerState(p).setPs(PlayerStatus.SLOT);
		PlayerState.getPlayerState(p).setSlot(i + 1);
	}

}
