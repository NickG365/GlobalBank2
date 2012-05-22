package net.ark3l.globalbank2.delayedTasks;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.PlayerState.PlayerStatus;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DelayedBank implements Runnable {
	public Player p;
	public GlobalBank b;
	public int maxSlots;

	public DelayedBank(Player p, GlobalBank b, int maxSlots) {
		this.p = p;
		this.b = b;
		this.maxSlots = maxSlots;
	}

	public void run() {
		b.removeContents(p);
		ItemStack[] content = new ItemStack[54];
		ArrayList<ItemStack> iss = new ArrayList<ItemStack>();
		for (int i = 0; (i < content.length); i++) {
			ItemStack is;
			if(i < maxSlots) // fill with chests only up to the max slots for the player
		        is = new ItemStack(Material.CHEST, i + 1);
			else
				is = new ItemStack(Material.STONE);

			content[i] = is;
			iss.add(is);
		}
		b.isk.put(p, iss);
		String s = p.getName();
		if (p.getName().length() > 11)
			s = s.substring(0, 10);
		Inventory inv = b.getServer().createInventory(p, 54, s);
		inv.setContents(content);
		p.openInventory(inv);
		//CustomInventory ci = new CustomInventory(content, "Bank:" + s);
		//p.openInventoryWindow(ci);
		PlayerState.getPlayerState(p).setPs(PlayerStatus.CHEST_SELECT);
	}

}
