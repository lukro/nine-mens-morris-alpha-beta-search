package game;
/**
 * Enum for all three game phases of nine men's morris
 * including a String representation
 * @author Lukas
 *
 */
public enum GamePhase {
	PLACING_PIECES,MOVING_PIECES,FLYING_PIECES;
	
	@Override
	public String toString() throws IllegalArgumentException{
		switch(this) {
			case PLACING_PIECES: return "Placing Pieces";
			case MOVING_PIECES: return "Moving Pieces";
			case FLYING_PIECES: return "Flying Pieces";
			default : throw new IllegalArgumentException();
		}
	}
}
