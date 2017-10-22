package benchmark;

import cache_performance.Solver;

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
        System.out.println();
        System.out.printf("===== Benchmarking %s algorithm on the %s problem =====", solver.getSolverName(), solver.getProblemName());
        System.out.println();
        initialTime = System.currentTimeMillis();
        initialMemory = runTime.totalMemory() - runTime.freeMemory();
    }

    public void toc() {
        long memoryUsage = this.initialMemory - (runTime.totalMemory()-runTime.totalMemory());
        long timeConsumption = System.currentTimeMillis() - this.initialTime;
        System.out.printf("Memory consumption: %.3f MB", bytesToMegabytes(memoryUsage));
        System.out.println();
        System.out.printf("Time consumption: %.3f second", timeConsumption/SECOND);
        System.out.println();
        System.out.println("====== Benchmark complete =======");
        System.out.println();
        runTime.gc();
    }
}
