package cache_performance;

import parser.POMDP;


import java.util.ArrayList;

public class VI_PP extends Solver {
    private double[]   vt;
    private ArrayList<ArrayList<Integer>> H;
    private ArrayList<Partition> partitions;

    private int solveP;

    //TODO:
    //Make possible action list
    //Make priority list
    //See if you need to run the whole priority

    public VI_PP() {

    }

    public VI_PP(POMDP mdp) {
        super(mdp);
        this.solverName = "VI_FeasibleActions";
    }

    public void Solve() {
        IntializeProblem();


    }

    public void solvePartition() {
        for (int i = 0; i < this.partitions.get(this.solveP).getPartition().size(); i++) {

        }
    }



    private double calculateValue(POMDP mdp, ArrayList<Integer> statesPossible, int state, int action) {
        double value, sum = 0.0;
        int sNext;
        //System.out.println(statesPossible.toString());
        for(sNext = 0; sNext < statesPossible.size(); sNext++) {
            sum = sum + mdp.getTransitionProbability(state, action, statesPossible.get(sNext))*getMaxQTablePrev(statesPossible.get(sNext));
        }
        value = mdp.getReward(state, action) + mdp.getDiscountFactor()*sum;
        //System.out.println("Value going into Qmatrix: " + value);
        return value;
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

    private double getDelta(double delta, int s, int a) {
        double d;
        d = Math.abs( this.qTable[s][a]) - Math.abs(this.qTablePrev[s][a]);
        if(delta < d) {
            return d;
        }
        return delta;
    }

    public void IntializeProblem(){
        initializeQTable();
        initializeH();
        this.partitions = createPartition();
        initializePDP();

        for (int i = 0; i <this.partitions.size() ; i++) {
            this.partitions.get(i).initializePriorityPartitions(this.partitions.size());
        }

        findMaxPriority();

        //System.out.println("Size of actionList: " + this.feasibleActions.getTotalFeasibleActions());
        //System.out.println("Size of actions possible: " + this.feasibleActions.getAllActions(0).getAmountOfActions());
    }

    public void initializeH() {
        this.H = new ArrayList<ArrayList<Integer>>();

        int s,a;
        ArrayList<Integer> maxReward = new ArrayList<Integer>(); //[State, Action]
        double maxR = -Double.MAX_VALUE;
        for(s = 0; s < this.mdp.getNumStates(); s++) {
            maxR = -Double.MAX_VALUE;
            maxReward.add(s);
            maxReward.add(0);
            for(a = 0; a < this.mdp.getNumActions(); a++) {
                if(maxR < this.mdp.getReward(s,a)) {
                    maxR = this.mdp.getReward(s, a);
                    maxReward.set(1, a);
                }
            }
            this.H.add(maxReward);
            maxReward.clear();
        }
    }

    //Wondering if this is not a waste of resource
    //TODO:
    // Make a couple of functions to make it easier to get values from the arrayarrayarraylist
    // Need to make this much clearer
    private ArrayList<Transition> sortFeasibleActions() {
        int s, a, sNext;
        ArrayList<Transition> feasibleActions = new ArrayList<Transition>();
        for(s = 0; s < this.mdp.getNumStates(); s++) {
            ArrayList<Actions> tempList = new ArrayList<>();

            for(a = 0; a < this.mdp.getNumActions(); a++) {
                ArrayList<Integer> temp = new ArrayList<>();
                for (sNext = 0; sNext < this.mdp.getNumStates(); sNext++) {
                    if(this.mdp.getTransitionProbability(s,a, sNext) > 0){
                        temp.add(sNext);
                    }
                }
                tempList.add(new Actions(a, temp));


            }
            Transition tran = new Transition(s, tempList);
            tempList.clear();
            feasibleActions.add(tran);
        }
        return feasibleActions;
    }

    //
    //
    //
    //
    //
    //
    private ArrayList<Partition> createPartition() {
        int partitionAmount = 10;
        ArrayList<Transition> fa = sortFeasibleActions();
        int iterationAmount = fa.size();
        System.out.println("Size of Transition table : " + iterationAmount);
        double partitionSize = (double) iterationAmount/partitionAmount;
        int minPartitionSize = (int) partitionSize;
        int leftOvers    = (int) Math.round( (double) (partitionSize - minPartitionSize)*partitionAmount);


        if(leftOvers + minPartitionSize*partitionAmount != iterationAmount) {
            System.out.println("Parition size is not correct. Something went wrong creating paritions");
            System.exit(0);
        }

        ArrayList<Partition> partitions = new ArrayList<>();
        for (int i = 0; i < partitionAmount; i++) {
            ArrayList<Transition> tempPart = new ArrayList<>();

            for (int j = 0; j < minPartitionSize; j++) {
                tempPart.add(fa.get(j + i*minPartitionSize));
                //System.out.println(fa.get(j*i) + " get this: " + (j + i*minPartitionSize));
            }
            Partition tempP = new Partition(tempPart, i);
            partitions.add(tempP);
            tempPart.clear();
        }

        for (int i = 0; i < leftOvers; i++) {
            partitions.get(partitions.size()-1).addToParitionList(fa.get(minPartitionSize + i));
        }

        return partitions;
    }

    private double getValueH(int i){
        return this.mdp.getReward(this.H.get(i).get(0),this.H.get(i).get(1));
    }

    private void initializeVt() {
        this.vt = new double[this.mdp.getNumStates()];
    }

    //PDP = Partition dependents of a Partition
    private void initializePDP(){
        for (int i = 0; i < this.partitions.size() ; i++) {
            for (int j = 0; j <this.partitions.size() ; j++) {
                this.partitions.get(i).comparePartitions(this.partitions.get(j));
            }
        }
    }

    private void findMaxPriority() {
        double max = 0;
        for (int i = 0; i < this.partitions.size() ; i++) {
            if(max < this.partitions.get(i).getPrioirty()) {
                max = this.partitions.get(i).getPrioirty();
                solveP = i;
            }
        }
    }

}





/**
 *
 * GraveYard
 *
 * /
 initializeQTable();
 initializeVt();
 this.actionList = sortFeasibleActions(this.mdp);
 // Make assert that discount factor has to be between 0-1
 int s,a;
 double sum;
 double delta = 1;
 int count = 0;
 while(delta > 0.0001) {
 delta = 0;
 count++;
 //Iterating over each State
 for(s = 0; s < this.mdp.getNumStates(); s++) {
 double bellman;
 //Iterating over each Action
 for(a = 0; a < this.actionList.get(s).size(); a++) {
 bellman = calculateValue(this.mdp, this.actionList.get(s).get(a), s, a);
 this.qTable[s][a] = bellman;

 delta = getDelta(delta, s, a);
 }
 }
 saveCurrentQMatrix();
 }
 //		mu.recordMemoryUsuage();
 //		//printQTable();
 //		System.out.format("The amount of cycles was: %d%n", count);

 System.out.println("PDP0: " + this.partitions.get(0).PDP.toString() + " statelist: " + this.partitions.get(0).getStatesList().toString() + "Partition size: " + this.partitions.get(0).partition.size() );
 System.out.println("PDP1: " + this.partitions.get(1).PDP.toString() + " statelist: " + this.partitions.get(1).getStatesList().toString() + "Partition size: " + this.partitions.get(1).partition.size());
 System.out.println("PDP2: " + this.partitions.get(2).PDP.toString() + " statelist: " + this.partitions.get(2).getStatesList().toString() + "Partition size: " + this.partitions.get(2).partition.size());
 System.out.println("PDP3: " + this.partitions.get(3).PDP.toString() + " statelist: " + this.partitions.get(3).getStatesList().toString() + "Partition size: " + this.partitions.get(3).partition.size());
 System.out.println("PDP4: " + this.partitions.get(4).PDP.toString() + " statelist: " + this.partitions.get(4).getStatesList().toString() + "Partition size: " + this.partitions.get(4).partition.size());
 System.out.println("PDP5: " + this.partitions.get(5).PDP.toString() + " statelist: " + this.partitions.get(5).getStatesList().toString() + "Partition size: " + this.partitions.get(5).partition.size());
 */