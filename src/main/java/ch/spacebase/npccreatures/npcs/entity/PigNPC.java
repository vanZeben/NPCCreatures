package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCPig;

/**
 * Represents a Pig NPC.
 */
public class PigNPC extends BaseNPC {

	public PigNPC(NPCPig entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
