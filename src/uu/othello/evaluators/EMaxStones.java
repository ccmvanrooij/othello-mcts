package uu.othello.evaluators;

import uu.othello.Othello;

/**
 *
 * @author Floor & Christian
 * Chooses board with maximum number of stones of your color
 *
 */
public class EMaxStones extends Evaluator{

    @Override
    protected double evaluate(int[][] board) {
        int p1=0;
		int p2=0;
		int opponent = (color == Othello.black) ? Othello.white : Othello.black;

		for(int i=0; i<6; i++) {
			for(int j=0; j<6; j++) {
				if(board[i][j] == color ) { p1++; }
				else if(board[i][j] == opponent) { p2++; }
			}
		}
		return (p1-p2);
    }

}
