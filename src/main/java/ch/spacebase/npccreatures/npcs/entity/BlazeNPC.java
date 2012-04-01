package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCBlaze;

/**
 * Represents a Blaze NPC.
 */
public class BlazeNPC extends BaseNPC {

	public BlazeNPC(NPCBlaze entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
