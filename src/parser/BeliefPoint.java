package parser;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BeliefPoint {
	private double[] belief;
	
	private double admissibleCost = 0.0; // admissible cost, used by CPBVI
	
	private boolean actionObservationProbInitialized = false;
	private double[][] aoProbs; // aoProbs[a][o] represents P(o|b,a)
	
	private boolean isStateBelief = false;
	private int state = -1;
	
	private double upperBound = Double.POSITIVE_INFINITY; // upper bound on the value, used by GapMin and FiniteVI
	private boolean upperBoundTight = false; // used by FiniteVI
	private BeliefPoint[][] beliefTransitionMap = null;
	private double lastGap = Double.POSITIVE_INFINITY;
	private int sleepTime = 0;
	private boolean[] successors = null;
	
	private List<Integer> history = new ArrayList<Integer>();
	
	public BeliefPoint(double[] belief) {
		this.belief = belief;
	}
	
	public double[] getBelief() {
		return belief;
	}
	
	public double getBelief(int s) {
		assert s >= 0 && s < belief.length;
		return belief[s];
	}
	
	public void addToHistory(int i) {
		history.add(i);
	}
	
	public List<Integer> getHistory() {
		return history;
	}
	
	public void setHistory(List<Integer> history) {
		this.history = history;
	}
	
	public List<Integer> getHistoryCopy() {
		List<Integer> newHistory = new ArrayList<Integer>();
		newHistory.addAll(history);
		return newHistory;
	}
	
	public int hashCode() {
		return history.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof BeliefPoint) {
			List<Integer> otherHistory = ((BeliefPoint) o).getHistory();
			
			if(history.size() != otherHistory.size()) {
				return false;
			}
			else {
				boolean isEqual = true;
				
				for(int i=0; i<history.size()&&isEqual; i++) {
					isEqual = isEqual && (history.get(i)==otherHistory.get(i));
				}
				
				return isEqual;
			}
			
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		String ret = "<BP(";
		
		for(int i=0; i<belief.length; i++) {
			ret += belief[i]+",";
		}
		
		return ret+")>";
	}
	
	public boolean hasActionObservationProbabilities() {
		return actionObservationProbInitialized;
	}
	
	public void setActionObservationProbabilities(double[][] aoProbs) {
		assert this.aoProbs == null;
		this.aoProbs = aoProbs;
		this.actionObservationProbInitialized = true;
	}
	
	public double getActionObservationProbability(int a, int o) {
		assert aoProbs != null;
		return aoProbs[a][o];
	}
	
	public double getUpperBound() {
		return upperBound;
	}
	
	public void setUpperBound(double upper) {
		assert upper > Double.NEGATIVE_INFINITY;
		this.upperBound = upper;
	}
	
	public void setStateBelief() {
		isStateBelief = true;
	}
	
	public boolean isStateBelief() {
		return isStateBelief;
	}
	
	public void setState(int s) {
		state = s;
	}
	
	public int getState() {
		assert isStateBelief;
		return state;
	}
	
	public void setAdmissibleCost(double ac) {
		this.admissibleCost = ac;
	}
	
	public double getAdmissibleCost() {
		return admissibleCost;
	}
	
	public boolean isUpperBoundTight() {
		return upperBoundTight;
	}
	
	public void setUpperBoundTight() {
		upperBoundTight = true;
	}
	
	public void resetUpperBound() {
		upperBoundTight = false;
		upperBound = Double.POSITIVE_INFINITY;
	}
	
	public void setBeliefTransitionMap(BeliefPoint[][] map) {
		this.beliefTransitionMap = map;
	}
	
	public boolean hasBeliefTransitionMap() {
		return beliefTransitionMap != null;
	}
	
	public BeliefPoint[][] getBeliefTransitionMap() {
		return beliefTransitionMap;
	}
	
	public double getLastGap() {
		return lastGap;
	}
	
	public void setLastGap(double gap) {
		this.lastGap = gap;
	}
	
	public int getSleepTime() {
		return sleepTime;
	}
	
	public void reduceSleepTime() {
		sleepTime--;
		assert sleepTime >= 0;
	}
	
	public void setSleepTime(int t) {
		this.sleepTime = t;
	}
	
	public boolean[] getSuccessors() {
		return successors;
	}
	
	public void setSuccessors(boolean[] successors) {
		this.successors = successors;
	}
}
