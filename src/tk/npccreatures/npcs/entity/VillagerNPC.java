package tk.npccreatures.npcs.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import tk.npccreatures.npcs.nms.NPCVillager;

import net.minecraft.server.EntityVillager;

/**
 * Represents a Villager NPC.
 */
public class VillagerNPC extends NPC {

	public VillagerNPC(NPCVillager entity, String name) {
		super(entity, name);
		entity.setBukkitEntity(this);
	}

	/**
	 * Represents a Villager's skin type.
	 */
	public enum SkinType {
		REGULAR(5), FARMER(0), LIBRARIAN(1), PRIEST(2), BLACKSMITH(3), BUTCHER(4);

		private int id;

		private SkinType(int id) {
			this.id = id;
		}

		/**
		 * Gets the skin ID.
		 * 
		 * @return The skin ID.
		 */
		public int getId() {
			return this.id;
		}

		/**
		 * Gets the skin type belonging to a skin ID.
		 * 
		 * @param The
		 *            ID of the skin type.
		 * @return The skin type.
		 */
		public static SkinType fromId(int id) {
			switch (id) {
			case 0:
				return SkinType.FARMER;
			case 1:
				return SkinType.LIBRARIAN;
			case 2:
				return SkinType.PRIEST;
			case 3:
				return SkinType.BLACKSMITH;
			case 4:
				return SkinType.BUTCHER;
			default:
				return SkinType.REGULAR;
			}
		}
	}

	/**
	 * Sets the skin type of an NPC.
	 * 
	 * @param The
	 *            skin type to set.
	 */
	public void setSkin(SkinType type) {
		try {
			Field field = EntityVillager.class.getDeclaredField("profession");
			field.setAccessible(true);
			field.setInt(this.entity, type.getId());
			Method method = EntityVillager.class.getDeclaredMethod("y", new Class<?>[] {});
			method.setAccessible(true);
			method.invoke((EntityVillager) this.entity, new Object[] {});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the skin type of the NPC.
	 * 
	 * @return The NPCs skin type.
	 */
	public SkinType getSkin() {
		try {
			Field field = EntityVillager.class.getDeclaredField("profession");
			field.setAccessible(true);
			return SkinType.fromId(field.getInt(this.entity));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return SkinType.REGULAR;
	}

}
