package mcts;

import cache_performance.Solver;
import parser.POMDP;
import parser.ParsePOMDP;

public class MCTSSolver extends Solver {
    private int TIMEOUT = 2000;

    public MCTSSolver(POMDP mdp) {
        super(mdp);
    }

    public MCTSSolver(POMDP mdp, int TIMEOUT) {
        super(mdp);
        this.TIMEOUT = TIMEOUT;
    }

    public void solve(){
        MCTSNode root = new MCTSNode(this.mdp, mdp.getInitialState());
        int simround = 0;
        long start = System.currentTimeMillis();
        long elapsed_time;
        do {
            root.simulateRound();
            elapsed_time = System.currentTimeMillis() - start;
            simround++;
            System.out.println(simround);
        } while (true);
//        } while (elapsed_time < this.TIMEOUT);
    }

    public static void main(String[] args) {
        MCTSSolver mctsSolver = new MCTSSolver(ParsePOMDP.readPOMDP("domains/tiger.aaai.POMDP"));
        mctsSolver.solve();
    }
}
