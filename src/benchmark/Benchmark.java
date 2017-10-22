package benchmark;
import mcts.MCTSSolver;
import parser.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;

import cache_performance.*;

public class Benchmark {
	
	private ArrayList<POMDP> testProblems;
	
	private void loadPOMDP() {
		this.testProblems = new ArrayList<POMDP>();
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway2.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/saci-s12-a6-z5.95.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/saci-s100-a10-z31.POMDP"));
		
	}
	
	public void benchmark(Solver solver) {
		Metrics metrics = new Metrics(solver);
		metrics.tic();
		solver.Solve();
		metrics.toc();
	}

	public ArrayList<POMDP> getTestProblems() {
		return testProblems;
	}

	public static void main(String[] args) {
		Benchmark benchmark = new Benchmark();
		benchmark.loadPOMDP();
		for (POMDP problem : benchmark.getTestProblems()) {
//			benchmark.benchmark(new MCTSSolver(problem));
			benchmark.benchmark(new VI(problem));
			benchmark.benchmark(new VI_FeasibleActions(problem));
			benchmark.benchmark(new VI_CachePerformance(problem));
		}
    }
}
