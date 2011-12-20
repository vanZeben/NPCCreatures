package me.steveice10.npccreatures;

public abstract class ResourceRunnable implements Runnable {
	protected Object[] params;
	
	public ResourceRunnable(Object... parameters) {
		params = parameters;
	}
}