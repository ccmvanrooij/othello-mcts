import java.util.LinkedList;

public class Othello {

	public int rows, cols;
	public static final int black = 1;
	public static final int white = 2;
	public static final int empty = 0;
	
	public Othello(int rows, int cols) {
		this.cols = cols;
		this.rows = rows;
	}

    /**
     * creates new gameBoard
     * @return starting state in string format
     */
	public String createGameBoard(){

        return "000000000000001200002100000000000000";
		
	}

    /**
     * creates new gameBoard
     * @return starting state in array format
     */
    public int[][] createGameBoardArray() {
        int[][] board = new int[rows][cols];

		for(int i=0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				board[i][j] = empty;
			}
		}
		board[rows/2-1][cols/2-1] = black;
		board[rows/2][cols/2] = black;
		board[rows/2][cols/2-1] = white;
		board[rows/2-1][cols/2] = white;
	

        return board;
    }
	
	/**
     * Get possible boards from given board in String format
     * @param color color of current player
     * @param board board to generate children with
     * @return LinkedList with all possible boards in Strings, can be empty if none available
     */
	public LinkedList<int[][]> getBoards(int color, String board) {
		
		int[][] boardArr = this.toArray(board);
		LinkedList<int[][]> boards = this.getBoards(color, boardArr);
		
		return boards;
	}

    /**
     * Get possible boards from given board in Array format
     * @param color color of current player
     * @param game board to generate children for
     * @return LinkedList with all possible boards in Array, can be empty if none available
     */
	public LinkedList<int[][]> getBoards(int color, int[][] game) {
		
		LinkedList<int[][]> successors = new LinkedList<int[][]>();
		
		for(int r=0; r<rows; r++) {
			for(int c=0; c<cols; c++) {

                if(game[r][c] == empty) {

                    int[][] successor = this.makeMove(color, r, c, game);
                    if(successor != null) successors.add(successor);
                }
				
			}
		}
		
		return successors;
		
	}


    /**
     * Check if a move on tile r,c is possible and return the result
     * @param color your color
     * @param r row number of tile to make your move
     * @param c col number of tile to make your move
     * @param game 2-dimensional array with board state
     * @return a new game state after the given move is made, null if move is not possible
     */
	public int[][] makeMove (int color, int r, int c, int[][] game) {

        // check if field is empty
        if(game[r][c] != empty) return null;

        int colorOpponent = (color == black) ? white : black;
        boolean valid = false;

        // copy old board
        int[][] newGame = new int[game.length][game[0].length];
        for(int i = 0; i < cols; i++) {
            newGame[i] = game[i].clone();
        }

        // check surrounding square for opponent
		for(int dr=-1; dr<=1; dr++) {

			// check if new position is within board boundaries (rows)
			if(r+dr >= 0 && r+dr < rows) {

				for(int dc=-1; dc<=1; dc++) {

					// check if new position is within board boundaries (cols)
					if(c+dc >= 0 && c+dc < cols) {

						// skip center position
						if(dr!=0 || dc!=0) {

							if(game[r+dr][c+dc] == colorOpponent){
								valid = paintDirection(color, r, c, dr, dc, game, newGame) || valid;
							}
						}
					}

				}
			}
		}


        // return new game state changed, null otherwise
        if(valid) return newGame;
        return null;
	}


    /**
	 *
	 * @param color your color
	 * @param r row number to start search
	 * @param c col number to start search
	 * @param dr row-direction to search for empty tile
	 * @param dc col-direction to search for empty tile
	 * @param game initial game state
	 * @param newGame game after painting the new tiles (this var will be modified)
	 * @return true if direction is painted, false otherwise
	 */
	private boolean paintDirection(int color, int r, int c, int dr, int dc, int[][] game, int[][] newGame) {

		int rNew = r+dr;
		int cNew = c+dc;

		// continue to search until boundary is hit or tile is your own
		while(rNew >= 0 && rNew < rows && cNew >= 0 && cNew < cols && game[rNew][cNew] != empty) {

			if(game[rNew][cNew] == color) {

				do {
                    // walk back in reversed direction to origin, paint all tiles on the road
					rNew = rNew-dr;
					cNew = cNew-dc;

                    // change color
					newGame[rNew][cNew] = color;

					// continue until starting point
				} while(rNew != r || cNew != c);

				return true;

			}

            // walk in direction of found opponent tile
			rNew = rNew+dr;
			cNew = cNew+dc;

		}

        // not possible
		return false;

	}
	
	
	/*
	 * HELPER FUNCTIONS
	 */
	
	public String toString(int[][] board){
		String output = "";
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {	
				output += board[i][j];
			}
		}
		
		return output;
	}
	
	public int[][] toArray (String board){
		
		int[][] output = new int[this.rows][this.cols];
		
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				output[i][j] = Character.getNumericValue(board.charAt(i*this.rows+j));
			}
		}
		
		return output;
		
	}

}
