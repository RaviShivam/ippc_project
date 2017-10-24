package mcts;

import javafx.util.Pair;
import parser.POMDP;

import java.util.*;

public class MCTSNode {
    private Random r = new Random();
    private double nVisits;
    private double q = 0;
    private double epsilon = 1e-6;
    private int stateLabel = -1;
    private double Cp = 0.4;
    private int numberOfActions;
    private POMDP mdp;
    private Map<Pair<Integer, Integer>, MCTSNode> children;
    private double discount;

    public MCTSNode(POMDP mdp, int stateLabel) {
        this.mdp = mdp;
        this.discount = mdp.getDiscountFactor();
        this.numberOfActions = mdp.getNumActions();
        this.stateLabel = stateLabel;
        this.children = new HashMap<>();
    }

    public void simulateRound(int rHorizons) {
        if (rHorizons == 0) return;
        if (this.isLeaf()) {
            int action = this.select().getKey();
            this.nVisits++;
            this.q = mdp.getReward(this.stateLabel, action);
            return;
        }
        // Go deeper in to the tree.
        Pair<Integer, MCTSNode> selection = this.select();
        int bestAction = selection.getKey();
        MCTSNode bestNode = selection.getValue();
        bestNode.simulateRound(rHorizons-1);

        // Update current values.
        double immediateReward = this.mdp.getReward(this.stateLabel, bestAction);
        this.q = immediateReward + this.discount*bestNode.q;
        this.nVisits++;
    }

    public boolean isLeaf() {
        return this.children.size() == 0;
    }

    private Pair<Integer, MCTSNode> select() {
        MCTSNode selected = null;
        int action = 0;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numberOfActions; i++) {
            MCTSNode c = this.randomTransition(i);
            double uctValue = c.q + Cp*Math.sqrt(Math.log(nVisits) / (c.nVisits + epsilon)) + r.nextDouble()*epsilon;
            if (uctValue > bestValue) {
                selected = c;
                action = i;
                bestValue = uctValue;
            }
        }
        return new Pair<>(action, selected);
    }
    private MCTSNode randomTransition(int action) {
        double selection = r.nextDouble(); //reuse the variable as sampled state
        double p = 0.0;
        double[] transitions = this.mdp.getTransitionFunction()[this.stateLabel][action];
        for (int i = 0; i < transitions.length;i++) {
           p += transitions[i];
           if (p>=selection) {
               selection= i;
               break;
           }
        }
        int sampledState = (int) selection;

//        List<Integer> possibleStates = new ArrayList<>();
//        for (int nState = 0; nState < mdp.getNumStates(); nState++) {
//            double p = (int) (mdp.getTransitionProbability(this.stateLabel, action, nState) * 100); //probability for a transition
//            for (int i = 0; i < p; i++) {
//                possibleStates.add(nState);
//            }
//        }
//        int sampledState = possibleStates.get(r.nextInt(possibleStates.size())); // Get a random state from transition table
        //Create a new child if necessary, else return existing child with key.
        Pair<Integer, Integer> sa_pair = new Pair<>(action, sampledState);
        if (!this.children.containsKey(sa_pair)) {
            this.children.put(sa_pair, new MCTSNode(this.mdp,sampledState));
        }
        return this.children.get(sa_pair);
    }

    public double getValue() {
        return q;
    }
}
