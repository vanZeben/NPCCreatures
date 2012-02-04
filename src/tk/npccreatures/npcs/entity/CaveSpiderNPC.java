package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCCaveSpider;

/**
 * Represents a CaveSpider NPC.
 */
public class CaveSpiderNPC extends BaseNPC {

	public CaveSpiderNPC(NPCCaveSpider entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
