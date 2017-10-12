package rddl.solver.mdp.mcts;

import java.math.BigInteger;

//import mcts.TreeNode;
//import rddl.EvalException;
//import rddl.State;
//import rddl.policy.EnumerableStatePolicy;
//import util.Pair;

import java.math.*;
import java.util.*;

import rddl.*;
import rddl.RDDL.*;
import rddl.policy.*;
import util.*;

public class mcts_new extends EnumerableStatePolicy{

//	@Override
	protected final int TIMEOUT_ORDER = 1000; //1000 milliseconds
	protected final double gamma = 0.85; // discount value
	protected int TIMEOUT = 160 * TIMEOUT_ORDER; 
	protected List<HashMap<BigInteger, HashMap<String, Double>>> rewardsPerHorizon = null;
	protected List<HashMap<BigInteger, HashMap<String, Integer>>> pullsPerHorizon = null;
	protected List<HashMap<BigInteger, Integer>> visitsPerHorizon = null;
	// TODO Auto-generated method stub
	/*
	 * function mcts(state)
	 * repeat
	 * 	search(state,0)
	 * until timeout
	 * return bestaction(state,0)
	 * 
	 * function search(state,depth)
	 * 	if teminal(state) then return 0
	 * 	if isleaf(state,d) then return evaluate(state)
	 * 	action = selectAction(state,depth)
	 * 	(nextstate,reward) = simulateAction(state,Action)
	 * 	q = reward+gamma search(nextstate,depth+1)
	 * 	updateval(state,action,q,depth)
	 * return q
	 */
	protected String getBestAction(State s){
		
		BigInteger stateAsNumber = this.getStateLabel(s);
		
		long timeout = this.getTimePerAction();
		
		Pair<Integer, Long> searchResult = buildSearchTree(s, timeout);
		int completedSearches = searchResult._o1;
		long elapsedTime = searchResult._o2;
		
		Pair<String, Double> result = this.getUCTBestAction(stateAsNumber, this.getRemainingHorizons(), 0.0);
		String action = result._o1;
		double reward = result._o2;
		
		int searchTreeDepth = 0;
		
		for (int i = 0; i < this.rewardsPerHorizon.size(); i++) {
			if (this.rewardsPerHorizon.get(i).size() > 0) {
				searchTreeDepth = this.rewardsPerHorizon.size() - i;
				break;
			}
		}
		return action;
	}
	
	private Pair<String, Double> getUCTBestAction(BigInteger stat, int remainingHorizons, double d) {
		// TODO Auto-generated method stub
		return null;
	}

	protected Pair<Integer, Long> buildSearchTree(State s, long timeout) {
		int completedSearches = 0;
		
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0;
		
		do {
			this.search(s, this.getRemainingHorizons());
			completedSearches++;
			elapsedTime = System.currentTimeMillis() - startTime;
		} while (elapsedTime < timeout);
		
		return new Pair<Integer, Long>(completedSearches, elapsedTime);
	}
	
	protected double search(State state, int remainingHorizons) {
		if(remainingHorizons==0) return 0.0;
		BigInteger stateAsNumber = this.getStateLabel(state);
		if (isLeaf(stateAsNumber, remainingHorizons)) 
			return this.evaluate(state);
		Pair<String,Double> actionnr = selectAction(stateAsNumber,remainingHorizons); 
		String action = actionnr._o1;
		Pair<State, Double> result = simulateAction(stateAsNumber, action);
		State nextstate = result._o1;
		double reward = result._o2;
		double q = reward + gamma*search(nextstate,remainingHorizons-1);
		updateValue(stateAsNumber,action,q,remainingHorizons);
		return q;
	}

	private double evaluate(State state) {
		// TODO Auto-generated method stub
		return 0;
	}

	private void updateValue(BigInteger state, String action, double q, int remainingHorizons) {
		// TODO Auto-generated method stub
		
	}

	private Pair<State, Double> simulateAction(BigInteger state, String action) {
		// TODO Auto-generated method stub
		
		
	}

	private Pair<String,Double> selectAction(BigInteger state, int remainingHorizons) {
		// TODO Auto-generated method stub
		String bestAction = null;
		double bestActionReward = 0;
		
		int visitTimes = 0;
		HashMap<BigInteger,HashMap<String,Double>> reward = this.rewardsPerHorizon.get(remainingHorizons-1);
		HashMap<BigInteger,Integer> visit = this.visitsPerHorizon.get(remainingHorizons-1);
		HashMap<BigInteger,HashMap<String,Integer>> pull = this.pullsPerHorizon.get(remainingHorizons-1);
		//how many times was this node visited?
		if(visit.containsKey(state))
			visitTimes = visit.get(state);
		for(CString action:this.getActions()){
			String a = action._string;
			double actionReward = 0;
			int N = 0;
			// UCB formular: v_i+ C*sqrt(lnN/n_i) n_i is the number of the times the node has been visited,N
			//is the total number of times that its parent has been visited
			if(reward.containsKey(state) && reward.get(state).containsKey(a))
				actionReward = reward.get(state).get(action);
			if(pull.containsKey(state) && pull.get(state).containsKey(a))
				N = pull.get(state).get(action);
			if(N!=0 && visitTimes!=0)
				actionReward = actionReward+Math.sqrt(Math.log(N)/visitTimes);
			if(actionReward>bestActionReward){
				bestActionReward = actionReward;
				bestAction = a;
			}
		}
		
		return new Pair<String,Double>(bestAction,bestActionReward);
	}

	private boolean isLeaf(BigInteger state, int remainingHorizons) {
		HashMap<BigInteger, HashMap<String, Double>> rewards = this.rewardsPerHorizon.get(remainingHorizons - 1);
		if (!rewards.containsKey(state)) return true;
		HashMap<String, Double> actionRewards = rewards.get(state);
		return (actionRewards == null || actionRewards.keySet().size() < this.getActions().size());
	}
	

	private long getTimePerAction() {
		int t = this.getRemainingHorizons();
		int n = this.getTotalHorizons();
		double s = n * (n+1) * (2*n + 1) / 6;
		double timeShare = t * t / s;
		return (long) (TIMEOUT * timeShare);
	}
}
