package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCZombie;

/**
 * Represents a Zombie NPC.
 */
public class ZombieNPC extends BaseNPC {

	public ZombieNPC(NPCZombie entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
