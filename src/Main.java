import java.util.Scanner;
import game.*;
import game.players.*;
import game.board.*;

public class Main {
	private static Scanner input = new Scanner(System.in);
	private static final int MAX_MOVES = 100;
	private static Game game;
	
	public static void main(String[] args) {
		startGame();
	}
	
	
	/**
	 * Asks the user for the difficulty level 
	 * and the starting player
	 * @return The starting player 
	 */
	private static Player setupGame() {
		String userInput;
		boolean playerStarts;
		int depth;
		//use depth of the Minimax search as difficulty level
		System.out.println("Please choose a difficulty: (H)ard, (M)edium, (E)asy");
		userInput = input.nextLine().toUpperCase();
		if(userInput.equals("H") || userInput.equals("HARD")) {
			depth = 5;
		} else if(userInput.equals("M") || userInput.equals("MEDIUM")) {
			depth = 3;
		} else if(userInput.equals("E") || userInput.equals("EASY")) {
			depth = 1;
		} else {
			System.out.println("Command unknown");
			return null;
		}
		
		
		System.out.println("Do you want to start? (Yes|No)");
		userInput = input.nextLine().toUpperCase();
		Player currentPlayer;
		if(userInput.equals("YES")) {
			playerStarts = true;
		} else if(userInput.equals("NO")) {
			playerStarts = false;
		} else {
			System.out.println("Command unknown");
			return null;
		}
		//set the respective color of the player and the currentPlayer to the player beginning
		if(playerStarts) {
			game = new Game(depth, 'W', 'B');
			currentPlayer = game.getHumanPlayer();
		}
		else {
			game = new Game(depth, 'B', 'W');
			currentPlayer = game.getAIPlayer();
		}
		return currentPlayer;
	}
	
	/**
	 * The method starting the game and calling every method neccessary to play the game 
	 */
	private static void startGame()  {
		Player currentPlayer; 
		int numberOfMoves = 0;
		
		do { 
			currentPlayer = setupGame();
		} while(currentPlayer == null);
	
				
		while(!game.hasLost(currentPlayer) && numberOfMoves < MAX_MOVES) {
					
			if(currentPlayer.getGamePhase() == GamePhase.PLACING_PIECES) {
				//call firstPhase until the users gives valid input 
				while(!firstPhaseMove(game, currentPlayer));
			}
			
			else {
				//call secondPhase until the users gives valid input 
				while(!secondPhaseMove(game, currentPlayer));
			}
			numberOfMoves++;
			currentPlayer = game.getOtherPlayer(currentPlayer);
		}
		
		if(game.hasLost(game.getHumanPlayer())) {
			System.out.println("And the winner is...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("the AI");
		}
		
		else if (game.hasLost(game.getAIPlayer())){
			System.out.println("Surprisingly, you won.");
		}
		
		else if(numberOfMoves > MAX_MOVES) {
			System.out.println("No winner could be determined. It seems that you can play as good as the AI.");
		}
	}
	
	/**
	 * This method represents the placing pieces phase of the game
	 * @param game The game where everything takes place
	 * @param currentPlayer The player in turn
	 * @return Did the user give valid input?
	 */
	private static boolean firstPhaseMove(Game game, Player currentPlayer) {
		Move move;
		int index;

		if(currentPlayer instanceof AIPlayer) {
			//call Minimax search
			move = game.getAIPlayer().searchForBestMove(game);
			System.out.println("AI placed one piece on index " + move.getDestination().getIndex() + ".");
			//if the following attribute has a not null value, the AI made a mill
			if(move.getPieceToRemove() != null) {
				System.out.println();
				System.out.println("AI made a mill. It removed your piece at index " + move.getPieceToRemove().getIndex() + ".");
			}

		} else {
			game.printBoard();
			System.out.println("It's your turn. You're in the Placing Pieces Phase. Give an index where to place the piece.");
			try {
				index = input.nextInt(); //might throw InputMismatchException
				move = new Move(null, game.getBoard().getPos(index),null); //getPos might throw IllegalArgumentException
			}
			catch(Exception x) {
				System.out.println("Invalid Input. Try again.");
				//place the scanner to the next line, otherwise it will pass the given invalid input 
				input.nextLine();
				return false;
			}
		}
		
		if(game.applyMove(move, currentPlayer)) {
			checkMill(game, currentPlayer, move);
			System.out.println();
			return true;
		} else {
			System.out.println("Invalid Move. Try again.");
			return false;
		}
	}
	
	/**
	 * This method represents the moving pieces and flying pieces phase of the game
	 * @param game The game where everything takes place
	 * @param currentPlayer 
	 * @return Did the user give valid input?
	 */
	private static boolean secondPhaseMove(Game game, Player currentPlayer) {
		Move move;
		int sourceIndex, destIndex;
		if(currentPlayer instanceof AIPlayer) {
			//call minimax search
			move = game.getAIPlayer().searchForBestMove(game);
			System.out.println("AI moved piece from " + move.getSource().getIndex() + " to "+ move.getDestination().getIndex() + ".");
			if(move.getPieceToRemove() != null) {
				System.out.println();
				System.out.println("AI made a mill. It removed your piece at index " + move.getPieceToRemove().getIndex() + ".");
			}
		} else {
			game.printBoard();
			System.out.println("It's your turn. You're in the " + currentPlayer.getGamePhase() + " Phase. Give a source index:");
			try {
				sourceIndex = input.nextInt(); //might throw InputMismatchException
				System.out.println("Give a destination index:");
				destIndex = input.nextInt(); //might throw InputMismatchException
				move = new Move(game.getBoard().getPos(sourceIndex), game.getBoard().getPos(destIndex),null); //might throw IllegalArgumentException	
			}
			catch(Exception x) {
				System.out.println("Invalid Input. Try again.");
				//place the scanner to the next line, otherwise it will pass the given invalid input 
				input.nextLine();
				return false;
			}
		}
		if(game.applyMove(move, currentPlayer)) {
			checkMill(game, currentPlayer, move);
			System.out.println();
			return true;
		} else {
			System.out.println("Invalid Move. Try again.");
			return false;
		}
	}
	
	/**
	 * This method asks the user for the piece to remove if the player made a mill.
	 * It also validates the input.
	 * @param game The game where everything takes place
	 * @param currentPlayer  
	 * @param move The move that might made a mill
	 */
	private static void checkMill(Game game, Player currentPlayer, Move move) {
		//You don't need to check if the AIPlayer made a mill,
		//because it was already checked in the computation of the best move and applied in applyMove 
		if(game.checkIfMill(currentPlayer, move,null) && currentPlayer != game.getAIPlayer()) {
			
			int removeIndex;
			//endless loop for invalid user input
			while(true) {
				if(currentPlayer == game.getHumanPlayer()) {
					if(!game.allPiecesBelongToMill(game.getAIPlayer())) {
						//if the other player has only three pieces, the current player has now definitely won
						if(game.getOtherPlayer(currentPlayer).getNumOfRemainingPieces() <= 3) {
							game.getOtherPlayer(currentPlayer).decNumOfRemainingPieces();
							return;
						}
						System.out.println("You made a mill. Give the index of the opponent's piece to remove:");
						removeIndex = input.nextInt();
						if(game.removePiece(removeIndex, currentPlayer)) {
							break;
						} else {
							System.out.println("Invalid Removal. Try again.");
						}
					}
					else {
						System.out.println("You made a mill, but you can't remove a piece from your opponent (all his pieces belong to mills).");
						break;
					}
				}
			}
		}	
	}
}
