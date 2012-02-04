package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCSnowman;

public class SnowmanNPC extends BaseNPC {

	/**
	 * Represents a Snowman NPC.
	 */
	public SnowmanNPC(NPCSnowman entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
