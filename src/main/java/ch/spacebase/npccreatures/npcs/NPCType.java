package ch.spacebase.npccreatures.npcs;

import net.minecraft.server.Entity;
import ch.spacebase.npccreatures.npcs.nms.*;

public enum NPCType {

	/**
	 * Represents a Human NPC.
	 */
	HUMAN(NPCHuman.class),
	/**
	 * Represents a Villager NPC.
	 */
	VILLAGER(NPCVillager.class),
	/**
	 * Represents a Zombie NPC.
	 */
	ZOMBIE(NPCZombie.class),
	/**
	 * Represents a Spider NPC.
	 */
	SPIDER(NPCSpider.class),
	/**
	 * Represents a Skeleton NPC.
	 */
	SKELETON(NPCSkeleton.class),
	/**
	 * Represents a Creeper NPC.
	 */
	CREEPER(NPCCreeper.class),
	/**
	 * Represents an Enderman NPC.
	 */
	ENDERMAN(NPCEnderman.class),
	/**
	 * Represents a Blaze NPC.
	 */
	BLAZE(NPCBlaze.class),
	/**
	 * Represents a MagmaCube NPC.
	 */
	MAGMACUBE(NPCMagmaCube.class),
	/**
	 * Represents a PigZombie NPC.
	 */
	PIGZOMBIE(NPCPigZombie.class),
	/**
	 * Represents a CaveSpider NPC.
	 */
	CAVESPIDER(NPCCaveSpider.class),
	/**
	 * Represents a Slime NPC.
	 */
	SLIME(NPCSlime.class),
	/**
	 * Represents a Silverfish NPC.
	 */
	SILVERFISH(NPCSilverfish.class),
	/**
	 * Represents a Snowman NPC.
	 */
	SNOWMAN(NPCSnowman.class),
	/**
	 * Represents a Pig NPC.
	 */
	PIG(NPCPig.class),
	/**
	 * Represents a Chicken NPC.
	 */
	CHICKEN(NPCChicken.class),
	/**
	 * Represents a Cow NPC.
	 */
	COW(NPCCow.class),
	/**
	 * Represents a Sheep NPC.
	 */
	SHEEP(NPCSheep.class),
	/**
	 * Represents a Wolf NPC.
	 */
	WOLF(NPCWolf.class),
	/**
	 * Represents a Squid NPC.
	 */
	SQUID(NPCSquid.class),
	/**
	 * Represents a Mooshroom NPC.
	 */
	MOOSHROOM(NPCMooshroom.class),
	/**
	 * Represents an EnderDragon NPC.
	 */
	ENDERDRAGON(NPCEnderDragon.class),
	/**
	 * Represents a Ghast NPC.
	 */
	GHAST(NPCGhast.class),
	/**
	 * Represents a Giant NPC.
	 */
	GIANT(NPCGiant.class),
	/**
	 * Represents a Ocelot NPC.
	 */
	OCELOT(NPCOcelot.class),
	/**
	 * Represents a Iron Golem NPC.
	 */
	IRONGOLEM(NPCIronGolem.class);
	
	private Class<? extends Entity> clazz;
	
	private NPCType(Class<? extends Entity> clazz) {
		this.clazz = clazz;
	}
	
	public Class<? extends Entity> getNPCClass() {
		return this.clazz;
	}
	
	public static NPCType getByName(String name) {
		for(NPCType type : values()) {
			if(type.name().equalsIgnoreCase(name)) return type;
		}
		
		return null;
	}

}
