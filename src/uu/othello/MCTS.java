package uu.othello;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import uu.othello.evaluators.EMaxStones;
import uu.othello.evaluators.EMobility;
import uu.othello.evaluators.ERandom;
import uu.othello.evaluators.Evaluator;


/**
 *
 * @author Floor & Christian
 * Plays a game according to the MCTS algorithm
 *
 */
public class MCTS {
	    
    /**
     * Turn off MCTS learning, only evaluation will be used
     * WARNING this must be on true at all times, only set to false when debugging
     */
    private boolean learn = true;

    private double weighting = 0;

	private Stack<String> path;
	private HashMap<String, Board> tree;
	
	public Othello game;
    public Evaluator opp;
    public Evaluator player;

	// Counters for administration of wins/loses
	private int simulatedGames, totalGames, opponentWin, playerWin;

    // color MCTS is playing
	private int color = Othello.black;

    // store boards to determine if game has ended (no moves possible for example)
	private String previousPlayer, previousOpponent;

    // store the amount of times the current parent is traversed
    private int previousNumTraversed = 1;

    // output
    private BufferedWriter output;


    /**
     * Run MCTS
     * @param args
     */
	public static void main(String[] args) {

		// MCTS; int size, int color, Evaluator player, Evaluator opp, int gamesPerUnit, int c, int numberOfRuns
        //new MCTS(6, Othello.black, new ERandom(), new EMobility(), 10, 1).runNumber(300);
		//new MCTS(6, Othello.black, new ERandom(), new EStability(), 500, 1).runNumber(100);
        //new MCTS(6, Othello.black, new ERandom(), new EMaxStones(), 500, 1).runNumber(100);
		//new MCTS(6, Othello.black, new EMaxStones(), new EStability(), 500, 1).runNumber(100);
		new MCTS(6, Othello.black, new EMaxStones(), new EMobility(), 10, 1).runNumber(300);
	}

    /**
     * Create new MCTS object
     * @param size size of the board, currently 6 and 8 are supported
     * @param color the color MCTS is playing, white or black
     * @param player the MCTS simulation evaluator
     * @param opp the opponent evaluator
     * @param gamesPerUnit the number of traininggames must be played in each iteration
     * @param c UCB c constant
     */
	public MCTS(int size, int color, Evaluator player, Evaluator opp, int gamesPerUnit, double c) {

        System.out.println("Start new game");

        this.simulatedGames = gamesPerUnit;
        this.weighting = c;
        
        if(color == Othello.black || color == Othello.white) {
            this.color = color;
        }

        // initialize empty vars
		this.tree = new HashMap<String, Board>(4096);
		this.game = new Othello(size,size);
        this.path = new Stack<String>();

        // initialize opponent (Random by default)
        this.opp = (opp == null) ? new ERandom() : opp;
        this.opp.init(game, (this.color == Othello.black) ? Othello.white : Othello.black);
        
        // initialize mcts evaluator (Random by default)
        this.player = (player == null) ? new ERandom() : player;
        this.player.init(game, this.color);

        if(this.learn) this.load("tree.txt");

        System.out.println(player.getClass().getName() + " vs " + opp.getClass().getName() +
        		", C=" + this.weighting + ", Games per Unit=" + this.simulatedGames);
        
        // output to file
        try {
            this.output = new BufferedWriter(new FileWriter(player.getClass().getName() + opp.getClass().getName() + this.weighting+".txt"));
            this.output.write(player.getClass().getName() + " vs " + opp.getClass().getName() +
            		", C=" + this.weighting+ ", Games per Unit=" + this.simulatedGames + System.getProperty("line.separator"));
        } catch (IOException e) {
            System.out.println("NO OUTPUT FILE");
        }
	}
	
	/**
     * run mcts for a certain amount of games, save the result
     * @param games number of iterations to run
     */	
	private void runNumber(int games) {
        int p = 0;
		int i = 0;
		totalGames = 0;
		while(i<games) {
			this.run();
			i++;
            int percent = (int) (100*((double)i/(double)games));
            if(percent > p) {
                System.out.println(percent+"%");
                p = percent;
            }
		}

        try {
            this.output.close();
        } catch (IOException ex) {}

        // save to text file
        if(this.learn) this.save("tree.txt");
	}
		
		
	/**
     * run mcts for a certain period and save the result
     * @param seconds number of seconds to run
     */	
	private void runTime(int seconds) {
		for (long stop = System.nanoTime()+TimeUnit.SECONDS.toNanos(seconds); stop>System.nanoTime(); ) {
			this.run();
		}
		// save to text file
        if(this.learn) this.save("tree.txt");
	}
	
	/**
     * run mcts for one iteration (training + testing)
     */
	private void run() {

        this.playerWin = 0;
        this.opponentWin = 0;
        
        // check testgames
        for(int i = 0; i < 100; i++) {

            this.path.removeAllElements();

            // fresh start
            String board = this.init();

            // play
            String opponentBoard = "";
            do {
                // get opponent move 
                opponentBoard = this.opp.makeMove(board);
                // make a move
                board = this.traverse(opponentBoard, false);

            } while (board != null);

            double result = determineWinner(opponentBoard);
            if(result == 1) this.playerWin++;
            else if(result == -1) this.opponentWin++;

        }

        try {
            this.output.write((playerWin)+System.getProperty("line.separator"));
        } catch (IOException ex) {
            System.out.println(playerWin);
        }

        if(learn ) {
        	// learn from trainings games
        
	        for(int i = 0; i < this.simulatedGames; i++) {
	        	
	        	totalGames++;
	
	            // fresh start
	            String board = this.init();
	
	            // play
	            String opponentBoard = "";
	            do {
	                // get opponent move
	                opponentBoard = this.opp.makeMove(board);
	                // make a move
	                board = this.traverse(opponentBoard, true);
	
	            } while (board != null);
	
	            backPropagate(determineWinner(opponentBoard));
	
	        }
        }
	}	

	/**
     * initialize the first game board
     * @return the first state of the game
     */
    public String init() {
        // create start board
		String board = this.game.createGameBoard();
        this.previousPlayer = "";
        this.previousOpponent = "";
        this.previousNumTraversed = 1;

        if(this.color == Othello.black) {
            // choose first move automatically
            LinkedList<int[][]> children = this.game.getBoards(this.color, board);
            board = game.toString(children.getFirst());
        }

		// add to traversed path
		this.path.push(board);

		// add to tree if it doesn't exist (only first time running MCTS)
		if (this.tree.get(board) == null) {
			Board start = new Board();
			start.increaseReward(0);
            this.tree.put(board, start);
		}

        return board;
    }

    /**
     * play a move based on mcts
     * @param board the move from opponent
     * @param l true if training game (calc UCB) false if test game (take numTraversed)
     * @return new game state with played move, null if game has ended
     */
	public String traverse (String board, boolean l) {

        // check for end of game (two equal consecutive moves from opponent)
        if(this.previousOpponent.equals(board)) return null;
        this.previousOpponent = board;

        String newBoard = board;

        // get all possible children
        LinkedList<int[][]> children = this.game.getBoards(this.color, board);
        if(children.isEmpty()) {
            
            // check if game is ended
            if(this.previousPlayer.equals(board)) {
                return null;
            }
            return newBoard;
        }

        // check if parent node was added in previous run (choose between UCB or Simulation)
        if(this.previousNumTraversed > 0 && this.learn) {

            // search in tree
            double ucb = Double.NEGATIVE_INFINITY;
            double u = Double.NEGATIVE_INFINITY;
            Board successor = null;
            int numTraversed = 0;

            for(int[][] ch : children) {

                String child = game.toString(ch);

                successor = this.tree.get(child);
                u = Double.NEGATIVE_INFINITY;

                if(l) {

                    // check if this child is stored in the tree
                    if(successor == null) {
                        // child isn't explored yet, give it a really high (+random) number
                        u = 10000 + Math.random();
                        newBoard = child;
                    } else {
                        // calculate UCB value
                         u = successor.ucb(this.weighting, this.totalGames);
                    }

                } else {

                    u = (successor != null) ? successor.getNumTraversed() : Double.NEGATIVE_INFINITY;

                }

                // choose current board if UCB is higher than previous
                if(u > ucb) {
                    ucb = u;
                    newBoard = child;
                    numTraversed = (successor == null) ? 0 : successor.getNumTraversed();
                }

            }

            if(l && u == Double.NEGATIVE_INFINITY) {
                newBoard = this.player.makeMove(board);
                numTraversed = 0;
            }
            
            // save chosen board to traversed path
            this.path.push(newBoard);
            this.previousNumTraversed = numTraversed;
            if(this.previousNumTraversed == 0) {
                this.tree.put(newBoard, new Board());
                //System.out.println("TRAVERSE add to tree: "+ newBoard);
            }

        } else {
            // simulate using heuristics
			newBoard = this.player.makeMove(board);
        }

        this.previousPlayer = newBoard;
		return newBoard;
	}

	
	/**
     * updates all traversed states in the hashmap
     * @param result the result of the played game, positive if won, negative otherwise
     */
	public void backPropagate(double result) {
		
		int counter = 0;
		while(!this.path.empty()) {

            // increase reward
            tree.get(this.path.pop()).increaseReward(result);
            counter++;

		}
		
	}

	/**
     * Determine reward after finished game; loss -1, draw 0, win 1
     * @param board the finished state of the game
     * @return the reward 1 for win, -1 for loss, 0 for draw
     */
    public double determineWinner(String board) {
    	
		int pointsPlayer = 0;
		int pointsOpponent = 0;
		for (int i=0; i<board.length(); i++) {
			if (Character.getNumericValue(board.charAt(i)) == Othello.black) {
				pointsPlayer++;
			} else if (Character.getNumericValue(board.charAt(i)) == Othello.white) {
				pointsOpponent++;
			}
		}
		
		//System.out.println("pointplayer: " + pointsPlayer + " en pointsopponent: " + pointsOpponent);
		if (pointsOpponent<pointsPlayer) { return 1; }
		else if (pointsPlayer<pointsOpponent) { return -1; }
		else { return 0; }
	}
    
    /**
     * saves current hashmap in memory to file
     * @param f file to save to
     */
	public void save(String f) {
		try{
			FileWriter fstream = new FileWriter(f);
	        BufferedWriter out = new BufferedWriter(fstream);

            // iterate over hashmap
            for (Map.Entry<String, Board> entry : this.tree.entrySet()) {
                Board b = entry.getValue();
                out.write(entry.getKey()+";"+b.getNumTraversed()+";"+b.getReward()+System.getProperty("line.separator"));
            }
			
			out.close();
			
		} catch (Exception ex){//Catch output exceptions
            System.err.println("Error: " + ex.getMessage());
        }

        System.out.println("Saved to "+f);

	}

    /**
     * loads the hashmap from file in memory
     * format: string key, numTraversed, reward
     * @param f file to read from
     */
	private void load(String f) {

        int i = 0;
		try{
			FileReader file = new FileReader(f);
		    Scanner outerScan = new Scanner(file);
		    
		    outerScan.useDelimiter(System.getProperty("line.separator"));
		    while (outerScan.hasNext()){
		    	
		    	Scanner innerScan = new Scanner(outerScan.next());	    
			    innerScan.useDelimiter(";");
	
			    String key = innerScan.next();
			    String numTraversed = innerScan.next();
			    String reward = innerScan.next();
			    
			    tree.put(key, new Board(Integer.parseInt(numTraversed), Double.parseDouble(reward)));
                i++;
		    }
		    file.close();
		}
		catch (Exception e) {
			System.out.println("Please check input file! Error: " + e.getMessage());
		}

        System.out.println("Loaded "+i+" game states");

	}	
}
