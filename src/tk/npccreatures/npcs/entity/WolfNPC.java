package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCWolf;

/**
 * Represents a Wolf NPC.
 */
public class WolfNPC extends NPC {

	public WolfNPC(NPCWolf entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

	public void setSitting(boolean sitting) {
		((NPCWolf) this.entity).setSitting(true);
	}

	public void setAngry(boolean angry) {
		((NPCWolf) this.entity).setAngry(true);
	}

	public boolean isAngry() {
		return ((NPCWolf) this.entity).isAngry();
	}

	public boolean isSitting() {
		return ((NPCWolf) this.entity).isSitting();
	}

}
