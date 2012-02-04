package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCSpider;

public class SpiderNPC extends BaseNPC {

	/**
	 * Represents a Spider NPC.
	 */
	public SpiderNPC(NPCSpider entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
