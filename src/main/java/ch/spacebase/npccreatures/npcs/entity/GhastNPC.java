package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCGhast;

/**
 * Represents a Ghast NPC.
 */
public class GhastNPC extends BaseNPC {

	public GhastNPC(NPCGhast entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
