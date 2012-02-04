package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCGhast;

/**
 * Represents a Ghast NPC.
 */
public class GhastNPC extends BaseNPC {

	public GhastNPC(NPCGhast entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
