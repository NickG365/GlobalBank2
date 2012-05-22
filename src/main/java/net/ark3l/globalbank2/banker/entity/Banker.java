package net.ark3l.globalbank2.banker.entity;

import net.minecraft.server.Entity;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class Banker {

	public final String bankName;
	private final Entity entity;

	public Banker(Entity entity, String bankName) {
		this.entity = entity;
		this.bankName = bankName;
		setItemInHand(Material.PAPER);
	}


	public void setItemInHand(Material m) {
		setItemInHand(m, (short) 0);
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
		if(obj instanceof Banker) {
			return getBukkitEntity().getEntityId() == ((Banker) obj).getBukkitEntity().getEntityId();
		}
		return false;
	}

}
