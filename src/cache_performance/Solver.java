package cache_performance;


import benchmark.MemoryUsage;
import parser.*;

public class Solver {
	protected double[][] qTable;
	protected double[][] qTablePrev;
	protected POMDP mdp;
	protected MemoryUsage mu;
	protected String solverName;
	
	public Solver() {
		mu = MemoryUsage.getInstance();
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

	public String getSolverName() {
		return this.solverName;
	}

	public String getProblemName() {
	    return this.mdp.getName();
	}

	public void Solve() {}
}
