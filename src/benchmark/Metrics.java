package benchmark;

import cache_performance.Solver;

import java.io.PrintWriter;

public class Metrics {

    private long initialMemory;
    private long initialTime;
    private Runtime runTime;
    private Solver solver;
    private static final double MEGABYTE = 1024.0 * 1024.0;
    private static final double SECOND = 1000.0;

    public Metrics(Solver solver) {
        this.solver = solver;
        runTime = Runtime.getRuntime();
    }

    public static double bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

    public void tic() {
        this.runTime.gc();
        System.out.println();
        System.out.printf("===== Benchmarking %s algorithm on the %s problem =====", solver.getSolverName(), solver.getProblemName());
        System.out.println();
        initialTime = System.currentTimeMillis();
        initialMemory = runTime.totalMemory() - runTime.freeMemory();
    }

    public void toc(PrintWriter writer) {
        double memoryUsage = bytesToMegabytes(this.initialMemory - (runTime.totalMemory()-runTime.totalMemory()));
        double timeConsumption = (System.currentTimeMillis() - this.initialTime)/SECOND;
        System.out.printf("Memory consumption: %.3f MB", memoryUsage);
        System.out.println();
        System.out.printf("Time consumption: %.7f second", timeConsumption);
        System.out.println();
        System.out.println("====== Benchmark complete =======");
        System.out.println();
        writer.printf("%s; %s; %.3f; %.4f", solver.getSolverName(), solver.getProblemName(), memoryUsage, timeConsumption);
        writer.println();
        runTime.gc();
    }
}
