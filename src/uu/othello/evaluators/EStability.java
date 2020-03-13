package uu.othello.evaluators;

/**
 *
 * @author Floor & Christian
 * Chooses board with best stability evaluation
 *
 */
public class EStability extends Evaluator{

	protected static final int[][] wf80 = new int[][]
	                                	        {{ 100,-20, 10, 10, 10, 10,-20,100},
	                                		     { -20,-20,  0,  0,  0,  0,-20,-20},
	                                		     {  10,  0,  0,  0,  0,  0,  0, 10},
	                                		     {  10,  0,  0,  0,  0,  0,  0, 10},
	                                		     {  10,  0,  0,  0,  0,  0,  0, 10},
	                                		     {  10,  0,  0,  0,  0,  0,  0, 10},
	                                		     { -20,-20,  0,  0,  0,  0,-20,-20},
	                                		     { 100,-20, 10, 10, 10, 10,-20,100}};

	protected static final int[][] wf81 = new int[][]
		                                	        {{ 100,0, 0, 0, 0, 0,0,100},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     {  0,  0,  0,  0,  0,  0,  0, 0},
		                                		     { 100,0, 0, 0, 0, 0,0,100}};

	protected static final int[][] wf60 = new int[][]
	                                	        {{ 100,-10,  5,  5,-10, 100},
	                                		     { -10,-10,  0,  0,-10, -10},
	                                		     {   5,  0,  0,  0,  0,   5},
	                                		     {   5,  0,  0,  0,  0,   5},
	                                		     { -10,-10,  0,  0,-10, -10},
	                                		     { 100,-10,  5,  5,-10, 100}};

	protected static final int[][] wf61 = new int[][]
	                                	        {{ 100,0,  0,  0,0, 100},
	                                		     { 0,0, 0, 0,0, 0},
	                                		     {   0, 0,  0,  0, 0,  0},
	                                		     {   0, 0,  0,  0, 0,   0},
	                                		     { 0,0, 0,0,0, 0},
	                                		     { 100,0,  0,  0,00, 0}};

    @Override
    protected double evaluate(int[][] board) {

		double value = 0;

		// stability
		for(int i=0; i<this.othello.rows; i++) {
			for(int j=0; j<this.othello.cols; j++) {
				if(board[i][j] == color) {
					value += wf60[i][j];
				} else {
					value -= wf60[i][j];
				}
			}
		}
		//System.out.println("Evaluatiewaarde: " + value);
		return value;
    }

}
