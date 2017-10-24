package mcts;

import cache_performance.Solver;
import parser.POMDP;
import parser.ParsePOMDP;

public class MCTSSolver extends Solver {
    private int TIMEOUT = 1000;
    private int horizon = 150;

    public MCTSSolver(POMDP mdp) {
        super(mdp);
        this.solverName = "MCTS Solver";
    }

    public MCTSSolver(POMDP mdp, int TIMEOUT) {
        super(mdp);
        this.TIMEOUT = TIMEOUT;
    }

    public void Solve(){
        MCTSNode root = new MCTSNode(this.mdp, mdp.getInitialState());
        long start = System.currentTimeMillis();
        long elapsed_time;
        do {
            root.simulateRound(this.horizon);
            elapsed_time = System.currentTimeMillis() - start;
//        } while (true);
        } while (elapsed_time < this.TIMEOUT);
    }

    public static void main(String[] args) {
        MCTSSolver mctsSolver = new MCTSSolver(ParsePOMDP.readPOMDP("domains/tiger.aaai.POMDP"));
        mctsSolver.Solve();
    }
}
