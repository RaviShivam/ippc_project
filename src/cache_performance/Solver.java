package cache_performance;


import benchmark.Logger;
import benchmark.MemoryUsage;
import parser.*;

public class Solver {
	protected double[][] qTable;
	protected double[][] qTablePrev;
	protected POMDP mdp;
	protected MemoryUsage mu;
	protected String solverName;
	protected Logger log;
	protected final double RECORDTIME = 0.02;
	
	public Solver() {
		mu = MemoryUsage.getInstance();
	}
	
	public Solver(POMDP mdp) {
		this.mdp = mdp;
		this.log = new Logger();
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

	protected double maxReward() {
		double max = 0;

		for (int i = 0; i < this.mdp.getNumStates(); i++) {
			for (int j = 0; j < this.mdp.getNumActions(); j++) {
				if(max < this.qTable[i][j]) {
					max = this.qTable[i][j];
				}
			}
		}

		return max;
	}

	private static final long MEGABYTE = 1024L * 1024L;

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}
}
