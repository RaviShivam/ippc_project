package cache_performance;

import parser.*;
import java.util.ArrayList;

public class VI_FeasibleActions extends Solver{
	private double[]   vt;
	private ArrayList<ArrayList<ArrayList<Integer>>> actionList;
	
	//TODO:
	//Make possible action list
	//Make priority list
	//See if you need to run the whole priority
	
	public VI_FeasibleActions() {
		
	}

	public VI_FeasibleActions(POMDP mdp) {
		super(mdp);
		this.solverName = "VI_FeasibleActions";
	}
	
	public void Solve() {
		initializeQTable();
		initializeVt();
		this.actionList = sortFeasibleActions(this.mdp);
		// Make assert that discount factor has to be between 0-1
		int s,a;
		double sum;
		double delta = 1;
		int count = 0;
		while(delta > 0.0001) {
		//for (int i = 0; i < 2000; i++) {
			delta = 0;
			count++;
			//Iterating over each State
			for(s = 0; s < this.mdp.getNumStates(); s++) {
				double bellman;
				//Iterating over each Action
				for(a = 0; a < this.actionList.get(s).size(); a++) {
					bellman = calculateValue(this.mdp, this.actionList.get(s).get(a), s, a);
					this.qTable[s][a] = bellman;

					delta = getDelta(delta, s, a);
				}
			}
			saveCurrentQMatrix();

		}
//		mu.recordMemoryUsuage();
		printQTable();
		System.out.format("The amount of cycles was: %d%n", count);
	}



	private double calculateValue(POMDP mdp, ArrayList<Integer> statesPossible, int state, int action) {
		double value, sum = 0.0;
		int sNext;
		//System.out.println(statesPossible.toString());
		for(sNext = 0; sNext < statesPossible.size(); sNext++) {
			sum = sum + mdp.getTransitionProbability(state, action, statesPossible.get(sNext))*getMaxQTablePrev(statesPossible.get(sNext));
		}
		//System.out.println("State: " + state + " action: " + action + " Snextlist size: " + statesPossible.size());
		value = mdp.getReward(state, action) + mdp.getDiscountFactor()*sum;
		//System.out.println("Value going into Qmatrix: " + value);
		return value;
	}

	//Wondering if this is not a waste of resource
		private ArrayList<ArrayList<ArrayList<Integer>>> sortFeasibleActions(POMDP mdp) {
			int s, a, sNext;

			ArrayList<ArrayList<ArrayList<Integer>>> actionList = new ArrayList<ArrayList<ArrayList<Integer>>>();
			for(s = 0; s < mdp.getNumStates(); s++) {
				ArrayList<ArrayList<Integer>> possibleActions = new ArrayList<ArrayList<Integer>>();
				for(a = 0; a < mdp.getNumActions(); a++) {
					ArrayList<Integer> possibleSNext = new ArrayList<Integer>();
					for(sNext = 0; sNext < mdp.getNumStates(); sNext++) {
						if(mdp.getTransitionProbability(s, a, sNext) > 0) {
							//System.out.println("statepossibility list: " + mdp.getTransitionProbability(s, a, sNext) + " s: " + s + " sNext: " + sNext);
							possibleSNext.add(sNext);
						}
					}
					possibleActions.add(possibleSNext);
				}
				actionList.add(possibleActions);
			}
			return actionList;
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
		d = Math.abs( this.qTable[s][a]) - Math.abs(this.qTablePrev[s][a]);
		if(delta < d) {
			return d;
		}
		return delta;
	}



	private void initializeVt() {
		this.vt = new double[this.mdp.getNumStates()];
	}
}
