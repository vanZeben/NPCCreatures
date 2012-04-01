package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCSpider;

public class SpiderNPC extends BaseNPC {

	/**
	 * Represents a Spider NPC.
	 */
	public SpiderNPC(NPCSpider entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
