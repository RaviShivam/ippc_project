package mcts;

import cache_performance.Solver;
import parser.POMDP;
import parser.ParsePOMDP;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MCTSSolver extends Solver {
    private int TIMEOUT = 2000;
    private int horizon = 150;
    private static final double MEGABYTE = 1024.0 * 1024.0;


    public static int bytesToMegabytes(long bytes)
    {
        return (int)(bytes / MEGABYTE);
    }

    public MCTSSolver(POMDP mdp) {
        super(mdp);
        this.solverName = "MCTS Solver";
    }

    public MCTSSolver(POMDP mdp, int TIMEOUT) {
        super(mdp);
        this.TIMEOUT = TIMEOUT;
    }

    public void Solve(){
        Runtime runTime = Runtime.getRuntime();
        long initialMemory = runTime.totalMemory() - runTime.freeMemory();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter("results/mcts.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MCTSNode root = new MCTSNode(this.mdp, mdp.getInitialState());
        long start = System.currentTimeMillis();
        long elapsed_time;
        long prev_time=-1;
        do {
            elapsed_time = System.currentTimeMillis() - start;
            root.simulateRound(this.horizon);
            if(elapsed_time!=prev_time) {
                if (elapsed_time % 50 < 10) {
                    Runtime runtime = Runtime.getRuntime();
                    int memoryUsage = bytesToMegabytes(-initialMemory + (runtime.totalMemory() - runtime.freeMemory()));
                    System.out.print(memoryUsage + "  ");
                    writer.printf("%.1f, %d", (float) (elapsed_time), memoryUsage);
                    writer.println();
                }
            }
            prev_time = elapsed_time;
//        } while (true);
        } while (elapsed_time < this.TIMEOUT);
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) {
        MCTSSolver mctsSolver = new MCTSSolver(ParsePOMDP.readPOMDP("domains/tiger.aaai.POMDP"));
        mctsSolver.Solve();
    }
}
