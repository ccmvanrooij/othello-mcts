package uu.othello.evaluators;

import java.util.LinkedList;

import uu.othello.Othello;

/**
 *
 * @author Floor & Christian
 * defines an evaluator to play othello
 */
public abstract class Evaluator {
    
    protected int color = Othello.white;
    protected Othello othello;

    // TODO randomness

    public Evaluator init(Othello oth, int color) {
        this.othello = oth;
        this.setColor(color);
        return this;
    }

    public void setColor(int color) {
        this.color = color;
    }

    /**
     * gets the best possible move according to this evaluator
     * @param board the board to send to evaluator
     * @param color the color this evaluator is playing
     * @return a new board after playing a move
     */
    public String makeMove(String board) {
        String boardId = board;

		LinkedList<int[][]> children = this.othello.getBoards(color, board);
		if(children.isEmpty()) {
			return boardId;
		}

		// Create LinkedList with multiple solutions
		LinkedList<int[][]> solutions = new LinkedList<int[][]>();

		double evaluate = Double.NEGATIVE_INFINITY;
		for(int[][] child : children) {
			double e = this.evaluate(child);

			if(e > evaluate) {
				evaluate = e;
				solutions.clear();
				solutions.add(child);
			} else if(e == evaluate) {
				solutions.add(child);
			}
		}
		boardId = this.othello.toString(solutions.get((int)(Math.random()*solutions.size())));
		return boardId;
    }
    
    /**
     * Evaluate board and return the result
     * @param board board to evaluate
     * @return double which represents the evaluation of the board (higher is better)
     */
    protected abstract double evaluate(int[][] board);

}
