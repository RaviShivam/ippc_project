package benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;

/**
 * This class is a singleton
 * @author yuupv
 *
 */
public class MemoryUsage {
	
	private ArrayList<Integer> numbers = new ArrayList<Integer>();
	
	private static MemoryUsage mu = new MemoryUsage();
	
	private MemoryMXBean memoryMxBean;
	private java.lang.management.MemoryUsage memUsage;
	
	private MemoryUsage() {
		this.memoryMxBean = ManagementFactory.getMemoryMXBean();
		this.memUsage = memoryMxBean.getHeapMemoryUsage();
	}
	


	
	public static MemoryUsage getInstance() {
		return mu;
	}
	
	public void addValue(int i) {
		this.numbers.add(i);
	}
	
	public void printNumbers() {
		System.out.println("Thisi s numbers from singleton" +this.numbers.toString());
	}
	
	public void recordMemoryUsuage() {
	    java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
	    String smemUsed = df.format((double)memUsage.getUsed()/(1024 * 1024));
	    String smemMax = df.format((double)memUsage.getMax()/(1024 * 1024));
	    System.out.println("Heap memory usage (in MB): " + smemUsed + "/" + smemMax);
	}

	public Long getMemoryUsuage() {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		String smemUsed = df.format((double)memUsage.getUsed()/(1024 * 1024));
		return this.memUsage.getUsed();
	}
}
