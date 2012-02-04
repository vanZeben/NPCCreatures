package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCGiant;

/**
 * Represents a Giant NPC.
 */
public class GiantNPC extends BaseNPC {

	public GiantNPC(NPCGiant entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
