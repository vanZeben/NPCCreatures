package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCCow;

/**
 * Represents a Cow NPC.
 */
public class CowNPC extends BaseNPC {

	public CowNPC(NPCCow entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
