package ch.spacebase.npccreatures.npcs.entity;

import ch.spacebase.npccreatures.npcs.nms.NPCSkeleton;

public class SkeletonNPC extends BaseNPC {

	/**
	 * Represents a Skeleton NPC.
	 */
	public SkeletonNPC(NPCSkeleton entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
