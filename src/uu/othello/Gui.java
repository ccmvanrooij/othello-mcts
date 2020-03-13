package uu.othello;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import uu.othello.evaluators.ERandom;


public class Gui extends Frame implements ActionListener {

    MCTS mcts;
    int[][] state;
    BoardGraph bg;
    int numMoves;
    boolean gameOver;

    /* BUTTONS  */
    Button bStep;
    Button bSkip;
    Button bNew;
    Button bSave;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
	
	private Gui() {
        super("MCTS OTHELLO");

        this.setLayout(new BorderLayout(5,5));
	    this.setResizable(false);

        // close window with close button
        this.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override
            public void windowClosing(java.awt.event.WindowEvent we){
                System.exit(0);
            }
        });
        this.setBackground(new Color(220,220,220));

        // create the game board
        this.bg = new BoardGraph(this, 6, 6);
        this.add(bg, BorderLayout.CENTER);
        bg.addMouseListener(bg);

        // sample game
        this.mcts = new MCTS(6, Othello.black, new ERandom(), new ERandom(), 100, 1);

        // create control panel with buttons
        Panel control = new Panel();

        bNew = new Button("new");
        bSkip = new Button("skip");
        bStep = new Button("step");
        bSave = new Button("save");
        
        bStep.addActionListener(this);
        bSkip.addActionListener(this);
        bNew.addActionListener(this);
        bSave.addActionListener(this);

        // add control panel and buttons to frame
        control.add(bNew);
        control.add(bStep);
        control.add(bSkip);
        control.add(bSave);
        this.add(control, BorderLayout.NORTH);

        this.reset();

        // display game
        this.pack();
	    this.setVisible(true);
		
    }

    public void gameOver() {
        System.out.println("Game Over");
        this.gameOver = true;
        this.bSkip.setEnabled(false);
        this.bStep.setEnabled(false);
        this.bSave.setEnabled(true);

        this.mcts.backPropagate(mcts.determineWinner(mcts.game.toString(this.state)));
    }

    /**
     * called when user clicks on a field
     * @param p the field that is clicked (x (cols) ,y (rows)) 
     */
    public void clicked(Point p) {

        if(!gameOver) {
            // check whose turn it is
            if(!bStep.isEnabled()) {
                int[][] newboard = mcts.game.makeMove(Othello.white, p.y, p.x, this.state);
                if(newboard != null) {
                    this.state = newboard;
                    this.bg.update(state);
                    this.bStep.setEnabled(true);
                    this.numMoves = -1;
                    this.bSkip.setEnabled(false);
                } 
            } else {
                mcts();
            }
        }

    }

    private void reset() {
        this.bSkip.setEnabled(false);
        this.bStep.setEnabled(false);
        this.bSave.setEnabled(false);
        this.gameOver = false;
        this.numMoves = -1;
        this.state = this.mcts.game.toArray(mcts.init());
        bg.update(this.state);
    }

    private void mcts() {

        if(!gameOver) {
            this.bSkip.setEnabled(false);

            // calc new successor
            String successor = this.mcts.traverse(this.mcts.game.toString(state), true);

            if(successor != null) {
                this.state = this.mcts.game.toArray(successor);
                this.bg.update(state);
                this.bStep.setEnabled(false);
            } else {
                // game finished
                this.gameOver();

            }

            // check num of valid moves
            if(this.numMoves == -1) {
                this.numMoves = this.mcts.game.getBoards(Othello.white, this.state).size();
            }

            // set skip button enabled if no moves are available
            if(this.numMoves == 0) this.bSkip.setEnabled(true);

        }


    }

	@Override
	public void actionPerformed(ActionEvent ae) {
        String b = ae.getActionCommand();
        if(b.equals("new")) {
            this.reset();
        } else if(b.equals("step") || b.equals("skip")) {
            this.mcts();
        } else if(b.equals("save")) {
            this.mcts.save("tree.txt");
        }
		
	}
	

}
/*
 * Graph class
 */
class BoardGraph extends Canvas implements MouseListener {

    private int[][] gameState;

    Gui mainGui;
    int numRows, numCols;
    int fieldDimension = 50;

    public BoardGraph(Gui g, int rows, int cols) {
        this.numRows = rows;
        this.numCols = cols;
        this.mainGui = g;
        this.setSize(rows*fieldDimension+1,cols*fieldDimension+21);
    }

    public void update(String gameState) {
        this.update(mainGui.mcts.game.toArray(gameState));
    }

    public void update(int[][] gameState) {
        this.gameState = gameState;
        this.repaint();
    }


    @Override
    public void paint(Graphics g) {

        double stoneScale = 0.75;
        int offsetY = 20;

        //draw field outlines
		g.setColor(Color.lightGray);
        // vertical lines
		for(int x=0; x<=numRows*fieldDimension; x+=fieldDimension) g.drawLine(x, offsetY, x, numRows*fieldDimension+offsetY);
        // horizontal lines
		for(int y=offsetY; y<=numCols*fieldDimension+offsetY; y+=fieldDimension) g.drawLine(0, y, numCols*fieldDimension, y);

        int black = 0;
        int white = 0;

        // draw gamestate
        if(this.gameState != null) {

            for(int x=0; x<numRows; x++) {
                for(int y=0; y<numRows; y++) {

                    if(gameState[x][y] != Othello.empty) {

                        // determine stone color
                        if(gameState[x][y] == Othello.black) {
                            g.setColor(new Color(70,70,70));
                            black++;
                        } else {
                            g.setColor(new Color(253,253,253));
                            white ++;
                        }

                        // draw stone
                        g.fillOval(y*fieldDimension + (int) ((1-stoneScale)/2*fieldDimension), x*fieldDimension + (int) ((1-stoneScale)/2*fieldDimension) + offsetY, (int) (stoneScale*fieldDimension), (int) (stoneScale*fieldDimension));
                    }

                }
            }

        }

        // draw number of stones
        g.setColor(new Color(70,70,70));
        g.drawString("black "+black+"  white "+white, 5, 15);

        // show status
        String status = "";
        if(black+white == mainGui.mcts.game.cols*mainGui.mcts.game.cols) {

            if(black > white) status = "* black";
            else if(white > black) status = "* white";
            else status = "* draw";
            mainGui.gameOver();

        } else {
            status = mainGui.bStep.isEnabled() ? "> black" : "> white";

        }

        g.drawString(status, 250, 15);

        
        
    }

    // Mouse listeners

    public void mouseClicked(MouseEvent me) {}
    public void mousePressed(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {
        if(me.getY() > 20) {
            int x = Math.max(0, Math.min(numCols, (int) me.getX()/fieldDimension));
            int y = Math.max(0, Math.min(numRows, (int) (me.getY()-20)/fieldDimension));
            mainGui.clicked(new Point(x,y));
        }
    }



}
