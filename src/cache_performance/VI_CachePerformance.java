package cache_performance;

import java.util.ArrayList;
import parser.*;

import static java.lang.Math.max;

public class VI_CachePerformance extends Solver {

	private int amountOfPartitions = 3;
	private ArrayList<Partition> partitions;
	private int solvePartion;

	//TODO:
	//Need to split up priorityList into paritions somehow.
	
	public VI_CachePerformance() {
	}
	
	public VI_CachePerformance(POMDP mdp) {
		super(mdp);
		this.solverName = "VI_CachePerformance";
	}

	public void Solve() {
		initializationSequence();
		Runtime runtime = Runtime.getRuntime();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		double delta = 1;
		int count = 0;
		this.log.startTimer();
		while(delta > 0.0001){
			if(this.log.getDeltaTime() < RECORDTIME) {
				delta = 0.0;
				count++;
				Partition p = partitions.get(solvePartion);
				solvPartition(p);
				updatePartitionPriority(p.getSDP());
				delta = maxHPP();
				this.log.updateTime();
			} else {
				this.log.resetSplit();
				double maxR = this.maxReward();
				memory = runtime.totalMemory() - runtime.freeMemory();
				this.log.addVIElement(maxR, count, bytesToMegabytes(memory));
			}
		}
		this.log.save("Cache_Performance");
		printQTable();
		System.out.println("This many cycles: " + count);

	}

	private void solvPartition(Partition p){

		for (int j = 0; j < p.getNumberOfStates() ; j++) {
			State s = p.getStateList().get(j);
			for (int k = 0; k < s.numActions(); k++) {
				double bellman = calculateBellman(s.getStateNum(), s.getActionNumber(k), s.getAction(k).getsNextList());
				qTable[s.getStateNum()][s.getActionNumber(k)] = bellman;
				s.setHT(bellman - qTablePrev[s.getStateNum()][s.getActionNumber(k)]); //This is wrong
			}

		}
		p.setMaxPrioirity(0.0);
		saveCurrentQMatrix();
	}

	private double solvState(State s, double max) {
		double hmax = max;
		for (int k = 0; k < s.numActions(); k++) {
			double bellman = calculateBellman(s.getStateNum(), s.getActionNumber(k), s.getAction(k).getsNextList());
			qTable[s.getStateNum()][s.getActionNumber(k)] = bellman;
			s.setHT(bellman - qTablePrev[s.getStateNum()][s.getActionNumber(k)]);
			hmax = max(max, s.getHT());
		}

		return hmax;
	}

	private double calculateBellman(int state, int action, ArrayList<Integer> sNextList) {
		double value, sum = 0.0;
		int sNext;
		for(sNext = 0; sNext < sNextList.size(); sNext++) {
			sum = sum + mdp.getTransitionProbability(state, action, sNextList.get(sNext))*getMaxQTablePrev(sNextList.get(sNext));
		}
		value = mdp.getReward(state, action) + mdp.getDiscountFactor()*sum;
		return value;
	}

	private void updatePartitionPriority(ArrayList<SDP> SDP) {

		ArrayList<Integer> PDP = new ArrayList<>();
		for (int i = 0; i < SDP.size(); i++) {
			PDP.add(SDP.get(i).getPartitionNumber());
		}

		// update partition priority for all dependent partitions
		//for all subset p' from PDP(P) do
		for (int i = 0; i < PDP.size(); i++) {
			Partition p = partitions.get(PDP.get(i));
			p.setHPP(solvePartion, (double) 0.0);
			p.setMaxPrioirity(0.0);

			double hmax = 0;

			for (int j = 0; j < SDP.get(i).getStateList().size(); j++) {
				hmax = max(hmax, solvState(SDP.get(i).getStateList().get(j), hmax));
			}
			p.setHPP(solvePartion, hmax);
			p.findMaxHPP();
			saveCurrentQMatrix();
		}
		solvePartion = findMaxPriority();

	}

	public void initializationSequence() {
		//Make states,actions and find all feasible actions
		ArrayList<State> states = initializeStates();
		initializeQTable();

		//Partitioning the states
		this.partitions = new ArrayList<>();
		partitionStates(states);
		this.solvePartion = initialPartition();

		//Finding dependencies between partitions
		initializeSDS();
	}

	public ArrayList<State> initializeStates() {
		int state, action, sNext;
		ArrayList<State> allStates = new ArrayList<>();

		//Cycle through each state
		for (state = 0; state < this.mdp.getNumStates(); state++) {

			ArrayList<Actions> actionList = new ArrayList<>();
			//Cycle through each action
			for (action = 0; action < this.mdp.getNumActions(); action++) {

				ArrayList<Integer> sNextList = new ArrayList<>();
				//For each possible sNext
				for (sNext = 0; sNext < this.mdp.getNumStates(); sNext++) {
					if (mdp.getTransitionProbability(state, action, sNext) > 0) {
						sNextList.add(sNext);
					}
				}
				Actions act = new Actions(action, sNextList);
				actionList.add(act);
			}
			State st = new State(state, actionList, this.mdp);
			allStates.add(st);
		}
		return allStates;
	}

	public void partitionStates(ArrayList<State> allStates) {
		int partitionSize;


		// If amount of states allow for an equal amount of states in each partition do this loop
		if( (allStates.size() % amountOfPartitions ) == 0 ) {
			partitionSize = allStates.size() / amountOfPartitions;

			for (int i = 0; i < amountOfPartitions; i++) {
				ArrayList<State> tempPartition = new ArrayList<>();
				for (int j = 0; j < partitionSize; j++) {
					tempPartition.add(allStates.get(i*partitionSize + j));
				}
				this.partitions.add(new Partition(i, tempPartition, amountOfPartitions));
			}
		} else {
			partitionSize = allStates.size() / amountOfPartitions;
			for (int i = 0; i < amountOfPartitions; i++) {
				ArrayList<State> tempPartition = new ArrayList<>();
				for (int j = 0; j < partitionSize; j++) {
					tempPartition.add(allStates.get(i*partitionSize + j));
				}
				this.partitions.add(new Partition(i, tempPartition, amountOfPartitions));
			}

			int leftOvers = allStates.size() - partitionSize*amountOfPartitions;


		}

	}

	public int initialPartition(){
		double max = 0.0;
		int p = 0;
		for (int i = 0; i < amountOfPartitions ; i++) {
			if(max < partitions.get(i).getMaxReward()) {
				max = partitions.get(i).getMaxReward();
				p = i;
			}
		}
		return p;
	}

	public void initializeSDS() {
		//For each partition
		for (int i = 0; i < amountOfPartitions; i++) {
			//Try every partition
			for (int j = 0; j < amountOfPartitions; j++) {
				if(i != j) {
					this.partitions.get(i).checkPartitionDependence(this.partitions.get(j));
				}
			}
		}
	}

	private double getMaxQTablePrev(int s) {
		double max = 0;

		for(int i = 0; i < this.mdp.getNumActions(); i++) {
			if(max < qTablePrev[s][i]) {
				max = qTablePrev[s][i];
			}
		}
		return max;
	}

	private void printQTable() {
		int s,a;

		for( s=0; s<this.mdp.getNumStates(); s++) {
			for( a=0; a<this.mdp.getNumActions(); a++) {
				System.out.format("%06.3f  ", qTable[s][a]);
			}
			System.out.format("%n");
		}
		System.out.format("%n%n%n");
	}

	private void saveCurrentQMatrix() {
		for(int s = 0; s < this.mdp.getNumStates(); s++) {
			for(int a = 0; a < this.mdp.getNumActions(); a++) {
				this.qTablePrev[s][a] = this.qTable[s][a];
			}
		}
	}

	private int findMaxPriority(){
		double max = 0.0;
		int p = 0;
		ArrayList<Double> temp = new ArrayList<>();
		for (int i = 0; i < partitions.size(); i++) {
			temp.add(partitions.get(i).getMaxReward());
			if(max < partitions.get(i).getMaxReward()) {
				max = partitions.get(i).getMaxReward();
				p = i;
			}
		}

		//System.out.println("Max Priority: " + p + " all max values: " + temp.toString());
		return p;
	}

	private double maxHPP() {
		double max = 0.0;
		for (int i = 0; i < partitions.size(); i++) {
			if(max < partitions.get(i).getMaxReward()) {
				max = partitions.get(i).getMaxReward();
			}
		}
		return max;

	}

	private void printAllHPP() {
		for (int i = 0; i < partitions.size(); i++) {
			partitions.get(i).printHPP();
		}
	}

}
