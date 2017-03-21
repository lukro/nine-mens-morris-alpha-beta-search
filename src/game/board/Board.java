package game.board;

/**
 * This class models the game board. It has available 
 * every possible mill indices and every position
 * @author Lukas
 *
 */
public class Board {
	//The indices used by the board:
	// 0        1        2
	//    3     4     5
	//       6  7  8
	// 9  10 11    12 13 14
	//       15 16 17
	//    18    19    20
	// 21       22       23
	private final Position[] pos;
	public static final int BOARD_SIZE = 24;
	public static final int[][] POSSIBLE_MILLS = {
			{0, 1, 2},
			{3, 4, 5},
			{6, 7, 8},
			{9, 10, 11},
			{12, 13, 14},
			{15, 16, 17},
			{18, 19, 20},
			{21, 22, 23},
			{0, 9, 21},
			{3, 10, 18},
			{6, 11, 15},
			{1, 4, 7 },
			{16, 19, 22},
			{8, 12, 17},
			{5, 13, 20},
			{2, 14, 23},
	};
	
	/**
	 * Constructor creating the positions array and initializing the board
	 */
	public Board() {
		pos = new Position[BOARD_SIZE];
		setupConnection();
	}
	
	/**
	 * Setup the connection of the positions on the board
	 */
	private void setupConnection() {
		//creating the position objects
		for(int i = 0; i < BOARD_SIZE; i++) {
			pos[i] = new Position(i);
		}
		//setup the adjacent positions for every position on the board
		pos[0].addAdjPos(pos[1],pos[9]);
		pos[1].addAdjPos(pos[0],pos[2],pos[4]);
		pos[2].addAdjPos(pos[1],pos[14]);
	    pos[3].addAdjPos(pos[4],pos[10]);
		pos[4].addAdjPos(pos[1],pos[3],pos[5],pos[7]);
		pos[5].addAdjPos(pos[4],pos[13]);
	    pos[6].addAdjPos(pos[7],pos[11]);
		pos[7].addAdjPos(pos[4],pos[6],pos[8]);
		pos[8].addAdjPos(pos[7],pos[12]);
		pos[9].addAdjPos(pos[0],pos[10],pos[21]);
		pos[10].addAdjPos(pos[3],pos[9],pos[11],pos[18]);
        pos[11].addAdjPos(pos[6],pos[10],pos[15]);
		pos[12].addAdjPos(pos[8],pos[13],pos[17]);
		pos[13].addAdjPos(pos[5],pos[12],pos[14],pos[20]);
		pos[14].addAdjPos(pos[2],pos[13],pos[23]);
		pos[15].addAdjPos(pos[11],pos[16]);
		pos[16].addAdjPos(pos[15],pos[17],pos[19]);
		pos[17].addAdjPos(pos[12],pos[16]);
		pos[18].addAdjPos(pos[10],pos[19]);
		pos[19].addAdjPos(pos[16],pos[18],pos[20],pos[22]);
		pos[20].addAdjPos(pos[13],pos[19]);
		pos[21].addAdjPos(pos[9],pos[22]);
		pos[22].addAdjPos(pos[19],pos[21],pos[23]);
		pos[23].addAdjPos(pos[14],pos[22]);
	}
		
	
	public Position getPos(int index) throws IllegalArgumentException {
		if(index > BOARD_SIZE) 
			throw new IllegalArgumentException();
		return pos[index];
	}
}
