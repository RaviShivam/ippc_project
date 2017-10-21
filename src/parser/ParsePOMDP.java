package parser;

import java.util.HashMap;

import libpomdp.common.Pomdp;
import libpomdp.parser.FileParser;

public class ParsePOMDP {
	/**
	 * Parse a .POMDP file and create POMDP object
	 * @param filePath full path to the .POMDP file
	 * @return a POMDP object
	 */
	public static POMDP readPOMDP(String filePath) {
		System.out.println();
		System.out.println("=== READ POMDP FILE ===");
		System.out.println("File: "+filePath);
		
		Pomdp pomdp = FileParser.loadPomdp(filePath, 0);
		int nStates = pomdp.nrStates();
		int nActions = pomdp.nrActions();
		int nObservations = pomdp.nrObservations();
		double discountFactor = pomdp.getGamma();
		
		double[][] rewardFunction = new double[nStates][nActions];
		double[][][] transitionFunction = new double[nStates][nActions][nStates];
		double[][][] observationFunction = new double[nActions][nStates][nObservations];
		
		HashMap<Integer,String> actionLabels = new HashMap<Integer,String>();
		
		for(int s=0; s<nStates; s++) {
			for(int a=0; a<nActions; a++) {
				for(int sNext=0; sNext<nStates; sNext++) {
					transitionFunction[s][a][sNext] = pomdp.getTransitionTable(a).get(s, sNext);
				}
			}
		}
		
		for(int a=0; a<nActions; a++) {
			for(int sNext=0; sNext<nStates; sNext++) {
				for(int o=0; o<nObservations; o++) {
					observationFunction[a][sNext][o] = pomdp.getObservationTable(a).get(sNext, o);
				}
			}
		}
		
		for(int s=0; s<nStates; s++) {
			for(int a=0; a<nActions; a++) {
				rewardFunction[s][a] = pomdp.getRewardTable(a).get(s);
			}
		}
		
		for(int a=0; a<nActions; a++) {
			try {
				actionLabels.put(a, pomdp.getActionString(a));
			}
			catch (NullPointerException e) {
				actionLabels.put(a, a+"");
			}
		}
		
		double[] beliefEntries = pomdp.getInitialBeliefState().getPoint().getArray();
		BeliefPoint b0 = new BeliefPoint(beliefEntries);
		
		// extract instance name
		String[] fileSplit = filePath.split("\\/");
		String filename = fileSplit[fileSplit.length-1];
		
		return new POMDP(filename.replace(".POMDP", ""), nStates, nActions, nObservations, discountFactor, rewardFunction, transitionFunction, observationFunction, b0);
	}
}
