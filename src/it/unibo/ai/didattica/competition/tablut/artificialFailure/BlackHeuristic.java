package it.unibo.ai.didattica.competition.tablut.artificialFailure;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class BlackHeuristic extends Heuristic{
	
	private final int TOTAL_WEIGHTS = 4;
	private final int WHITE_PAWNS = 0;
	private final int BLACK_NEAR_KING = 1;
	private final int BLACK_PAWNS = 2;
	private final int PAWN_ON_DIAMOND_SPOTS = 3;
	
	private double whiteOnBoard = 0; //linked with WHITE_PAWNS
	private double blackOnBoard = 0; // linked with BLACK_PAWNS
	private double blackNearKing = 0; //linked to BLACK_NEAR_KING
	private double blackOnDiamondSpots = 0; // linked to PAWN_ON_DIAMOND_SPOTS
	private int king_x = 0;
	private int king_y = 0;
	private int[][] diamondSpots = {{1, 2}, {2, 1}, {6, 1}, {7, 2}, {1, 6}, {2, 7}, {6, 7}, {7, 6}};
	
	
	
	
	

	public BlackHeuristic(State state) {
		super(state);
		this.weights = new double[TOTAL_WEIGHTS];		
	}
	
	
	@Override
	public double evaluateState() {
		//check black win
		if (state.getTurn().equalsTurn(Turn.BLACKWIN.toString())) {
			return Double.NEGATIVE_INFINITY;
		} else if(state.getTurn().equalsTurn(Turn.WHITEWIN.toString())) {
			return Double.POSITIVE_INFINITY;
		}
		
		double res = 0;
		
		this.setWeights();
		
		this.getValues(state);
		
		//evaluate the final result
		res += (TOT_WHITE - whiteOnBoard)/TOT_WHITE * weights[WHITE_PAWNS];
		res += (blackOnBoard)/TOT_BLACK * weights[BLACK_PAWNS];
		res += weights[BLACK_NEAR_KING];
		res += (8 - blackOnDiamondSpots)/8 * weights[PAWN_ON_DIAMOND_SPOTS];
			
		return res;
	}
	
	public void getValues(State state) {
		for (int i = 0; i < state.getBoard().length; i++) {
			for (int j = 0; j < state.getBoard().length; j++) {
				checkBlackPawn(i,j);
				checkWhitePawn(i,j);
				checkKing(i,j);
				
			}
		}
	}
	
	/**
	 * Checks how much black pawns are on spots to complete the diamond tactics
	 */
	public void checkDiamondSpots() {
	    for(int[] position : diamondSpots) {
	        if(this.state.getPawn(position[0], position[1]).equals(State.Pawn.BLACK))
	            this.blackOnDiamondSpots++;
	    }
    }
	
	/**
	* Check the king position. Value modified: king_x, king_y
	* @param i row
	* @param j column
	*/
	public void checkKing(int i, int j) {
		if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
			king_x = i;
			king_y = j;
			checkNearKing(king_x, king_y);
		}
	}
	
	/**
	* Check how many black pawns surround the king. Structures creating walls are considered as black pawns.
	* @param i row
	* @param j column
	*/
	public void checkNearKing(int r, int c) {
		if(state.getPawn(king_x-1, king_y).equalsPawn(State.Pawn.BLACK.toString()) ||
				(king_x-1 == 0 && king_y == 3) || (king_x-1 == 0 && king_y == 5) || (king_x-1 == 1 && king_y == 4) ||
				(king_x-1 == 4 && king_y == 1) || (king_x-1 == 4 && king_y == 7)) {//up
			this.blackNearKing++;
		}
		if(state.getPawn(king_x, king_y+1).equalsPawn(State.Pawn.BLACK.toString()) ||
				(king_x == 1 && king_y+1 == 4) || (king_x == 8 && king_y+1 == 4) || (king_x == 3 && king_y+1 == 8) ||
				(king_x == 5 && king_y+1 == 8) || (king_x == 4 && king_y+1 == 7)) {//right
			this.blackNearKing++;
		}
		if(state.getPawn(king_x+1, king_y).equalsPawn(State.Pawn.BLACK.toString()) ||
				(king_x+1 == 8 && king_y == 3) || (king_x+1 == 8 && king_y == 5) || (king_x+1 == 7 && king_y == 4) ||
				(king_x+1 == 4 && king_y == 1) || (king_x+1 == 4 && king_y == 7)) {//down
			this.blackNearKing++;
		}
		if(state.getPawn(king_x, king_y-1).equalsPawn(State.Pawn.BLACK.toString()) ||
				(king_x == 1 && king_y-1 == 4) || (king_x == 7 && king_y-1 == 4) || (king_x == 3 && king_y-1 == 0) ||
				(king_x == 5 && king_y-1 == 0) || (king_x == 4 && king_y-1 == 1)) {//left
			this.blackNearKing++;
		}
	}
	
	/**
	 * Value modified: whiteOnBoard
	 * @param i row
	 * @param j column
	 */
	public void checkWhitePawn(int i, int j) {
		if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
				/*|| state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())*/) {
			whiteOnBoard++;
		}
	}
	
	/**
	* Value modified: blackOnBoard
	* @param i row
	* @param j column
	*/
	public void checkBlackPawn(int i, int j) {
		if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
			blackOnBoard++;
		}
	}
	
	
	/**
	 * Check if enemy has got diamond tactics. Diamond tactics: when black occupies the ways to escape.
	 */
	
	/*
	public boolean checkDiamondTactics(){
		return state.getPawn(1,2).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(2,1).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(1,6).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(2,7).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(6,1).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(7,2).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(6,7).equalsPawn(State.Pawn.BLACK.toString()) &&
				state.getPawn(7,6).equalsPawn(State.Pawn.BLACK.toString());
	}
	*/
	
	/**
	 * Set the weight to give priority to get in the diamond scheme
	 */
	public void setWeights() {
		weights[WHITE_PAWNS] = 20;
		weights[BLACK_NEAR_KING] = 5;
		weights[BLACK_PAWNS] = 5;
		weights[PAWN_ON_DIAMOND_SPOTS] = 40;
	}
	
	/**
	 * Set the weight to give priority to eat white 
	 */
	/*
	public void aggressiveAttackTactics() {
		weights[WHITE_PAWNS] = 30;
		weights[BLACK_NEAR_KING] = 20;
		weights[BLACK_PAWNS] = 10;
		weights[PAWN_ON_DIAMOND_SPOTS] = 30;
	}
	*/

}
