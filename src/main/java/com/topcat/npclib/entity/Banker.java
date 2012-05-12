package com.topcat.npclib.entity;

import com.topcat.npclib.nms.NPCEntity;
import net.minecraft.server.Entity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class Banker {

	public String bankName;
	private Entity entity;

	public Banker(Entity entity, String bankName) {
		this.entity = entity;
		setItemInHand(Material.PAPER);
		this.bankName = bankName;
	}


	public void setItemInHand(Material m) {
		setItemInHand(m, (short) 0);
	}

	public void setItemInHand(Material m, short damage) {
		((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
	}

	public void setName(String name) {
		((NPCEntity) getEntity()).name = name;
	}

	public String getName() {
		return ((NPCEntity) getEntity()).name;
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

	public void moveTo(Location l) {
		getBukkitEntity().teleport(l);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Banker) {
			return getBukkitEntity().getEntityId() == ((Banker) obj).getBukkitEntity().getEntityId();
		}
		return false;
	}

}
