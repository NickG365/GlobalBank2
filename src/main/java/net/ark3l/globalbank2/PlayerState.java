package net.ark3l.globalbank2;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerState {
	public static HashMap<Player, PlayerState> m = new HashMap<Player, PlayerState>();
	private Player p;
	private PlayerStatus ps = PlayerStatus.DEFAULT;
	private int slot = 0;
	private int BuyingSlot = 0;

	public enum PlayerStatus {
		CHEST_SELECT, SLOT, DEFAULT
	}

	public PlayerState(Player p) {
		this.p = p;
		PlayerState.m.put(p, this);
	}

	public Player getP() {
		return p;
	}

	public PlayerStatus getPs() {
		return ps;
	}

	public void setPs(PlayerStatus ps) {
		this.ps = ps;
	}

	public static PlayerState getPlayerState(Player p) {
		if (PlayerState.m.containsKey(p)) {
			return PlayerState.m.get(p);
		} else {
			return new PlayerState(p);
		}
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getBuyingSlot() {
		return BuyingSlot;
	}

	public void setBuyingSlot(int buyingSlot) {
		BuyingSlot = buyingSlot;
	}
}
