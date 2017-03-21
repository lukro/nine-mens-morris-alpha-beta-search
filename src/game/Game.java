package game;
import java.util.ArrayList;
import java.util.List;
import game.players.*;
import game.board.*;

/**
 * This class commands the board and the players as well as 
 * the algorithms neccessary for the flow of play
 * @author Lukas
 *
 */
public class Game {
		private Board board;
		private Player HumanPlayer;
		private AIPlayer AIPlayer;
		
		public Game(int depth, char HumanSymbol, char AISymbol) {
			HumanPlayer = new Player(HumanSymbol);
			AIPlayer = new AIPlayer(AISymbol, depth);
			board = new Board();
		}
		
		public Player getHumanPlayer() {
			return HumanPlayer;
		}

		public AIPlayer getAIPlayer() {
			return AIPlayer;
		}

		/**
		 * used several times to get the respective other players object
		 * @param  player The player at hand
		 * @return The counterpart of the player
		 */
		public Player getOtherPlayer(Player player) {
			if(player == AIPlayer)
				return HumanPlayer;
			else
				return AIPlayer;
		}
		
		public Board getBoard() {
			return board;
		}
		
		/**
		 * This method prints the current state of the board with all remaining pieces 
		 */
		public void printBoard() {
			System.out.println(positionCharacter(0)+"- - - - - -"+positionCharacter(1)+"- - - - - -"+positionCharacter(2));
			System.out.println(" |             |             |");
			System.out.println(" |    "+positionCharacter(3)+"- - -"+positionCharacter(4)+"- - -"+positionCharacter(5)+"    |");
			System.out.println(" |     |       |       |     |");
			System.out.println(" |     |  "+positionCharacter(6)+"-"+positionCharacter(7)+"-"+positionCharacter(8)+"  |     |" );
			System.out.println(" |     |   |       |   |     |");
			System.out.println(positionCharacter(9)+"- -"+positionCharacter(10)+"-"+positionCharacter(11)+"     "+positionCharacter(12)+"-"+positionCharacter(13)+"- -" +positionCharacter(14));
			System.out.println(" |     |   |       |   |     |");
			System.out.println(" |     |  "+positionCharacter(15)+"-"+positionCharacter(16)+"-"+positionCharacter(17)+"  |     |" );
			System.out.println(" |     |       |       |     |");
			System.out.println(" |    "+positionCharacter(18)+"- - -"+positionCharacter(19)+"- - -"+positionCharacter(20)+"    |");
			System.out.println(" |             |             |");
			System.out.println(positionCharacter(21)+"- - - - - -"+positionCharacter(22)+"- - - - - -"+positionCharacter(23));
		}
		
		/**
		 * This method is used in printBoard to return the character to be displayed.
		 * @param index Index of the position to be returned
		 * @return Either the index or, if a piece is on this index, the symbol of the owning player
		 */
		private String positionCharacter(int index) {
			Player playerOccupying = board.getPos(index).getPlayerOccupying();
			//if there is no player at this position return the index
			if(playerOccupying == null) {
				if(index <= 9) {
					return " " + index + " ";
				}
				else 
					return Integer.toString(index) + " ";
			}
			//otherwise return its symbol
			else
				return " " + playerOccupying.getSymbol() + " ";
		}
		
		/**
		 * This method generates all possible moves of the player
		 * @param player 
		 * @return List of all possible moves including different taken pieces, if a move creates a mill
		 */
		public List<Move> generatePossibleMoves(Player player) {
			List<Move> possibleMoves = new ArrayList<Move>();
			
			if(player.getGamePhase() == GamePhase.PLACING_PIECES) {
				//moves don't have sources in this phase, so insert one move for every
				//unoccupied destination into the list
				for(int destIndex = 0; destIndex < Board.BOARD_SIZE; destIndex++) {
					//destination unoccupied?
					if(board.getPos(destIndex).getPlayerOccupying() == null) {
						Move move = new Move(null,board.getPos(destIndex),null);
						//execute Move partly to test if it creates a mill, but undo it afterwards
						//no need to apply Move, because the number of pieces does not need to be changed 
						board.getPos(destIndex).setPlayerOccupying(player);
						checkIfMill(player,move,possibleMoves);
						board.getPos(destIndex).setPlayerOccupying(null);
					}	
					
				}
			}
			
			if(player.getGamePhase() == GamePhase.MOVING_PIECES) {
				//insert one move into the list for every piece of the player for each unoccupied adjacent position, respectively
				for(int sourceIndex = 0; sourceIndex < Board.BOARD_SIZE; sourceIndex++) {
					//source occupied by player?
					if(board.getPos(sourceIndex).getPlayerOccupying() == player) {
						for(Position p : board.getPos(sourceIndex).getAdjacentPositions()) {
							//destination unoccupied?
							if(p.getPlayerOccupying() == null) {
								Move move = new Move(board.getPos(sourceIndex), p,null);
								//execute Move partly to test if it creates a mill, but undo it afterwards
								//no need to call applyMove, because the number of pieces does not need to be changed 
								board.getPos(sourceIndex).setPlayerOccupying(null);
								p.setPlayerOccupying(player);
								checkIfMill(player,move,possibleMoves);
								p.setPlayerOccupying(null);
								board.getPos(sourceIndex).setPlayerOccupying(player);
							}
							
						}
					}
				}		
			}
			if(player.getGamePhase() == GamePhase.FLYING_PIECES) {
				//insert one move into the list for every piece of the player for each unoccupied position of the board, respectively 
				for(int sourceIndex = 0; sourceIndex < Board.BOARD_SIZE; sourceIndex++) {
					//source occupied by player?
					if(board.getPos(sourceIndex).getPlayerOccupying() == player) {
						for(int destIndex = 0; destIndex < Board.BOARD_SIZE; destIndex++) {
							//destination unoccupied?
							if(board.getPos(destIndex).getPlayerOccupying() == null) {
								Move move = new Move(board.getPos(sourceIndex), board.getPos(destIndex), null);
								//execute Move partly to test if it creates a mill, but undo it afterwards
								//no need to apply Move, because the number of pieces does not need to be changed 
								board.getPos(sourceIndex).setPlayerOccupying(null);
								board.getPos(destIndex).setPlayerOccupying(player);
								checkIfMill(player,move,possibleMoves);
								board.getPos(destIndex).setPlayerOccupying(null);
								board.getPos(sourceIndex).setPlayerOccupying(player);
							}
							
						}
					}
					
				}
			}
			return possibleMoves;	
		}
	
		/**
		 * returns a boolean because it is used to determine whether a move by the human player is valid or not
		 * @param move
		 * @param player Player executing move
		 */
		public boolean applyMove(Move move, Player player) {
			//If the player is a human, the move has first to be validated, because humans make mistakes. 
			//The AI does only create valid moves ;)
			if(player == HumanPlayer) {
				if(!checkHumanMove(move)) {
					return false;
				}
			}
			Position position = move.getDestination();
			position.setPlayerOccupying(player);
			//if player is in the placing phase, increment the number of placed pieces
			if(move.getSource() == null) {
				player.incNumOfPlacedPieces();
			}
			else {
				//if the player is not in the placing phase anymore, the source position has the be set occupied
				Position source = move.getSource();
				source.setPlayerOccupying(null);
			}
			if(move.getPieceToRemove() != null) {
				//if the move made a mill, set the position of the piece to remove unoccupied
				Position toRemove = move.getPieceToRemove();
				toRemove.setPlayerOccupying(null);
				getOtherPlayer(player).decNumOfRemainingPieces();
			}
			return true;
		}
		
		/**
		 *This method is used in applyMove to validate the move, if the executing player is a human player
		 *@param move 
		 *@return Is the move valid?
		 */
		private boolean checkHumanMove(Move move) {
			//for every game phase the destination position has to be unoccupied
			if(!(move.getDestination().getPlayerOccupying() == null)) {
				return false;
			}
			//after the placing pieces phase the move must have a source position occupied by the player executing the move
			else if(HumanPlayer.getGamePhase() == GamePhase.FLYING_PIECES || HumanPlayer.getGamePhase() == GamePhase.MOVING_PIECES) {
				if(move.getSource().getPlayerOccupying() != HumanPlayer) {
					return false;
				}
			}
			//in the moving pieces phase the destination must be directly accessible from the source
			if(HumanPlayer.getGamePhase() == GamePhase.MOVING_PIECES) {
				//check if the destination is in the adjacency list of the source position
				List<Position> adjPos = move.getSource().getAdjacentPositions();
				for(Position p: adjPos) {
					if(move.getDestination() == p) {
						//the destination is in the adjacency list, so the move is valid 
						return true;
					}
				}
				//the destination is not in the adjacency list, so the move is invalid
				return false;
			}
			return true;
			
		}
		
		/**
		 * This method undoes every operation made in applyMove, see the respecting comments
		 * @param move
		 * @param player Player executing move
		 */
		public void undoMove(Move move, Player player) {
			//analogous to applyMove
			Position position = move.getDestination();
			position.setPlayerOccupying(null);
			if(move.getSource() == null) {
				player.decNumOfPlacedPieces();
			}
			else {
				Position source = move.getSource();
				source.setPlayerOccupying(player);
			}
			if(move.getPieceToRemove() != null) {
				Position toRestore = move.getPieceToRemove();
				toRestore.setPlayerOccupying(getOtherPlayer(player));
				getOtherPlayer(player).incNumOfRemainingPieces();
			}
		}
		
		/**
		 * This method checks, if a move makes a mill
		 * @param player The Player applying the move
		 * @param move Move that might make a mill
		 * @param possibleMoves The possibleMoves List used in generatePossibleMoves
		 * @return 
		 */
		public boolean checkIfMill(Player player, Move move, List<Move> possibleMoves)  {
			boolean completedMill = false;
			for(int i = 0; i < Board.POSSIBLE_MILLS.length; i++) { 
				int pieces = 0; 
				boolean moveContainsPiece = false;
				int[] mill = Board.POSSIBLE_MILLS[i];

				for(int x = 0; x < 3; x++) {
					//count the players pieces in the row of the possible mill
					if(board.getPos(mill[x]).getPlayerOccupying() == player) {
						pieces++;
					}
					//check if the possible mill was created by the move
					if(board.getPos(mill[x]) == move.getDestination()) {
						moveContainsPiece = true;
					}
				}
				
				if(pieces == 3 && moveContainsPiece) { 
					completedMill = true;
					if(!(possibleMoves == null)) {
						//you can only remove pieces that don't belong to mills
						if(allPiecesBelongToMill(getOtherPlayer(player))) {
							possibleMoves.add(move);
						}
						else {
							for(int k = 0; k < Board.BOARD_SIZE; k++) {
								Position pos = board.getPos(k);
		
								if(pos.getPlayerOccupying() == getOtherPlayer(player) && removable(pos, getOtherPlayer(player))) {
									//create a copy of the move and set its piece to remove
									//otherwise you use the same move object over the whole loop
									Move moveCopy = new Move(move);
									moveCopy.setPieceToRemove(pos);
									possibleMoves.add(moveCopy);
								}
								
							}
							return completedMill;
						}
					}
				}
			}
			if(!completedMill && possibleMoves != null) 
				possibleMoves.add(move);
			return completedMill;
		}
		
		/**
		 * Checks if the piece to remove belongs to a mill
		 * @param
		 * @return Is the piece removable?
		 */
		private boolean removable(Position pos, Player playerOwningPiece) {
			//when the player has less than 4 pieces you can remove all his pieces
			if(playerOwningPiece.getGamePhase() == GamePhase.FLYING_PIECES)
				return true;
			
			//same procedure as in checkIfMill
			for(int i = 0; i < Board.POSSIBLE_MILLS.length; i++) { 
				boolean pieceBelongsToIt = false;
				int pieces = 0; 
				int[] mill = Board.POSSIBLE_MILLS[i];

				for(int x = 0; x < 3; x++) {
					
					Position millPos = board.getPos(mill[x]);
					if(millPos.getPlayerOccupying() == playerOwningPiece) {
						pieces++;
					}
					if(millPos == pos) {
						pieceBelongsToIt = true;
					}
				}
				if(pieces == 3 && pieceBelongsToIt)
					return false;
			}
			return true;
		}
		
		/**
		 * This method checks if all pieces of player belong to a mill.
		 * It is used to decide whether you can remove a piece of player.
		 * @param player The player owning the pieces
		 * @return Do all pieces of player belong to mills`?
		 */
		public boolean allPiecesBelongToMill(Player player) {
			//iterate through every piece of the player and check if it is removable 
			for(int i = 0; i < Board.BOARD_SIZE; i++) {
				Position currentPos = board.getPos(i);
				//if you found one removable, you can return false
				if(currentPos.getPlayerOccupying() == player && removable(currentPos, player)) {
					return false;
				}
			}
			return true;
		}
		
		/**
		 * This method removes a piece from the board
		 * @param index The index of the piece to remove
		 * @param removingPlayer The player removing the piece
		 * @return Did the remove succeed?
		 */
		public boolean removePiece(int index, Player removingPlayer) {
			//The position has to be occupied by the other player 
			if(board.getPos(index).getPlayerOccupying() != getOtherPlayer(removingPlayer)) {
				return false;
			}
			board.getPos(index).setPlayerOccupying(null);
			getOtherPlayer(removingPlayer).decNumOfRemainingPieces();
			return true;
			
		}
		
		/**
		 * Determines whether a player lost.
		 * @param currentPlayer 
		 * @return Did currentPlayer loose?
		 */
		public boolean hasLost(Player currentPlayer) {
			return currentPlayer.getNumOfRemainingPieces() <= 2 || generatePossibleMoves(currentPlayer).size() == 0;
		}
		
		/**
		*Performance optimization of hasLost for usage inside the AlphaBeta Method
		*because the possibleMoves don't need to be calculated
		*/
		public boolean hasLost(Player currentPlayer, List<Move> possibleMoves) {
			return currentPlayer.getNumOfRemainingPieces() <= 2 || possibleMoves.size() == 0;
		}
		
}
