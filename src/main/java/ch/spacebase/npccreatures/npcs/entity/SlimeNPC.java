package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCSlime;

public class SlimeNPC extends BaseNPC {

	/**
	 * Represents a Slime NPC.
	 */
	public SlimeNPC(NPCSlime entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

	public void setSize(int size) {
		((NPCSlime) this.entity).setSize(size);
	}

	public int getSize() {
		return ((NPCSlime) this.entity).getSize();
	}

}
