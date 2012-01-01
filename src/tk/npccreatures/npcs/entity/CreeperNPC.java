package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCCreeper;

/**
 * Represents a Creeper NPC.
 */
public class CreeperNPC extends NPC {

	public CreeperNPC(NPCCreeper entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

	/**
	 * Sets the powered state of the NPC.
	 * 
	 * @param Whether
	 *            the NPC is powered or not.
	 */
	public void setPowered(boolean powered) {
		((NPCCreeper) this.entity).setPowered(true);
	}

	/**
	 * Gets the powered state of the NPC.
	 * 
	 * @return The powered state of the NPC.
	 */
	public boolean isPowered() {
		return ((NPCCreeper) this.entity).isPowered();
	}

}
