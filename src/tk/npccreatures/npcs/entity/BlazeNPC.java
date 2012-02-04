package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCBlaze;

/**
 * Represents a Blaze NPC.
 */
public class BlazeNPC extends BaseNPC {

	public BlazeNPC(NPCBlaze entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
