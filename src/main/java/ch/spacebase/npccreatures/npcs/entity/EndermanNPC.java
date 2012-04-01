package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCEnderman;

/**
 * Represents an Enderman NPC.
 */
public class EndermanNPC extends BaseNPC {

	public EndermanNPC(NPCEnderman entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}
	
	public int getHeldId() {
		return ((NPCEnderman) this.entity).getCarriedId();
	}
	
	public int getHeldData() {
		return ((NPCEnderman) this.entity).getCarriedData();
	}
	
	public void setHeldId(int id) {
		((NPCEnderman) this.entity).setCarriedId(id);
	}
	
	public void setHeldData(int data) {
		((NPCEnderman) this.entity).setCarriedData(data);
	}

}
