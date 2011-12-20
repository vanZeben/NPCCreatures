package com.topcat.npclib.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.EntityVillager;

import com.topcat.npclib.nms.NPCVillager;

public class VillagerNPC extends NPC {

	public VillagerNPC(NPCVillager entity, String name)
	{
		super(entity, name);
	}
	
	public enum SkinType {
		REGULAR(5),
		FARMER(0),
		LIBRARIAN(1),
		PRIEST(2),
		BLACKSMITH(3),
		BUTCHER(4);
		
		private int id;
		
		private SkinType(int id)
		{
			this.id = id;
		}
		
		public int getId()
		{
			return this.id;
		}
	}
	
	public void setSkin(SkinType type)
	{
		try {
			Field field = EntityVillager.class.getDeclaredField("profession");
			field.setAccessible(true);
			field.setInt((EntityVillager)this.entity, type.getId());
			Method method = EntityVillager.class.getDeclaredMethod("y", new Class<?>[] {});
	    	method.setAccessible(true);
	    	method.invoke((EntityVillager)this.entity, new Object[] {});
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
