package it.unibo.ai.didattica.competition.tablut.artificialFailure;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class WhiteHeuristic extends Heuristic{
	
	//weights
	private final int TOTAL_WEIGHTS = 9;
	private final int WHITE_PAWNS = 0;
	private final int BLACK_NEAR_KING = 1;
	private final int KING_NEAR_TOWER = 2;
	private final int WHITE_IN_DANGER = 3;
	private final int KING_IN_TOWER = 4;
	private final int KING_ON_STAR = 5;
	private final int HOPE_ACTIONS_TO_WIN = 6;
	private final int BLACK_PAWNS = 7;
	private final int BLACK_IN_DANGER = 8;
	
	//variables related to weights
	private double whiteOnBoard = 0; //linked with WHITE_PAWNS
	private double blackOnBoard = 0; // linked with BLACK_PAWNS
	private int king_x = 0;
	private int king_y = 0;
	private double blackNearKing = 0; //linked to BLACK_NEAR_KING
	private double kingNearTower = 0; //linked to KING_NEAR_TOWER; 1 if true, 0 if false
	private double whiteInDanger = 0; //linked to WHITE_IN_DANGER
	private double kingInTower = 0; //linked to KING_IN_TOWER; 1 if true, 0 if false
	private double kingOnStar = 0; //linked to KING_ON_STAR; 1 if true, 0 if false
	private double hopeActionsToWin = 0; //linked to HOPE_ACTIONS_TO_WIN
	private double blackInDanger = 0; //linked to BLACK_IN_DANGER


	public WhiteHeuristic(State state) {
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
		res += whiteOnBoard/TOT_WHITE * weights[WHITE_PAWNS];
		res += blackOnBoard/TOT_BLACK * weights[BLACK_PAWNS];
		res -= blackNearKing/4 * weights[BLACK_NEAR_KING];
		res += kingNearTower * weights[KING_NEAR_TOWER];
		res += (TOT_WHITE - whiteInDanger)/TOT_WHITE* weights[WHITE_IN_DANGER];
		res += kingInTower * weights[KING_IN_TOWER];
		res += (4 - hopeActionsToWin) * weights[HOPE_ACTIONS_TO_WIN];
		res += blackInDanger/TOT_BLACK * weights[BLACK_IN_DANGER];
		res += kingOnStar * weights[KING_ON_STAR];
		
		return res;
	}
	
	/**
	 * Set the variable related to weights values
	 * @param state
	 */
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
	* Value modified: blackNearKing, kingInTower, kingNearTower,
	* @param i row
	* @param j column
	*/
	public void checkKing(int i, int j) {
		if (state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
			king_x = i;
			king_y = j;
			
			//check black or structures near king
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
			
			//check if king is in throne
			if(king_x == 4 && king_y == 4)
				this.kingInTower=1;
			//else if near tower
			else if((king_x == 3 && king_y == 4) || (king_x == 4 && king_y == 5) || (king_x == 5 && king_y == 4) ||
					(king_x == 4 && king_y == 3)){
				kingNearTower++;
			}
			
			//check how much way to escape there are
			wayToEscape(i,j);
			checkKingOnStar(i,j);
		}
	}
	
	public void checkKingOnStar(int r, int c) {
		if((king_x==0 && king_y==1) || (king_x==0 && king_y==2) || (king_x==0 && king_y==6) || (king_x==0 && king_y==7) ||
			(king_x==8 && king_y==1) || (king_x==8 && king_y==2) || (king_x==8 && king_y==6) || (king_x==8 && king_y==7) ||
			(king_x==1 && king_y==0) || (king_x==2 && king_y==0) || (king_x==6 && king_y==0) || (king_x==7 && king_y==0) ||
			(king_x==1 && king_y==8) || (king_x==2 && king_y==8) || (king_x==6 && king_y==8) || (king_x==7 && king_y==8)) {
			kingOnStar++;
		}
	}
	
	/**
	 * Checks if there are free way to escape
	 * @param r row
	 * @param c column
	 */
	public void wayToEscape(int r, int c) {
		if(c==1 || c==2 || c==6 || c==7) { //check only if the king is on the correct columns
			for(int i=r+1; i<=8; i++) { //look down
				if(i == 8) {
					hopeActionsToWin++;
					break;
				}
				if(occupied(i,c))
					break;
			}
			for(int i=r-1; i>=0; i--) { //look up
				if(i == 0) {
					hopeActionsToWin++;
					break;
				}
				if(occupied(i,c))
					break;
			}
		}
		if(r==1 || r==2 || r==6 || r==7) {//check only if the king is on the correct columns
			for(int j=c+1; j<=8; j++) { //look down
				if(j == 8) {
					hopeActionsToWin++;
					break;
				}
				if(occupied(r,j))
					break;
			}
			for(int j=c-1; j>=0; j--) { //look up
				if(j == 0) {
					hopeActionsToWin++;
					break;
				}
				if(occupied(r,j))
					break;
			}
		}
	}
	
	/**
	 * Check the position given
	 * @param r
	 * @param c
	 * @return true if the position given is occupied, false otherwise
	 */
	public boolean occupied(int r, int c) {
		return state.getPawn(r, c).equalsPawn(State.Pawn.BLACK.toString()) ||
				state.getPawn(r, c).equalsPawn(State.Pawn.WHITE.toString());
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
			isInDanger(State.Pawn.BLACK.toString(),i,j);
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
			isInDanger(State.Pawn.BLACK.toString(),i,j);
		}
	}
	
	public void isInDanger(String color, int r, int c) {
		if(color.equals(State.Pawn.BLACK.toString())) {
			if((r>0 && r<8) && (c>0 && c<8) && (c!=4 && r!=1) && (c!=4 && r!=7) &&
				(c!=1 && r!=4) && (c!=7 && r!=4)) {
				if(state.getPawn(r-1,c).equalsPawn(State.Pawn.WHITE.toString()) || state.getPawn(r+1,c).equalsPawn(State.Pawn.WHITE.toString()) ||
					state.getPawn(r,c+1).equalsPawn(State.Pawn.WHITE.toString()) || state.getPawn(r,c-1).equalsPawn(State.Pawn.WHITE.toString())) {
					blackInDanger++;
				}
			}
		}else {
			if(state.getPawn(r-1,c).equalsPawn(State.Pawn.BLACK.toString()) || state.getPawn(r+1,c).equalsPawn(State.Pawn.BLACK.toString()) ||
					state.getPawn(r,c+1).equalsPawn(State.Pawn.BLACK.toString()) || state.getPawn(r,c-1).equalsPawn(State.Pawn.BLACK.toString())) {
					whiteInDanger++;
				}
		}
	}
	
	/**
	 * Setting weights
	 */
	public void setWeights() {
		weights[WHITE_PAWNS] = 15;
		weights[BLACK_NEAR_KING] = 5;
		weights[KING_NEAR_TOWER] = 2;
		weights[WHITE_IN_DANGER] = 15;
		weights[KING_IN_TOWER] = 2;
		weights[KING_ON_STAR] = 15;
		weights[HOPE_ACTIONS_TO_WIN] = 15;
		weights[BLACK_PAWNS] = 20;
		weights[BLACK_IN_DANGER] = 15;
	}
	
	
	/**
	 * Check if enemy has got diamond tactics. Diamond tactics: when black occupies the ways to escape.
	 */
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

}
