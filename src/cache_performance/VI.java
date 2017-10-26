package cache_performance;
import benchmark.Metrics;
import parser.*;

public class VI extends Solver {
	private static final long MEGABYTE = 1024L * 1024L;

	public static long bytesToMegabytes(long bytes) {
		return bytes / MEGABYTE;
	}

	public VI() {
		
	}
	
	public VI(POMDP mdp) {
		super(mdp);
		this.solverName = "Value Iteration";
	}
	
	public void Solve() {
		initializeQTable();
		Runtime runtime = Runtime.getRuntime();

		long memory = runtime.totalMemory() - runtime.freeMemory();
		System.out.println("Used memory is megabytes: "
				+ bytesToMegabytes(memory));
		// Make assert that discount factor has to be between 0-1
		int sNext,s,a;
		double sum;
		double delta = 1;
		int count = 0;
		while(delta > 0.0001) {
			delta = 0;
			count++;
			for(s = 0; s < this.mdp.getNumStates(); s++) {
				for(a = 0; a < this.mdp.getNumActions(); a++) {
					sum = 0.0;

					for(sNext = 0; sNext < this.mdp.getNumStates(); sNext++) {
						sum = sum + this.mdp.getTransitionProbability(s, a, sNext)*getMaxQTablePrev(sNext);
					}
					this.qTable[s][a] = this.mdp.getReward(s, a) + this.mdp.getDiscountFactor()*sum;
					delta = getDelta(delta, s, a);
				}
			}
			saveCurrentQMatrix();

		}
		//printQTable();
//		mu.recordMemoryUsuage();
//		System.out.format("The amount of cycles was: %d%n", count);
	}
	
	private double getMaxQTablePrev(int s) {
		double max = 0;

		for(int i = 0; i < this.mdp.getNumActions(); i++) {
			if(max < qTablePrev[s][i]) {
				max = qTablePrev[s][i];
			}
		}
		return max;
	}
	
	private void printQTable() {
		int s,a;
		
		for( s=0; s<this.mdp.getNumStates(); s++) {
			for( a=0; a<this.mdp.getNumActions(); a++) {
				System.out.format("%06.3f  ", qTable[s][a]);
			}
			System.out.format("%n");
		}
		System.out.format("%n%n%n");
		
	}
	
	private void saveCurrentQMatrix() {
		for(int s = 0; s < this.mdp.getNumStates(); s++) {
			for(int a = 0; a < this.mdp.getNumActions(); a++) {
				this.qTablePrev[s][a] = this.qTable[s][a];
			}
		}
	}
	
	private double getDelta(double delta, int s, int a) {
		double d;	
		d = Math.abs(this.qTable[s][a]) - Math.abs(this.qTablePrev[s][a]);
		//System.out.println("delta: " + d);
		if(delta < d) {
			return d;
		}
		return delta;
	}
	
}
