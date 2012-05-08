package net.ark3l.globalbank2.delayedTasks;

import java.util.ArrayList;

import net.ark3l.globalbank2.GlobalBank;
import net.ark3l.globalbank2.PlayerState;
import net.ark3l.globalbank2.PlayerState.PlayerStatus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class DelayedBank implements Runnable {
	public Player p;
	public GlobalBank b;
	SpoutPlayer sp;

	public DelayedBank(Player p, GlobalBank b) {
		this.p = p;
		this.b = b;
		this.sp = SpoutManager.getPlayer(p);
	}

	@Override
	public void run() {
		b.removeContents(p);
		ItemStack[] content = new ItemStack[54];
		ArrayList<ItemStack> iss = new ArrayList<ItemStack>();
		for (int i = 0; (i < content.length); i++) {
			ItemStack is = new ItemStack(Material.CHEST, i + 1);
			content[i] = is;
			iss.add(is);

		}
		b.isk.put(p, iss);
		String s = p.getName();
		if (p.getName().length() > 11)
			s = s.substring(0, 10);
		;
		CustomInventory ci = new CustomInventory(content, "Bank:" + s);
		sp.openInventoryWindow(ci);
		PlayerState.getPlayerState(p).setPs(PlayerStatus.CHEST_SELECT);
	}

}
