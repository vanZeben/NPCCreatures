package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCCow;

/**
 * Represents a Cow NPC.
 */
public class CowNPC extends BaseNPC {

	public CowNPC(NPCCow entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
