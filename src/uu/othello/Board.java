package uu.othello;

public class Board {
	private int n = 0;
	private double ucb, v = 0;
	private boolean changed = true;
	
	public Board (int n, double v) {
		this.n = n;
		this.v = v;
	}
	
	public Board() {}

    /**
     * Calculate UCB value
     * @param c Constant changes proportion Reward/NumTraversed
     * @param totalCounter Amount of times parent is traversed
     * @return ucb value
     */
	public double ucb(double c, int totalCounter) {
		if(this.changed) {
			this.ucb = v+c*(Math.sqrt(Math.log(totalCounter)/n));
			this.changed = false;
		}
		return this.ucb;
		
	}

    /**
     * Increase the reward of this board by v and increase traversed counter with 1
     * @param v the value to add to the total reward (can be negative)
     */
	public void increaseReward(double v) {
		this.v += v;
		this.n++;
		this.changed = true;
	}
	
    /**
     * get the number of times this board was traversed
     * @return the number of times this board was traversed
     */
	public int getNumTraversed() {
		return this.n;
	}
	
	public double getReward() {
		return this.v;
	}
	
	
}
