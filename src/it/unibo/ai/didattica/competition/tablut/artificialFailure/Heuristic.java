package it.unibo.ai.didattica.competition.tablut.artificialFailure;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public abstract class Heuristic {
	
	protected GameAshtonTablut game;
	protected State.Turn turn;
	protected State state;
	
	protected double[] weights;
	
	protected final static int TOT_BLACK = 16;
	protected final static int TOT_WHITE = 8;

	
	
	public Heuristic(State state) {
		super();
		this.state = state;
	}

	
	public double evaluateState() {
		return 1;
	}
	
	

	
	
	
	
}
