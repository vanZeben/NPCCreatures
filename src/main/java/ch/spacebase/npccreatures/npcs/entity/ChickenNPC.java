package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCChicken;

/**
 * Represents a Chicken NPC.
 */
public class ChickenNPC extends BaseNPC {

	public ChickenNPC(NPCChicken entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
