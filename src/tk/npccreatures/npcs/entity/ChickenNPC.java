package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCChicken;

/**
 * Represents a Chicken NPC.
 */
public class ChickenNPC extends NPC {

	public ChickenNPC(NPCChicken entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
