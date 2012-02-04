package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCMooshroom;

/**
 * Represents a Mooshroom NPC.
 */
public class MooshroomNPC extends BaseNPC {

	public MooshroomNPC(NPCMooshroom entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
