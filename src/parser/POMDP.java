package parser;

import java.util.Random;

public class POMDP extends MDP {
	private int nObservations;
	private double[][][] observationFunction;
	private BeliefPoint b0;
	
	public POMDP(String name, int nStates, int nActions, int nObservations, double discountFactor, double[][] rewardFunction, double[][][] transitionFunction, double[][][] observationFunction, BeliefPoint b0) {
		super(name, nStates, nActions, discountFactor, rewardFunction, transitionFunction, 0);
		this.nObservations = nObservations;
		this.observationFunction = observationFunction;
		this.b0 = b0;
	}
	
	public POMDP(int nStates, int nActions, int nObservations, double discountFactor, Random rnd) {		
		// initialize POMDP parameters
		super("Random("+nStates+","+nActions+","+nObservations+","+discountFactor+")", nStates, nActions, discountFactor, new double[nStates][nActions], new double[nStates][nActions][nStates], 0);
		this.nObservations = nObservations;
			
		// create a randomly generated POMDP
		double[][] rewardFunction = new double[nStates][nActions];
		double[][][] transitionFunction = new double[nStates][nActions][nStates];
		observationFunction = new double[nActions][nStates][nObservations];
		
		// generate reward function
		for(int s=0; s<nStates; s++) {
			for(int a=0; a<nActions; a++) {
				rewardFunction[s][a] = -10.0 + rnd.nextDouble() * 20.0;
			}
		}
		super.setRewardFunction(rewardFunction);
		
		// generate transition function
		for(int s=0; s<nStates; s++) {
			for(int a=0; a<nActions; a++) {
				double[] prob = getRandomDistribution(nStates, rnd);
				
				for(int sNext=0; sNext<nStates; sNext++) {
					transitionFunction[s][a][sNext] = prob[sNext];
				}
			}
		}
		super.setTransitionFunction(transitionFunction);
		
		// generate observation function
		for(int a=0; a<nActions; a++) {
			for(int s=0; s<nStates; s++) {
				double[] prob = getRandomDistribution(nObservations, rnd);
				
				for(int o=0; o<nObservations; o++) {
					observationFunction[a][s][o] = prob[o];
				}
			}
		}
		
		// initialize uniform initial belief
		double[] beliefEntries = new double[nStates];
		for(int s=0; s<nStates; s++) {
			beliefEntries[s] = 1.0 / ((double) nStates);
		}
		b0 = new BeliefPoint(beliefEntries);
	}
	
	private double[] getRandomDistribution(int n, Random rnd) {
		double a[] = new double[n];
		double s = 0.0d;
		
		for (int i=0; i<n; i++) {
			a[i] = 1.0d - rnd.nextDouble();
			a[i] = -1 * Math.log(a[i]);
			s += a[i];
		}
		
		for (int i=0; i<n; i++) {
			a[i] /= s;
		}
		
		return a;
	}
	
	public int getNumObservations() {
		return nObservations;
	}
	
	public double getObservationProbability(int a, int sNext, int o) {
		assert a<this.getNumActions() && sNext<this.getNumStates() && o<nObservations;
		return observationFunction[a][sNext][o];
	}
	
	public double[][][] getObservationFunction() {
		return observationFunction;
	}
	
	public BeliefPoint getInitialBelief() {
		return b0;
	}
	
	public void prepareBelief(BeliefPoint b) {
		if(b.hasActionObservationProbabilities()) return;
		
		double[][] aoProbs = new double[this.getNumActions()][nObservations];
		
		for(int a=0; a<this.getNumActions(); a++) {
			for(int o=0; o<nObservations; o++) {
				double prob = 0.0;
				
				for(int sNext=0; sNext<this.getNumStates(); sNext++) {
					double p = 0.0;
					
					for(int s=0; s<this.getNumStates(); s++) {
						p += getTransitionProbability(s, a, sNext) * b.getBelief(s);
					}
					
					prob += getObservationProbability(a, sNext, o) * p;
				}
				
				aoProbs[a][o] = prob;
			}
		}
		
		b.setActionObservationProbabilities(aoProbs);
	}
	
	public BeliefPoint updateBelief(BeliefPoint b, int a, int o) {
		assert a<this.getNumActions() && o<nObservations;
		double[] newBelief = new double[this.getNumStates()];
		
		// check if belief point has been prepared
		if(!b.hasActionObservationProbabilities()) {
			prepareBelief(b);
		}
		
		// compute normalizing constant
		double nc = b.getActionObservationProbability(a, o);
		assert nc > 0.0 : "o cannot be observed when executing a in belief b";
		
		// compute the new belief vector
		for(int sNext=0; sNext<this.getNumStates(); sNext++) {
			double beliefEntry = 0.0;
			
			for(int s=0; s<this.getNumStates(); s++) {
				beliefEntry += getTransitionProbability(s, a, sNext) * b.getBelief(s);
			}
			
			newBelief[sNext] = beliefEntry * (getObservationProbability(a, sNext, o) / nc);
		}
		
		return new BeliefPoint(newBelief);
	}
}
