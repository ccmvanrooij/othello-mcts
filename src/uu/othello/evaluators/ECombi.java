package uu.othello.evaluators;

import java.util.LinkedList;

import uu.othello.Othello;

/**
 *
 * @author Floor & Christian
 * Chooses board with both stability evaluation and mobility evaluation
 *
 */
public class ECombi extends EStability {

    @Override
    protected double evaluate(int[][] board) {
        // mobility
		LinkedList<int[][]> children = this.othello.getBoards(Othello.white, board);
		int whiteMoves = children.size();
		children = this.othello.getBoards(Othello.black, board);
		int blackMoves = children.size();

		double value = 0;

		if(color == Othello.black) {
			value = blackMoves - whiteMoves;
		} else {
			value = whiteMoves - blackMoves;
		}

        // add stability value
		return value + super.evaluate(board);
    }

}
