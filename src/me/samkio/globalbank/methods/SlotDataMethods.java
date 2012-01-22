package me.samkio.globalbank.methods;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SlotDataMethods {

	public static void saveBank(Player p, ItemStack[] is, int slot) {
		is = removeFirst2(is);
		MiscMethods.getAccount(p).setItemStack(slot, is);
		MiscMethods.saveAll();
	}

	public static ItemStack[] removeFirst2(ItemStack[] is){
		ItemStack[] sendMe = new ItemStack[52];
		for (int i = 2; i < 54; i++) {
			sendMe[i-2] = is[i];
		}
		return sendMe;
	}
	
	public static ItemStack[] getAccContent(Player player, int slot) {
		return MiscMethods.getAccount(player).getItemStacksFromSlot(slot);
	}

}
