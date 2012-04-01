package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCMooshroom;

/**
 * Represents a Mooshroom NPC.
 */
public class MooshroomNPC extends BaseNPC {

	public MooshroomNPC(NPCMooshroom entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
