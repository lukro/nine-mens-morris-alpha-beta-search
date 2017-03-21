package game.board;
/**
 * This class represents a move with its source position, destination position
 * and, if necessary, the position of the piece to remove (when move makes a mill).
 * It implements Comparable for presorting of the moves in Alpha Beta Pruning
 * @author Lukas
 */
public class Move implements Comparable<Move> {
	private final Position source;
	private final Position destination;
	private Position pieceToRemove;
	
	public Move(Position source, Position destination, Position pieceToRemove) {
		this.source = source;
		this.destination = destination;
		this.pieceToRemove = pieceToRemove;
	}
	
	/**
	 * Copy Constructor
	 * needed for copies required by moves creating a mill (one copy for each possible piece to remove)
	 * @param moveToCopy The moving to be copied
	 */
	public Move(Move moveToCopy) {
		this.source = moveToCopy.source;
		this.destination = moveToCopy.destination;
		this.pieceToRemove = moveToCopy.pieceToRemove;
	}
	public Position getPieceToRemove() {
		return pieceToRemove;
	}
	
	/**
	 * Sets the 
	 * @param pieceToRemove Position of the piece to remove
	 * @throws IllegalArgumentException if the position has no occupying player
	 */
	public void setPieceToRemove(Position pieceToRemove) throws IllegalArgumentException {
		if(pieceToRemove.getPlayerOccupying() == null) {
			throw new IllegalArgumentException();
		}
		this.pieceToRemove = pieceToRemove;
	}
	public Position getSource() {
		return source;
	}
	public Position getDestination() {
		return destination;
	}

	/**
	 * Provides a natural ordering based on the question if the move creates a mill
	 * Moves are considered lower in value if the create a mill, so that they will be 
	 * processed first
	 * @param otherMove Move to compare this move to
	 * @return 0 if they equal, 1 if the other move makes a mill and this one not, -1 if the opposite is the case
	 */
	@Override
	public int compareTo(Move otherMove) {
		if(otherMove.getPieceToRemove() != null && this.getPieceToRemove() != null 
				|| otherMove.getPieceToRemove() == null && this.getPieceToRemove() == null) {
			return 0;	
		}
		else if (otherMove.getPieceToRemove() != null && this.getPieceToRemove() == null) {
			return 1;
		}
		else {
			return -1;
		}
	}
}
