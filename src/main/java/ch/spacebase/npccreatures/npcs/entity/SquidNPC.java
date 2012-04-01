package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCSquid;

public class SquidNPC extends BaseNPC {

	/**
	 * Represents a Squid NPC.
	 */
	public SquidNPC(NPCSquid entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
