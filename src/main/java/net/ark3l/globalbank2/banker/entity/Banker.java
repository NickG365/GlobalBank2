package net.ark3l.globalbank2.banker.entity;

import net.ark3l.globalbank2.banker.nms.NPCEntity;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Banker {

	public final String bankName;
	private final Entity entity;

	public Banker(Entity entity, String bankName) {
		this.entity = entity;
		this.bankName = bankName;
		setItemInHand(Material.PAPER, (short) 0);
	}

	public SpoutPlayer getSpoutPlayer() {
		try {
			Class.forName("org.getspout.spout.Spout");

			if (!(getEntity().getBukkitEntity() instanceof SpoutCraftPlayer)) {
				((NPCEntity) getEntity()).setBukkitEntity(new SpoutCraftPlayer((CraftServer) Bukkit.getServer(), (EntityPlayer) getEntity()));
			}

			return (SpoutPlayer) getEntity().getBukkitEntity();
		} catch (ClassNotFoundException e) {
			Bukkit.getServer().getLogger().warning("Cannot get spout player without spout installed");
		}
		return null;
	}

	public void setItemInHand(Material m, short damage) {
		((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
	}

	public Entity getEntity() {
		return entity;
	}

	public void removeFromWorld() {
		try {
			entity.world.removeEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public org.bukkit.entity.Entity getBukkitEntity() {
		return entity.getBukkitEntity();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Banker && getBukkitEntity().getEntityId() == ((Banker) obj).getBukkitEntity().getEntityId();
	}

}
