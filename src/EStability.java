
/**
 *
 * @author Floor & Christian
 * Chooses board with best stability evaluation
 *
 */
public class EStability extends Evaluator{


	protected static final int[][] wf = new int[][]
	                                	        {{ 100,-10,  5,  5,-10, 100},
	                                		     { -10,-10,  0,  0,-10, -10},
	                                		     {   5,  0,  0,  0,  0,   5},
	                                		     {   5,  0,  0,  0,  0,   5},
	                                		     { -10,-10,  0,  0,-10, -10},
	                                		     { 100,-10,  5,  5,-10, 100}};

    @Override
    protected double evaluate(int[][] board) {

		double value = 0;

		// stability
		for(int i=0; i<this.othello.rows; i++) {
			for(int j=0; j<this.othello.cols; j++) {
				if(board[i][j] == color) {
					value += wf[i][j];
				} else {
					value -= wf[i][j];
				}
			}
		}
		//System.out.println("Evaluatiewaarde: " + value);
		return value;
    }

}
