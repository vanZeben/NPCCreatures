package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCCaveSpider;

/**
 * Represents a CaveSpider NPC.
 */
public class CaveSpiderNPC extends BaseNPC {

	public CaveSpiderNPC(NPCCaveSpider entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
