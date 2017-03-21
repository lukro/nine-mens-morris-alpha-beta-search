package game.players;
import game.*;


/**
 * This class models the player and maintains the number of placed and remaining pieces 
 * as well as the gamePhase, the player is in
 * @author Lukas
 *
 */
public class Player {

	private GamePhase gamePhase;
	private int numOfPlacedPieces;
	private int numOfRemainingPieces;
	/**
	 * The symbol to display in the console
	 */
	private char symbol;
	
	public Player(char symbol) {
		this.gamePhase = GamePhase.PLACING_PIECES;
		this.numOfPlacedPieces = 0;
		this.numOfRemainingPieces = 9;
		this.symbol = symbol;
	}
	
	public GamePhase getGamePhase() {
		return gamePhase;
	}
	
	public char getSymbol() {
		return symbol;
	}
	
	public void setGamePhase(GamePhase gamePhase) {
		this.gamePhase = gamePhase;
	}
	
	/**
	 * This method updates the Game Phase when need,
	 * e.g. when the number of remaining pieces is changed
	 */
	private void updateGamePhase() {
		if(numOfPlacedPieces < 9) {
			this.gamePhase = GamePhase.PLACING_PIECES;
		}
		else if (numOfRemainingPieces > 3){
			this.gamePhase = GamePhase.MOVING_PIECES;
		}
		else {
			this.gamePhase = GamePhase.FLYING_PIECES;
		}

	}

	/**
	 * This method increments the player's number of placed pieces
	 */
	public void incNumOfPlacedPieces() {
		numOfPlacedPieces++;
		updateGamePhase();
	}
	/**
	 * This method decrements the player's number of placed pieces
	 */
	public void decNumOfPlacedPieces() {
		numOfPlacedPieces--;
		updateGamePhase();		
	}
	/**
	 * This method increments the player's number of remaining pieces
	 */
	public void incNumOfRemainingPieces() {
		numOfRemainingPieces++;
		updateGamePhase();
	}
	/**
	 * This method decrements the player's number of remaining Pieces
	 */
	public void decNumOfRemainingPieces() {
		numOfRemainingPieces--;
		updateGamePhase();
	}

	public int getNumOfPlacedPieces() {
		return numOfPlacedPieces;
	}

	public int getNumOfRemainingPieces() {
		return numOfRemainingPieces;
	}
	
	
}
