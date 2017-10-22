package cache_performance;


import benchmark.MemoryUsuage;
import parser.*;

public class Solver {
	protected double[][] qTable;
	protected double[][] qTablePrev;
	protected POMDP mdp;
	protected MemoryUsuage mu;
	
	public Solver() {
		mu = MemoryUsuage.getInstance();
	}
	
	public Solver(POMDP mdp) {
		this.mdp = mdp;
	}
	
	public void addMDP(POMDP mdp) {
		this.mdp = mdp;
	}
	
	public void initializeQTable() {
		this.qTable = new double[this.mdp.getNumStates()][this.mdp.getNumActions()];
		this.qTablePrev = new double[this.mdp.getNumStates()][this.mdp.getNumActions()];
	}


	
}
