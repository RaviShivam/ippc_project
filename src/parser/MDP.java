package parser;

import java.util.Random;

public class MDP {
	private String name;
	private int nStates;
	private int nActions;
	private double[][] rewardFunction;
	private double[][][] transitionFunction;
	private double discountFactor;
	private int initialState;
	private double minReward = Double.POSITIVE_INFINITY;
	private double maxReward = Double.NEGATIVE_INFINITY;
	
	private boolean hasTimeDependentReward = false;
	private int T;
	private double[][][] timeRewardFunction;
	
	private boolean hasTimeDependentTransitions = false;
	private double[][][][] timeTransitionFunction;
	
	public MDP(String name, int nStates, int nActions, double discountFactor, double[][] rewardFunction, double[][][] transitionFunction, int initialState) {
		this.name = name;
		this.nStates = nStates;
		this.nActions = nActions;
		this.transitionFunction = transitionFunction;
		this.discountFactor = discountFactor;
		this.initialState = initialState;
		setRewardFunction(rewardFunction);
	}
	
	public MDP(String name, int nStates, int nActions, double discountFactor, double[][][] rewardFunction, double[][][] transitionFunction, int initialState) {
		this.name = name;
		this.nStates = nStates;
		this.nActions = nActions;
		this.transitionFunction = transitionFunction;
		this.discountFactor = discountFactor;
		this.initialState = initialState;
		setRewardFunction(rewardFunction);
	}
	
	public MDP(String name, int nStates, int nActions, double discountFactor, double[][] rewardFunction, double[][][][] transitionFunction, int initialState) {
		this.name = name;
		this.nStates = nStates;
		this.nActions = nActions;
		this.discountFactor = discountFactor;
		this.initialState = initialState;
		setRewardFunction(rewardFunction);
		
		this.transitionFunction = null;
		this.timeTransitionFunction = transitionFunction;
		this.T = transitionFunction.length;
		this.hasTimeDependentTransitions = true;
	}
	
	public MDP(String name, int nStates, int nActions, double discountFactor, double[][][] rewardFunction, double[][][][] transitionFunction, int initialState) {
		this.name = name;
		this.nStates = nStates;
		this.nActions = nActions;
		this.discountFactor = discountFactor;
		this.initialState = initialState;
		setRewardFunction(rewardFunction);
		
		this.transitionFunction = null;
		this.timeTransitionFunction = transitionFunction;
		this.T = transitionFunction.length;
		this.hasTimeDependentTransitions = true;
	}
	
	/**
	 * Set reward function
	 * @param rewardFunction reward function
	 */
	public void setRewardFunction(double[][] rewardFunction) {
		this.rewardFunction = rewardFunction;
		
		minReward = Double.POSITIVE_INFINITY;
		maxReward = Double.NEGATIVE_INFINITY;
		
		for(int s=0; s<nStates; s++) {
			for(int a=0; a<nActions; a++) {
				minReward = Math.min(minReward, rewardFunction[s][a]);
				maxReward = Math.max(maxReward, rewardFunction[s][a]);
			}
		}
	}
	
	/**
	 * Set time-dependent reward function
	 * @param rewardFunction reward function
	 */
	public void setRewardFunction(double[][][] timeRewardFunction) {
		this.timeRewardFunction = timeRewardFunction;
		this.T = timeRewardFunction.length;
		this.hasTimeDependentReward = true;
		
		minReward = Double.POSITIVE_INFINITY;
		maxReward = Double.NEGATIVE_INFINITY;
		
		for(int t=0; t<T; t++) {
			for(int s=0; s<nStates; s++) {
				for(int a=0; a<nActions; a++) {
					minReward = Math.min(minReward, timeRewardFunction[t][s][a]);
					maxReward = Math.max(maxReward, timeRewardFunction[t][s][a]);
				}
			}
		}
	}
	
	/**
	 * Get reward function
	 * @return reward function
	 */
	public double[][] getRewardFunction() {
		return rewardFunction;
	}
	
	/**
	 * Get reward R(s,a)
	 * @param s state s
	 * @param a action a
	 * @return reward R(s,a)
	 */
	public double getReward(int s, int a) {
		assert s<nStates && a<nActions && !hasTimeDependentReward;
		return rewardFunction[s][a];
	}
	
	/**
	 * Get reward R(s,a) at time t
	 * @param t time t
	 * @param s state s
	 * @param a action a
	 * @return reward R(s,a) at time t
	 */
	public double getReward(int t, int s, int a) {
		assert s<nStates && a<nActions && t<T;
		return hasTimeDependentReward ? timeRewardFunction[t][s][a] : rewardFunction[s][a];
	}
	
	/**
	 * Get minimum instantaneous reward
	 * @return min reward
	 */
	public double getMinReward() {
		return minReward;
	}
	
	/**
	 * Get maximum instantaneous reward
	 * @return max reward
	 */
	public double getMaxReward() {
		return maxReward;
	}
	
	/**
	 * Set transition function
	 * @param transitionFunction
	 */
	public void setTransitionFunction(double[][][] transitionFunction) {
		this.transitionFunction = transitionFunction;
	}
	
	/**
	 * Get transition probability P(sNext | s,a)
	 * @param s state s
	 * @param a action a
	 * @param sNext state sNext
	 * @return probability P(sNext | s,a)
	 */
	public double getTransitionProbability(int s, int a, int sNext) {
		assert s<nStates && a<nActions && sNext<nStates && !hasTimeDependentTransitions;
		return transitionFunction[s][a][sNext];
	}
	
	/**
	 * Get transition probability P(sNext | s,a) at time t
	 * @param t time t
	 * @param s state s
	 * @param a action a
	 * @param sNext state sNext
	 * @return probability P(sNext | s,a)
	 */
	public double getTransitionProbability(int t, int s, int a, int sNext) {
		assert s<nStates && a<nActions && sNext<nStates && t<T;
		return hasTimeDependentTransitions ? timeTransitionFunction[t][s][a][sNext] : transitionFunction[s][a][sNext];
	}
	
	/**
	 * Get transition function
	 * @return transition function
	 */
	public double[][][] getTransitionFunction() {
		return transitionFunction;
	}
	
	/**
	 * Add random noise to the transition model
	 * @param rnd random generator
	 * @param scalar scalar which is multiplied by the random noise in [0,1]
	 */
	public void addTransitionNoise(Random rnd, double scalar) {
		for(int s=0; s<nStates; s++) {
			for(int a=0; a<nActions; a++) {
				double probSum = 0.0;
				
				// add some noise to transitions with non-zero probability and keep track of total sum
				for(int sNext=0; sNext<nStates; sNext++) {
					if(transitionFunction[s][a][sNext] > 0.0001) {
						double noise = rnd.nextDouble() * scalar;
						transitionFunction[s][a][sNext] += noise;
					}
					
					probSum += transitionFunction[s][a][sNext];
				}
				
				// normalize
				for(int sNext=0; sNext<nStates; sNext++) {
					transitionFunction[s][a][sNext] = transitionFunction[s][a][sNext] / probSum;
				}
			}
		}
	}
	
	/**
	 * Get number of states
	 * @return number of states
	 */
	public int getNumStates() {
		return nStates;
	}
	
	/**
	 * Get number of actions
	 * @return number of actions
	 */
	public int getNumActions() {
		return nActions;
	}
	
	/**
	 * Get discount factor
	 * @return discount factor
	 */
	public double getDiscountFactor() {
		return discountFactor;
	}
	
	/**
	 * Get initial state
	 * @return initial state
	 */
	public int getInitialState() {
		return initialState;
	}
	
	/**
	 * Get name
	 * @return name
	 */
	public String getName() {
		return name;
	}
}
