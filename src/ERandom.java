
import java.util.LinkedList;

/**
 *
 * @author Floor & Christian
 * Makes random moves
 *
 */
public class ERandom extends Evaluator{

    @Override
    public String makeMove(String board) {
        
        // get all possible children
		LinkedList<int[][]> children = this.othello.getBoards(this.color, board);
		if(children.isEmpty()) {
			return board;
		}

        // pick random move
        return this.othello.toString(children.get((int)(Math.random()*children.size())));

    }

    @Override
    protected double evaluate(int[][] board) {
        return 0;
    }

}
