package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCWolf;

/**
 * Represents a Wolf NPC.
 */
public class WolfNPC extends BaseNPC {

	public WolfNPC(NPCWolf entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

	public void setSitting(boolean sitting) {
		((NPCWolf) this.entity).setSitting(sitting);
	}

	public void setAngry(boolean angry) {
		((NPCWolf) this.entity).setAngry(angry);
	}
	
	public void setTamed(boolean tame) {
		((NPCWolf) this.entity).setTamed(tame);
	}

	public boolean isAngry() {
		return ((NPCWolf) this.entity).isAngry();
	}

	public boolean isSitting() {
		return ((NPCWolf) this.entity).isSitting();
	}
	
	public boolean isTamed() {
		return ((NPCWolf) this.entity).isTamed();
	}

}
