
import java.util.LinkedList;


/**
 *
 * @author Floor & Christian
 * Chooses board with best mobility evaluation
 *
 */
public class EMobility extends Evaluator {

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

		return value;
    }

}
