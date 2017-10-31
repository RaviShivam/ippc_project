package cache_performance;

import parser.POMDP;


import java.util.ArrayList;

public class VI_PP extends Solver {
    private double[]   vt;
    private ArrayList<ArrayList<Integer>> H;
    private ArrayList<Partition> partitions;
    private ArrayList<Integer> priorityPartitions;


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
        InitialSequence();

        solvePartition();
        updatePartitionPriority();

    }

    public void InitialSequence(){
        initializeQTable();
        initializeH();
        ArrayList<Transition> fa = sortFeasibleActions();

        this.partitions = createPartition(fa);
        initializePDP();
        initializeHPP();


        for (int i = 0; i <this.partitions.size() ; i++) {
            this.partitions.get(i).initializePriorityPartitions(this.mdp);
        }
        findMaxPriority();
        //System.out.println("Starting on partition: " + this.solveP);

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

    private ArrayList<Transition> sortFeasibleActions() {
        int s, a, sNext;

        ArrayList<Transition> feasibleActions = new ArrayList<>();

        // For every state
        for(s = 0; s < this.mdp.getNumStates(); s++) {
            ArrayList<Actions> actionList = new ArrayList<>();
            // For each action in the state
            for(a = 0; a < this.mdp.getNumActions(); a++) {
                Actions actions = new Actions(a);
                // For each possible sNext state
                for (sNext = 0; sNext < this.mdp.getNumStates(); sNext++) {
                    // See if the transition is not 0
                    if(this.mdp.getTransitionProbability(s,a, sNext) > 0){
                        actions.addsNext(sNext);
                    }
                }
                actionList.add(actions);
            }
            Transition tran = new Transition(s, actionList);
            feasibleActions.add(tran);
        }

        return feasibleActions;
    }

    private ArrayList<Partition> createPartition(ArrayList<Transition> fa) {
        int partitionAmount = 10;

        int iterationAmount = fa.size();
        //System.out.println("Size of Transition table : " + iterationAmount);
        double partitionSize = (double) iterationAmount/partitionAmount;
        int minPartitionSize = (int) partitionSize;
        int leftOvers    = (int) Math.round( (double) (partitionSize - minPartitionSize)*partitionAmount);


        if(leftOvers + minPartitionSize*partitionAmount != iterationAmount) {
            //System.out.println("Parition size is not correct. Something went wrong creating paritions");
            System.exit(0);
        }

        ArrayList<Partition> partitions = new ArrayList<>();
        for (int i = 0; i < partitionAmount; i++) {
            ArrayList<Transition> tempPart = new ArrayList<>();

            for (int j = 0; j < minPartitionSize; j++) {
                tempPart.add(fa.get(j + i*minPartitionSize));
            }
            Partition tempP = new Partition(tempPart, i);
            partitions.add(tempP);

        }

        for (int i = 0; i < leftOvers; i++) {
            partitions.get(partitions.size() - leftOvers + i).addToParitionList(fa.get(minPartitionSize + i));
        }

        return partitions;
    }

    //PDP = Partition dependents of a Partition
    private void initializePDP(){
        for (int i = 0; i < this.partitions.size() ; i++) {
            for (int j = 0; j <this.partitions.size() ; j++) {
                this.partitions.get(i).comparePartitions(this.partitions.get(j));
            }
            //System.out.println("I am here: " + this.partitions.get(i).getPDP().get(0).toString());
        }
    }

    private void initializeHPP() {
        for (int i = 0; i < this.partitions.size() ; i++) {
            this.partitions.get(i).initializeHPP(this.partitions.size());

        }
    }

    private void findMaxPriority() {
        double max = 0;
        for (int i = 0; i < this.partitions.size() ; i++) {
            //System.out.println("Partition: " + i + " maxPriority: " + this.partitions.get(i).getPrioirty());
            if(max < this.partitions.get(i).getPrioirty()) {
                max = this.partitions.get(i).getPrioirty();
                solveP = i;
            }
        }
    }

    public void solvePartition() {
        double bellman;

        for (int i = 0; i < this.partitions.size() ; i++) {
            //System.out.println(i + ". " + this.partitions.get(i).getStatesList().toString());
        }

        //For each Transition in the partition
        for (int m = 0; m < 400; m++) {
            System.out.println("Solving this partition: " + this.solveP);
            this.partitions.get(this.solveP).setmaxH(0);
            ArrayList<Transition> solPartition = this.partitions.get(this.solveP).getPartition();
            for (int i = 0; i < solPartition.size(); i++) { // For each state -> solPartition.get(i).getState;
                int state = solPartition.get(i).getState();
                for (int j = 0; j < solPartition.get(i).getActionList().size(); j++) { //For each Action
                    Actions action = solPartition.get(i).getAction(j);
                        bellman = calculateValue(state, action.getActionNum(), action.getsNext());
                        this.qTable[state][action.getActionNum()] = bellman;
                    //System.out.println("Bellman should be: " + (bellman - this.qTablePrev[state][action.getActionNum()]) + " V(t): " + bellman);
                }


            }
            saveCurrentQMatrix();
            updatePartitionPriority();
            findMaxPriority();
        }

        printQTable();

    }

    private double calculateValue(int state, int action,  ArrayList<Integer> statesPossible) {
        double value, sum = 0.0;
        int sNext;
        //System.out.println(statesPossible.toString());
        if(statesPossible.isEmpty()) {
            System.out.println("Hello stranger");
            return 0;
        }

        for(sNext = 0; sNext < statesPossible.size(); sNext++) {
            sum = sum + this.mdp.getTransitionProbability(state, action, statesPossible.get(sNext))*getMaxQTablePrev(statesPossible.get(sNext));
        }
        value = this.mdp.getReward(state, action) + mdp.getDiscountFactor()*sum;
        //System.out.println("Value going into Qmatrix: " + value);
        return value;
    }

    private void updatePartitionPriority() {

        for (int i = 0; i < this.partitions.get(this.solveP).getPDP().size() ; i++) {
            this.partitions.get(this.partitions.get(this.solveP).getPDP().get(i).getID()).setHPP(this.solveP, 0);
        }

        for (int i = 0; i < this.partitions.get(this.solveP).getPDP().size(); i++) {
            int partitionNum = this.partitions.get(this.solveP).getPDP().get(i).getID();
            calculateHtsNext( partitionNum, this.partitions.get(this.solveP).getPDP().get(i).getsNextList() );
            this.partitions.get(partitionNum).findMaxHP();
            //System.out.println(i + "For partition: " + this.solveP + " I need to update these partitions: " + this.partitions.get(this.solveP).getPDP().get(i).getID() + " these states need to be recalculted: " + this.partitions.get(this.solveP).getPDP().get(i).toString());
        }

    }

    private void calculateHtsNext(int partition, ArrayList<Integer> sNextList) {
        double hmax = 0;
        //System.out.println(partition + "List of sNext for this partition: " + this.partitions.get(partition).getPartition().toString() + " sNextList: " + sNextList.toString());
        //Look into transition
        //For this partition recompute HT
        double value;

        for (int i = 0; i < sNextList.size() ; i++) {
            int maxAction = findPolicy(sNextList.get(i), this.mdp.getNumActions());
            value = calculateValue(sNextList.get(i), maxAction, this.partitions.get(partition).getSNextforState(sNextList.get(i), maxAction) );
            if(hmax < value - this.qTablePrev[sNextList.get(i)][maxAction]) {
                hmax = value - this.qTablePrev[sNextList.get(i)][maxAction];
            }
           // System.out.println("Value: " + (value - this.qTablePrev[sNextList.get(i)][maxAction]));
        }

        this.partitions.get(partition).setHPP(this.solveP, hmax);
    }

    private int findPolicy(int state, int numActions) {
        double max = 0;
        int action = 0;
        for (int i = 0; i < numActions ; i++) {
            if(this.qTable[state][i] > max) {
                max = this.qTable[state][i];
                action = i;
            }
        }
        return action;
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

    private int getPolicy(int state) {
        double max = 0;
        int action = 0;
        for (int i = 0; i < this.mdp.getNumActions() ; i++) {
            if(max < this.qTable[state][i]) {
                max = this.qTable[state][i];
                action = i;
            }
        }
        return action;
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

    private double getValueH(int i){
        return this.mdp.getReward(this.H.get(i).get(0),this.H.get(i).get(1));
    }

    private void initializeVt() {
        this.vt = new double[this.mdp.getNumStates()];
    }

    public void finishingSequence() {

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