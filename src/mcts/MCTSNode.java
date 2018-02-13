package mcts;

import javafx.util.Pair;
import parser.POMDP;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MCTSNode {
    private Random r = new Random();
    private double epsilon = 1e-6;
    private int stateLabel = -1;
    private double q = 0;
    private double nVisits = 0;
    private int numDefaultSimulations = 50;

    private int numberOfActions;
    private Map<Pair<Integer, Integer>, MCTSNode> children;
    private POMDP mdp;
    private double discount;
    private double Cp = 7;

    private double bestV;


    public MCTSNode(POMDP mdp, int stateLabel) {
        this.mdp = mdp;
        this.discount = mdp.getDiscountFactor();
        this.numberOfActions = mdp.getNumActions();
        this.stateLabel = stateLabel;
        this.children = new HashMap<>();
        this.r.setSeed(10);
    }

    public double simulateRound(int rHorizons) {
        if (rHorizons == 0) return 0.0;
        if (this.isLeaf()) {
            this.q = this.runDefaultPolicy();
            this.nVisits++;
            return this.q;
        }
        // Go deeper in to the tree.
        Pair<Integer, MCTSNode> selection = this.select();
        double value_estimate = selection.getValue().simulateRound(rHorizons - 1);
        this.q += value_estimate;
        this.nVisits++;
        return value_estimate;
    }

    private double runDefaultPolicy() {
        int horizon = 100;
        double value = 0.0;
        for (int i = 0; i < this.numDefaultSimulations; i++) {
            value += this.defaultSim(this.stateLabel, horizon);
        }
        return value / this.numDefaultSimulations;
    }

    private double defaultSim(int parentLabel, int lefHorizons) {
        if (lefHorizons == 0) {
            return 0.0;
        }
        int action = r.nextInt(this.numberOfActions);
        int child = simulateTransition(parentLabel, action);
        return this.mdp.getReward(parentLabel, action) + this.discount * defaultSim(child, lefHorizons - 1);
    }


    private Pair<Integer, MCTSNode> select() {
        MCTSNode selected = null;
        int action = 0;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numberOfActions; i++) {
            MCTSNode c = this.likelyMCTSChild(i);
            double uctValue = (c.q/(c.nVisits + epsilon)) + (Cp*(Math.sqrt(2*Math.log(nVisits) / (c.nVisits + epsilon)))) + r.nextDouble() * epsilon;
            if (uctValue > bestValue) {
                selected = c;
                action = i;
                bestValue = uctValue;
            }
        }
        this.bestV = bestValue;
        return new Pair<>(action, selected);
    }

    private MCTSNode likelyMCTSChild(int action) {
        int sampledState = simulateTransition(this.stateLabel, action);
        //Create a new child if necessary, else return existing child with key.
        Pair<Integer, Integer> sa_pair = new Pair<>(action, sampledState);
        if (!this.children.containsKey(sa_pair)) {
            this.children.put(sa_pair, new MCTSNode(this.mdp, sampledState));
        }
        return this.children.get(sa_pair);
    }

    private int simulateTransition(int parentLabel, int action) {
        double[] transitions = this.mdp.getTransitionFunction()[parentLabel][action];
        double selection = r.nextDouble(); //reuse the variable as sampled state
        double p = 0.0;
        for (int i = 0; i < transitions.length; i++) {
            p += transitions[i];
            if (p >= selection) {
                selection = i;
                break;
            }
        }
        return (int) selection;
    }

    public boolean isLeaf() {
        return this.nVisits == 0;
    }

    public double getValue() {
        return q;
    }

    public Map<Pair<Integer, Integer>, MCTSNode> getChildren() {
        return children;
    }

    public int getStateLabel() {
        return stateLabel;
    }

    public double getBestValue() {
        return this.bestV;
    }
}
