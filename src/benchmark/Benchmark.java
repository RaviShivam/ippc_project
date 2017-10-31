package benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import cache_performance.*;
import mcts.MCTSSolver;
import parser.POMDP;
import parser.ParsePOMDP;

public class Benchmark {
	
	private ArrayList<POMDP> testProblems;

	private PrintWriter writer;

	private void loadPOMDP() {
		this.testProblems = new ArrayList<POMDP>();
//		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway.POMDP"));
//		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway2.POMDP"));
//		this.testProblems.add(ParsePOMDP.LargeMDP("domains/aircraft.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/aircraft2.POMDP"));
		
	}
	
	public void benchmark(Solver solver) {
		Metrics metrics = new Metrics(solver);
		metrics.tic();
		solver.Solve();
		metrics.toc(this.writer);
	}

	private void initPrintWriter(String fileName) throws IOException {
		writer = new PrintWriter(new FileWriter(fileName));
		writer.println("solver; problem; memory (MB); time (s)");
	}

	private void closePrintWriter() {
		this.writer.flush();
		this.writer.close();
	}

	public ArrayList<POMDP> getTestProblems() {
		return testProblems;
	}

	public static void main(String[] args) throws IOException {
		Benchmark benchmark = new Benchmark();
		benchmark.loadPOMDP();
		benchmark.initPrintWriter("results/result.csv");
		for (POMDP problem : benchmark.getTestProblems()) {
			benchmark.benchmark(new MCTSSolver(problem));
//			benchmark.benchmark(new VI(problem));
//			benchmark.benchmark(new VI_FeasibleActions(problem));
//			benchmark.benchmark(new VI_CachePerformance(problem));

		}
		benchmark.closePrintWriter();
    }
}