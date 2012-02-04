package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCEnderman;

/**
 * Represents an Enderman NPC.
 */
public class EndermanNPC extends BaseNPC {

	public EndermanNPC(NPCEnderman entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
