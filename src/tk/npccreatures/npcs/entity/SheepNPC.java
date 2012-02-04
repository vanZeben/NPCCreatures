package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCSheep;

/**
 * Represents a Sheep NPC.
 */
public class SheepNPC extends BaseNPC {

	public SheepNPC(NPCSheep entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
