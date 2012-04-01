package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCPigZombie;

/**
 * Represents a PigZombie NPC.
 */
public class PigZombieNPC extends BaseNPC {

	public PigZombieNPC(NPCPigZombie entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
