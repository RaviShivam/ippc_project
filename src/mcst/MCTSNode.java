import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTSNode {
    MCTSNode[] children;
    double nVisits, totValue;
    POMDP mdp = null;
    private Random r = new Random();
    private int numberOfActions = 0;
    private double epsilon = 1e-6;
    private int stateLabel = -1;

    public MCTSNode(POMDP mdp, int stateLabel) {
        this.mdp = mdp;
        this.numberOfActions = mdp.getNumActions();
        this.stateLabel = stateLabel;
    }

    public void simulateRound() {
        if (this.isLeaf()) {
            this.expand();
            int action = this.select().getKey();
            this.nVisits++;
            this.totValue = mdp.getReward(this.stateLabel, action);
            return;
        }
        Pair<Integer, MCTSNode> selection = this.select();
        int bestAction = selection.getKey();
        MCTSNode bestNode = selection.getValue();
        bestNode.simulateRound();

        double immediateReward = this.mdp.getReward(this.stateLabel, bestAction);
        this.totValue = immediateReward + mdp.getDiscountFactor()*bestNode.totValue;
        this.nVisits++;
    }

    public boolean isLeaf() {
        return this.children == null;
    }

    private Pair<Integer, MCTSNode> select() {
        MCTSNode selected = null;
        int action = 0;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < numberOfActions; i++) {
            MCTSNode c = this.randomTransition(i);
            if (c == null) continue;
            double uctValue = c.totValue / (c.nVisits + epsilon) +
                    Math.sqrt(2*Math.log(nVisits + 1) / (c.nVisits + epsilon));
            if (uctValue > bestValue) {
                selected = c;
                action = i;
                bestValue = uctValue;
            }
        }
        return new Pair<>(action, selected);
    }

    private MCTSNode randomTransition(int action) {
        List<Integer> possibleStates = new ArrayList<>();
        for (int nState = 0; nState < mdp.getNumStates(); nState++) {
            double p = (int) (mdp.getTransitionProbability(this.stateLabel, action, nState) * 100); //probability for a transition
            for (int i = 0; i < p; i++) {
                possibleStates.add(nState);
            }
        }
        if (possibleStates.size() == 0) return null;
        int index = possibleStates.get(r.nextInt(possibleStates.size())); // Get a random state from transition table
        return this.children[index];
    }

    public void expand() {
        this.children = new MCTSNode[mdp.getNumStates()];
        for (int i = 0; i < this.mdp.getNumStates(); i++) {
            children[i] = new MCTSNode(this.mdp, i);
        }
    }
}
