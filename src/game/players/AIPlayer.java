package game.players;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import game.*;
import game.board.*;

/**
 * This class models the Computer Player
 */
public class AIPlayer extends Player {
	private int depth;
	
	/**
	 * Constructor calling its superclass constructor 
	 * @param symbol shown in showPos 
	 * @see Game.showPos()
	 * @param depth of the Alpha Beta Pruning Search
	 */
	public AIPlayer(char symbol, int depth) {
		super(symbol);
		this.depth = depth;
	}
	
	/**
	 * Outer method of the Alpha Beta Pruning Search. It identifies the best move by going 
	 * one level deeper in the tree and look for the best rated move
	 * @param game The game where everything takes places
	 * @return A random move of the best rated moves (if there is more than one best moves) or the single best move
	 */
	public Move searchForBestMove(Game game) {
		List<Move> moves = game.generatePossibleMoves(game.getAIPlayer());
		//presorts the moves to speed up the Alpha Beta search
		Collections.sort(moves);
		
		int value, bestValue = Integer.MIN_VALUE + 1;
		List<Move> bestMoves = new ArrayList<Move>();
		
		//look for the best moves and add them into a list 
		for(Move move : moves) {
			game.applyMove(move, game.getAIPlayer());
			//add one to MIN_VALUE, because Integer has not a symmetric range 
			//had it wrong at first, it took a long time to find out the reason for the misbehavior caused by this
			value = -alphaBeta(game, game.getHumanPlayer(), depth-1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
			game.undoMove(move, game.getAIPlayer());
			
			//only keep the best moves in the List
			//if new better move is found, clear the list and insert it 
			if(value > bestValue) {
				bestValue = value;
				bestMoves.clear();
				bestMoves.add(move);
			}
			else if(value == bestValue) {
				bestMoves.add(move);
			}
	
		}
		//retrieve a random item of the list to ensure variety of the game
		Random r = new Random();
		int randIndex = r.nextInt(bestMoves.size());
		return bestMoves.get(randIndex);
	}
	
	/**
	 * The actual Alpha Beta Pruning search. 
	 * The algorithm is implemented in a Negamax manner.
	 * @param game The game where everything takes places
	 * @param player The player of the current depth
	 * @param remainingDepth
	 * @param alpha 
	 * @param beta
	 * @return The recent alpha value.
	 * @see https://en.wikipedia.org/wiki/Negamax#Negamax_with_alpha_beta_pruning
	 */
	public int alphaBeta(Game game, Player player, int remainingDepth, int alpha, int beta) {
		
		if(remainingDepth == 0)
			return evaluate(game, player);
		List<Move> possibleMoves = game.generatePossibleMoves(player);
		//Presorts the moves to speed up the Alpha Beta search
		Collections.sort(possibleMoves);
		
		/*
		 * If the recent move caused a game over, rate this move very significantly.
		 * 
		 * Also take the depth of the game over into account to bypass a well known weakness of the 
		 * Minimax algorithm in general. 
		 * Namely, the algorithm takes a random choice if it either knows it wins anyway within the next magnitude_of_depth moves.
		 * Or, what is even worse, if it knows it looses within the next magnitude_of_depth of moves, provided that the human player plays 
		 * <b> perfect </b>. For a detailed explanation: http://neverstopbuilding.com/minimax
		 */
		if(game.hasLost(player, possibleMoves)) {
			return -1000-remainingDepth*10;
		}
		else if(game.hasLost(game.getOtherPlayer(player))) {
			return 1000+remainingDepth*10;
		}

		
		for(Move m: possibleMoves) {
			game.applyMove(m, player);
			int value = -alphaBeta(game, game.getOtherPlayer(player), remainingDepth-1,-beta, -alpha);
			game.undoMove(m, player);
			if(value > alpha) {
				alpha = value;
			}
			if(alpha >= beta) {
				break;
			}
		}
		return alpha;
	}

	/**
	 * Evaluation of a leaf node in the tree.
	 * @param game The game where everything takes place
	 * @param player The player at the deepest level of the search tree
	 * @return The score indicating how good the game situation is for player
	 */
	public int evaluate(Game game, Player player) {
		//see
		int playerNumOfMills = 0, opponentNumOfMills = 0;
		int playerNumOfTwoPieceConf = 0, opponentNumOfTwoPieceConf = 0;
		int playerNumOfThreePieceConf = 0, opponentNumOfThreePieceConf = 0;
		int playerNumOfDoubleMills = 0, opponentNumOfDoubleMills = 0;
		int playerNumOfBlockedPieces = getNumOfBlockedPieces(game, player);
		int opponentNumOfBlockedPieces = getNumOfBlockedPieces(game, game.getOtherPlayer(player));

		//lists saving all player and opponent rows (two piece configurations or mills)
		//to calculate three piece configurations and double Mills
		List<List<Position>> playerRows = new ArrayList<List<Position>>();
		List<List<Position>> opponentRows = new ArrayList<List<Position>>();

		for(int i = 0; i < Board.POSSIBLE_MILLS.length; i++) {
			int[] possibleMill = Board.POSSIBLE_MILLS[i]; 
			int playerPieces = 0, opponentPieces = 0, empty = 0;
			//preventively create a new Position array in case the row might make a mill or a two piece configuration
			playerRows.add(new ArrayList<Position>());
			opponentRows.add(new ArrayList<Position>());
			for(int j = 0; j < 3; j++) {
				Position pos = game.getBoard().getPos(possibleMill[j]);
				Player playerOccupying = pos.getPlayerOccupying();
				//count the number of player´s pieces, its opponents pieces and the empty ones
				if(playerOccupying == player) {
					playerPieces++;
					playerRows.get(playerRows.size() -1).add(pos);
				} else if(playerOccupying == null) {
					empty++;
				} else { 
					opponentPieces++;
					opponentRows.get(opponentRows.size() -1).add(pos);
				}
			}
			//now you determine what kind of configurations exists
			if(playerPieces == 3) {
				playerNumOfMills++;
				//if player has a mill here, we can delete the opponent row
				opponentRows.remove(opponentRows.size() - 1);
			} else if(opponentPieces == 3) {
				opponentNumOfMills++;
				//analogous to previous if statement
				playerRows.remove(playerRows.size() - 1);
			} 
			else if(playerPieces == 2 && empty == 1) {
				playerNumOfTwoPieceConf++;
				opponentRows.remove(opponentRows.size() - 1);

			} else if(opponentPieces == 2 && empty == 1) {
				opponentNumOfTwoPieceConf++;
				playerRows.remove(playerRows.size() - 1);
			} else {
				//if there´s neither a two piece conf nor a mill, we don´t need to save the row
				playerRows.remove(playerRows.size() - 1);
				opponentRows.remove(opponentRows.size() - 1);

			}
		}
		
		
		playerNumOfThreePieceConf = findDuplicates(playerRows);
		opponentNumOfThreePieceConf = findDuplicates(opponentRows);
		playerNumOfDoubleMills = findDoubleMills(game, player, playerRows);
		opponentNumOfDoubleMills = findDoubleMills(game, game.getOtherPlayer(player), opponentRows);

		//calculation of the actual differences
		int millDiff = playerNumOfMills - opponentNumOfMills;
		int doubleMillDiff = playerNumOfDoubleMills - opponentNumOfDoubleMills;
		int twoPieceConfDiff = playerNumOfTwoPieceConf - opponentNumOfTwoPieceConf;
		int threePieceConfDiff = playerNumOfThreePieceConf - opponentNumOfThreePieceConf;
		int diffOfPieces = player.getNumOfRemainingPieces() - game.getOtherPlayer(player).getNumOfRemainingPieces();
		int blockedPiecesDiff = opponentNumOfBlockedPieces - playerNumOfBlockedPieces; 

		int score;
		if(player.getGamePhase() == GamePhase.PLACING_PIECES) {
			score = 10 * doubleMillDiff + 20* millDiff + 30*diffOfPieces + 6*twoPieceConfDiff + 5 * threePieceConfDiff + 1 * blockedPiecesDiff;
		} else if(player.getGamePhase() == GamePhase.MOVING_PIECES) {
			score = 40 * doubleMillDiff + 20*millDiff + 30*diffOfPieces + 3*twoPieceConfDiff + 2 * threePieceConfDiff + 7 * blockedPiecesDiff;
		} else {
			score = 50 * doubleMillDiff + 10*millDiff + 30*diffOfPieces + 10*twoPieceConfDiff + 5 * threePieceConfDiff;
		}
		return score;
		
	}
	
	
	/**
	 * This method calculates the number of three piece confs 
	 * by searching the list of lists for positions that occur 
	 * in more than one Two Piece Conf
	 * @param rows Every Two Piece Conf or mill of the player
	 * @return Number of Three Pieces Confs
	 */
	public int findDuplicates(List<List<Position>> rows) {
		//convert two dimensional list (list of lists) to one dimensional list (single list)
		List<Position> oneDimList = new ArrayList<Position>();
		for(List<Position> row: rows) {
			//only consider two piece confs
			if(row.size() == 2) {
				oneDimList.addAll(row);
			}
		}
		//now find number of duplicates by creating a Set (only contains unique elements) 
		//and substract its size from the list of all row positions
		Set<Position> positions = new HashSet<Position>(oneDimList);
		int numberOfDuplicates = oneDimList.size() - positions.size();
		return numberOfDuplicates;
	}
	
	/**
	 * This method determines the number of Three Piece Configurations 
	 * @param game The game where everything takes place
	 * @param player The player owning the double mills
	 * @param rows The rows with Two Piece Confs or Mills
	 * @return Number of Three Piece Configurations 
	 */
	public int findDoubleMills(Game game, Player player, List<List<Position>> rows) {
		int numOfDoubleMills = 0;
		
		//checks for every mill (row.size() == 3) if a move of its pieces to the 
		//respective adjacent positions could create another mill
		for(List<Position> row: rows) {
			if(row.size() == 3) {
				for(Position pos: row) {
					for(Position adjPos : pos.getAdjacentPositions()) {
						if(adjPos.getPlayerOccupying() == null) {
							//execute Move partly to test if it creates a mill, but undo it afterwards
							//no need to apply Move, because the number of pieces doesn´t need to be changed 
							pos.setPlayerOccupying(null);
							adjPos.setPlayerOccupying(player);
							if(game.checkIfMill(player, new Move(pos, adjPos, null), null)) {
								numOfDoubleMills++;
							}
							adjPos.setPlayerOccupying(null);
							pos.setPlayerOccupying(player);
						}
					}
				}
			}
		}
		return numOfDoubleMills;
	}
	
	/**
	 * This method determines the player´s number of blocked pieces
	 * @param game The game where everything takes place
	 * @param player The player <b>owning</b> the blocked Pieces
	 * @return Number of the player´s blocked pieces 
	 */
	public int getNumOfBlockedPieces(Game game, Player player) {
		int numOfBlockedPieces = 0;
		for(int i = 0; i < Board.BOARD_SIZE; i++) {
			Position pos = game.getBoard().getPos(i);
			if(pos.getPlayerOccupying() == player) {
				for(Position adjPos: pos.getAdjacentPositions()) {
					if(adjPos.getPlayerOccupying() != game.getOtherPlayer(player)) {
						break;
					}
					numOfBlockedPieces++;
				}
			}
		}
		return numOfBlockedPieces;
	}

}
