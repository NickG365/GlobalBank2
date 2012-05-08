package net.ark3l.globalbank2;

import org.bukkit.Material;

import java.io.Serializable;

public class BMaterialData implements Serializable {
	private static final long serialVersionUID = 1385103110780786554L;
	@SuppressWarnings("unused")
	private final int type;
	private byte data = 0;

	public BMaterialData(final int type) {
		this(type, (byte) 0);
	}

	public BMaterialData(final Material type) {
		this(type, (byte) 0);
	}

	public BMaterialData(final int type, final byte data) {
		this.type = type;
		this.data = data;
	}

	public BMaterialData(final Material type, final byte data) {
		this(type.getId(), data);
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}
}