package mcts;

import cache_performance.Solver;
import javafx.util.Pair;
import parser.POMDP;
import parser.ParsePOMDP;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MCTSSolver extends Solver {
    private int TIMEOUT = 2000;
    private int depth = 100;
    private int horizon = 150;
    private MCTSNode root;

    public MCTSSolver(POMDP mdp) {
        super(mdp);
        this.solverName = "MCTS Solver";
    }

    public MCTSSolver(POMDP mdp, int TIMEOUT) {
        super(mdp);
        this.TIMEOUT = TIMEOUT;
    }

    public void printChildren(MCTSNode node) {
        for (Pair<Integer, Integer> pair: node.getChildren().keySet()) {
            MCTSNode c = node.getChildren().get(pair);
            System.out.printf("action: %d, state: %d, value: %f", pair.getKey(), pair.getValue(), c.getValue());
            System.out.println();
        }
        System.out.println("================");
    }

    public void Solve(int TIMEOUT) {
        this.TIMEOUT = TIMEOUT;
        this.Solve();
    }

    public void Solve(){
        MCTSNode root = new MCTSNode(this.mdp, mdp.getInitialState());
        long start = System.currentTimeMillis();
        long elapsed_time;
        do {
            root.simulateRound(this.horizon);
//            printChildren(root);
            elapsed_time = System.currentTimeMillis() - start;
//        } while (true);
        } while (elapsed_time < this.TIMEOUT);
        System.out.println(tranverseTreeBestChild(root, this.depth));
        this.root = root;
    }

    private double tranverseTreeBestChild(MCTSNode root, int horizon) {
        if (horizon==0 || root.getChildren().isEmpty()) return 0.0;
        Pair<Integer, Integer> best_action_state = root.getChildren().keySet().iterator().next();
        MCTSNode best_Child = root.getChildren().get(best_action_state);
        for (Pair<Integer, Integer> action_state: root.getChildren().keySet()) {
            if (root.getChildren().get(action_state).getValue() > best_Child.getValue()) {
                best_action_state = action_state;
                best_Child = root.getChildren().get(best_action_state);
            }
        }
        return this.mdp.getReward(root.getStateLabel(), best_action_state.getKey()) + this.tranverseTreeBestChild(best_Child, horizon-1);
    }

    public static void main(String[] args) throws IOException {
        String domain = "domains/";
        PrintWriter writer = new PrintWriter(new FileWriter("mcts_time_accuracy.csv"));
        writer.println("time(s), value");
        String[] problems = {"aircraft.POMDP"};
//        String[] problems = {"tiger.aaai.POMDP"};
        for (String problem : problems) {
            MCTSSolver mctsSolver = new MCTSSolver(ParsePOMDP.readPOMDP(domain+problem));
            int time_out = 0;
            for (int i = 0; i < 21; i++) {
                mctsSolver.Solve(time_out);
                double value = mctsSolver.tranverseTreeBestChild(mctsSolver.root, mctsSolver.depth);
                writer.println(time_out + ", " + value);
                time_out += 100;
            }
        }
        writer.flush();
        writer.close();
    }
}
