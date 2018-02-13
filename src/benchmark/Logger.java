package benchmark;


import java.time.Instant;
import java.util.ArrayList;

public class Logger {

    private long startTime;
    private long splitTime;
    private long endTime;
    private long deltaTime;

    private ArrayList<String> data;

    public Logger() {
        this.startTime = 0;

        this.endTime = 0;
        this.deltaTime = 0;

        this.data = new ArrayList<>();
    }

    public void startTimer() {
        this.startTime = Instant.now().toEpochMilli();
        this.splitTime = Instant.now().toEpochMilli();
    }

    public void updateTime() {
        this.deltaTime = Instant.now().toEpochMilli() - this.splitTime;
    }

    public void resetSplit() {
        this.splitTime = Instant.now().toEpochMilli();
        this.deltaTime = 0;
    }

    public long getDeltaTime() {
        return this.deltaTime;
    }

    public void addVIElement(double reward, int cycles, Long mem) {
        String res;

        long t = Instant.now().toEpochMilli() - this.startTime;
        res = t + "," + reward + "," + cycles + "," + mem;

        this.data.add(res);

    }
    public void addMCTSElement(double r, double mem) {
        String res;
        long t = Instant.now().toEpochMilli() - this.startTime;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        res = t + "," + df.format(r) + "," + df.format(mem);
        this.data.add(res);

    }

    public void save(String filename) {
        CSVFile.writeCSVFile(filename, this.data);
    }

}
