package ch.spacebase.npccreatures.npcs.pathing;

import java.util.ArrayList;

import org.bukkit.Location;

/**
 * Represents the path of an NPC.
 */
public class NPCPath {

	private ArrayList<Node> path;
	private NPCPathFinder pathFinder;
	private Location end;

	public NPCPath(NPCPathFinder npcPathFinder, ArrayList<Node> path, Location end) {
		this.path = path;
		this.end = end;
		this.pathFinder = npcPathFinder;
	}

	/**
	 * Gets the end location of the path.
	 * 
	 * @return The end of the path.
	 */
	public Location getEnd() {
		return end;
	}

	/**
	 * Gets a list of nodes in the path.
	 * 
	 * @return The list of nodes in the path.
	 */
	public ArrayList<Node> getPath() {
		return path;
	}

	public boolean checkPath(Node node, Node parent, boolean update) {
		return pathFinder.checkPath(node, parent, update);
	}

}