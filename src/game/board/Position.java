package game.board;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import game.players.*;

/**
 * This class models a position of the board. 
 * @author Lukas
 *
 */
public class Position {
	private int index;
	/** 
	 * adjacency list of the position
	 */
	private List<Position> adjPos;
	/** 
	* the player object occupying the position
	* if unocuppied -> NULL
	*/
	private Player playerOccupying;
	
	public Position() {
		adjPos = new ArrayList<Position>();
	}
	
	public Position(int index) {
		this.index = index;	
		adjPos = new ArrayList<Position>();
	}
	
	public Position(int index, List<Position> adj) {
		this.index = index;
		adjPos = new ArrayList<Position>(adj);
	}
	
	public int getIndex() {
		return index;
	}
	
	public void addAdjPos(Position ...adj) {
		adjPos.clear();
		adjPos.addAll(Arrays.asList(adj));
	}
	
	public List<Position> getAdjacentPositions() {
		return adjPos;
	}
	
	public Player getPlayerOccupying() {
		return playerOccupying;
	}
	
	public void setPlayerOccupying(Player playerOccupying) {
		this.playerOccupying = playerOccupying;
	}
	
}
