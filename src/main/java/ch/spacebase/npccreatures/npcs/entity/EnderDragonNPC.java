package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCEnderDragon;

/**
 * Represents an EnderDragon NPC.
 */
public class EnderDragonNPC extends BaseNPC {

	public EnderDragonNPC(NPCEnderDragon entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
