package benchmark;
import parser.*;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;

import cache_performance.*;

public class Benchmark {
	
	public ArrayList<POMDP> testProblems;
	
	private void loadPOMDP() {
		this.testProblems = new ArrayList<POMDP>();
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/hallway2.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/saci-s12-a6-z5.95.POMDP"));
		this.testProblems.add(ParsePOMDP.readPOMDP("domains/saci-s100-a10-z31.POMDP"));
		
	}
	
	public POMDP getMDP(int i) {
		return this.testProblems.get(i);
	}
	
	public static void main(String[] args) {
		Benchmark benchmark = new Benchmark();
		
		benchmark.loadPOMDP();
			
	    VI vi = new VI();
	    VI_FeasibleActions vi_FA = new VI_FeasibleActions();
	    VI_CachePerformance vi_cache = new VI_CachePerformance();
	    
	    MemoryUsuage mu = MemoryUsuage.getInstance();
	    mu.recordMemoryUsuage();
	    
	    for(int i = 0; i < benchmark.testProblems.size(); i++) {
	    	vi.addMDP(benchmark.getMDP(i));
	    	vi_FA.addMDP(benchmark.getMDP(i));
	    	vi_cache.addMDP(benchmark.getMDP(i));
	    	vi.Solve();
	    	vi_FA.Solve();
	    	vi_cache.Solve();
	    	vi_cache.clear();
	    }
	    
    }
	


}
