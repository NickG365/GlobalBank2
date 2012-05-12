package com.topcat.npclib.entity;

import com.topcat.npclib.entity.HumanNPC;
import com.topcat.npclib.nms.NPCEntity;
import org.bukkit.Material;

public class BankerNPC extends HumanNPC {

	public String bankName;

	public BankerNPC(NPCEntity npcEntity, String bankName) {
		super(npcEntity);
		setItemInHand(Material.PAPER);
		this.bankName = bankName;
	}

}
