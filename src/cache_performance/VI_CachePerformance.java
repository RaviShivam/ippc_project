package cache_performance;

import java.util.ArrayList;
import parser.*;

public class VI_CachePerformance extends Solver {
	private double[]   vt;
	private ArrayList<ArrayList<Double>> priorityList = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<ArrayList<Integer>>> actionList;
	
	//TODO:
	//Need to split up priorityList into paritions somehow.
	
	public VI_CachePerformance() {
		
	}
	
	public VI_CachePerformance(POMDP mdp) {
		super(mdp);
	}
	
	public void clear() {
		this.priorityList.clear();
		this.actionList.clear();
	}
	
	public void Solve() {
		initializeQTable();
		initializeVt();
		this.actionList = sortFeasibleActions(this.mdp);
		// Make assert that discount factor has to be between 0-1
		double delta = 1;
		int count = 0;
		
		//Making initial QMatrix
		delta = calculateQMatrix(mdp);		
		//Saving current QMatrix in qTablePrev
		saveCurrentQMatrix();
		
		prioritizeList();
		
		while(delta > 0.00001) {
			delta = 0;
			count++;
			delta = calculateQMatrix(mdp, this.actionList, this.priorityList);
			saveCurrentQMatrix();
			prioritizeList();
		}
		mu.recordMemoryUsuage();
		//printQTable();
		System.out.format("The amount of cycles was: %d%n", count);
	}
	
	private double calculateQMatrix(POMDP mdp) {
		double delta = 0;
		int s,a;
		for(s = 0; s < this.mdp.getNumStates(); s++) {
			double bellman;
			//Iterating over each Action
			for(a = 0; a < this.actionList.get(s).size(); a++) {
				bellman = calculateValue(this.mdp, this.actionList.get(s).get(a), s, a);
				this.qTable[s][a] = bellman;
				delta = getDelta(delta, s, a);
			}
		}
		return delta;
	}
	
	private double calculateQMatrix(POMDP mdp, ArrayList<ArrayList<ArrayList<Integer>>> actionList, ArrayList<ArrayList<Double>> priorityList ) {
		double bellman, delta = 0;
		int i, state, action, sNext;
		for(i = priorityList.size() - 1; i > 0; i--) {
			state = (int) priorityList.get(i).get(1).intValue();
			action = (int) priorityList.get(i).get(2).intValue();
			//System.out.println("Action: " + action + " state: " + state + " size of actionlist: " + actionList.size());
			bellman = calculateValue(mdp, actionList.get(state).get(action), state, action);
			this.qTable[state][action] = bellman;
			delta = getDelta(delta, state, action, i);
		}
		return delta;
	}
	
	/**
	 * Calculates a value for a Qmatrix given a certain State and Action
	 * @param mdp
	 * @param statesPossible - ArrayList<Integer> that holds all possible sNext states for a given action
	 * @param state
	 * @param action
	 * @return
	 */
	private double calculateValue(POMDP mdp, ArrayList<Integer> statesPossible, int state, int action) {
		double value, sum = 0.0;
		int sNext;
		//System.out.println(statesPossible.toString());
		for(sNext = 0; sNext < statesPossible.size(); sNext++) {
			sum = sum + mdp.getTransitionProbability(state, action, statesPossible.get(sNext))*getMaxQTablePrev(statesPossible.get(sNext));
		}
		value = mdp.getReward(state, action) + mdp.getDiscountFactor()*sum;
		//System.out.println("Value going into Qmatrix: " + value);
		return value;
	}
	
	/**
	 * Returns an ArrayList with all feasible actions and s' (next states)
	 * This reduces the search space for the value iterator
	 * @param mdp - Type: POMDP
	 * @return ArrayList<ArrayList<ArrayList<Integer>>> 
	 */
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
		//System.out.println(actionList.toString());
		return actionList;
	}
	
	
	private void prioritizeList() {
		int length = this.priorityList.size();
		
		if(this.priorityList == null || length == 0) {
			System.out.println("Could not prioritize list due to no values being in the list");
			return;
		}
		
		quickSort(0, length - 1);
		//System.out.println(this.priorityList.toString() + "\n");
	}
	
	/**
	 * Sorting Algorithm comes from: http://www.vogella.com/tutorials/JavaAlgorithmsQuicksort/article.html
	 * @param low
	 * @param high
	 */
	private void quickSort(int low, int high) {
		int i = low;
		int j = high;
        // Get the pivot element from the middle of the list
        Double pivot = this.priorityList.get(low + (high-low)/2).get(0);

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller than the pivot
            // element then get the next element from the left list
            while (this.priorityList.get(i).get(0) < pivot) {
                i++;
            }
            // If the current value from the right list is larger than the pivot
            // element then get the next element from the right list
            while (this.priorityList.get(j).get(0) > pivot) {
                j--;
            }

            // If we have found a value in the left list which is larger than
            // the pivot element and if we have found a value in the right list
            // which is smaller than the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quickSort(low, j);
        if (i < high)
            quickSort(i, high);

	}
	
	private void exchange(int i, int j) {
		ArrayList<Double> temp = this.priorityList.get(i);
		this.priorityList.set(i, this.priorityList.get(j));
		this.priorityList.set(j, temp);
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
	
	private double getDelta(double delta, int s, int a, int i) {
		this.priorityList.remove(i);
		ArrayList<Double> info = new ArrayList<Double>();
		double d;	
		d = (double) Math.abs( this.qTable[s][a]) - Math.abs(this.qTablePrev[s][a]);
		info.add(d);
		info.add((double) s);
		info.add((double) a);
		
		//System.out.println("delta: " + d);
		//if(d > 0) {this.priorityList.add(info); }
		this.priorityList.add(info);
		if(delta < d) {
			return d;
		}
		return delta;
	}
	
	//This function does not work
	private double getDelta(double delta, int s, int a) {
		ArrayList<Double> info = new ArrayList<Double>();
		double d;	
		d = (double) Math.abs( this.qTable[s][a]) - Math.abs(this.qTablePrev[s][a]);
		info.add(d);
		info.add((double) s);
		info.add((double) a);
		this.priorityList.add(info);
		if(delta < d) {
			return d;
		}
		return delta;
	}
	
	private void initializeVt() {
		this.vt = new double[this.mdp.getNumStates()];
	}
}
