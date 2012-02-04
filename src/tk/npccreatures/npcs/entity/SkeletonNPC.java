package tk.npccreatures.npcs.entity;

import tk.npccreatures.npcs.nms.NPCSkeleton;

public class SkeletonNPC extends BaseNPC {

	/**
	 * Represents a Skeleton NPC.
	 */
	public SkeletonNPC(NPCSkeleton entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

}
