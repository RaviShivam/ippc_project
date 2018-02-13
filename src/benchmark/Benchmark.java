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
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway2.POMDP"));
//		this.testProblems.add(ParsePOMDP.LargeMDP("domains/hallway.POMDP"));
		
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
		if(false){
			benchmark.testCSV();
		} else {
			benchmark.loadPOMDP();
			benchmark.initPrintWriter("results/result.csv");
			for (POMDP problem : benchmark.getTestProblems()) {
//				benchmark.benchmark(new MCTSSolver(problem));
				benchmark.benchmark(new VI(problem));
				benchmark.benchmark(new VI_FeasibleActions(problem));
				benchmark.benchmark(new VI_CachePerformance(problem));
			}
			benchmark.closePrintWriter();
		}

    }

    public void testCSV() {

		ArrayList<String> List = new ArrayList<>();

		List.add("0,0,0,0,0");
		List.add("1,2,3,4,5");
		List.add("6,7,8,9,10");

		CSVFile csvFile = new CSVFile();
		csvFile.writeCSVFile("test", List);

	}
}