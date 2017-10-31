package cache_performance;

import parser.POMDP;

import java.util.ArrayList;

/**
 * Created by yuupv on 25-Oct-17.
 */
public class Partition {

    public ArrayList<Transition> partition;
    public ArrayList<PDP> PDP = new ArrayList<>();
    public ArrayList<Double> HPP = new ArrayList<>();
    private ArrayList<Integer> statesList = new ArrayList<>();
    private ArrayList<Integer> priorityPartitions;

    private int id;
    //private double prioirty = 0;
    private double maxH = 0;


    public Partition(ArrayList<Transition> part, int id) {
        this.partition = part;
        findMaxReward();
        //System.out.println("Partition size: " + this.partition.size() + " This partition " + this.partition.toString());
        ArrayList<Integer> tempStates = new ArrayList<>();
        for (int i = 0; i < part.size(); i++) {
            tempStates.add(part.get(i).getState());
        }

        //System.out.println(id+". This is the partition: " + tempStates.toString());
        this.id = id;
        updateStatesList();
    }

    public void initializePriorityPartitions(POMDP mdp) {
        double maxReward = 0;

        for (int i = 0; i < this.partition.size(); i++) {
            for (int j = 0; j < this.partition.get(i).getActionList().size() ; j++) {
                if(maxReward < mdp.getReward(this.partition.get(i).getState(), this.partition.get(i).getAction(j).getActionNum())) {
                    //System.out.println("Max reward: " + maxReward);
                    maxReward = mdp.getReward(this.partition.get(i).getState(), this.partition.get(i).getAction(j).getActionNum());
                }
            }

        }
        this.maxH = maxReward;
    }

    public void comparePartitions(Partition comP) {
        if(comP.getID() == this.id) {
            return;
        }

        ArrayList<Integer> sNextList = new ArrayList<>();

        boolean isSet = false;
        // Do partitions share states?
        //for (int i = 0; i < this.statesList.size() ; i++) {
        //    for (int j = 0; j < comP.getStatesList().size() ; j++) {
        //        if(this.statesList.get(i) == comP.getStatesList().get(j)) {
        //            sNextList.add(this.statesList.get(i));
        //            isSet = true;
        //        }
        //    }
        //}

        // Does nState exist in other Partition

        //For every element in the partition
        for (int i = 0; i < this.partition.size(); i++) {
            //For every action in this partition
            for (int j = 0; j < this.partition.get(i).getActionList().size() ; j++) {
                // For every sNext in this partition
                for (int k = 0; k < this.partition.get(i).getAction(j).getSizesNext(); k++) {
                    for (int l = 0; l < comP.getStatesList().size(); l++) {
                        if(this.partition.get(i).getAction(j).getsNext().get(k) == comP.getStatesList().get(l)) {
                            sNextList.add(comP.getStatesList().get(l));
                            isSet = true;
                        }
                    }
                }

            }
        }

        //reduce sNext
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < sNextList.size(); i++) {
            if(temp.size() == 0) {
                temp.add(sNextList.get(i));
            } else {
                boolean same = false;
                for (int j = 0; j < temp.size(); j++) {
                    if(temp.get(j) == sNextList.get(i)) {
                        same = true;
                    }
                }
                if(!same) {
                    temp.add(sNextList.get(i));
                }
            }

        }


        if(isSet) { this.PDP.add(new PDP(comP.getID(), temp));}

    }

    public void initializeHPP(int size) {
        for (int i = 0; i < size ; i++) {
            this.HPP.add((double)0);
        }
        this.HPP.set(this.getID(), (double) -1);
    }

    public void setHPP(int partition, double val) {
        this.HPP.set(partition, val);
    }

    public void findMaxHP() {
        double max = 0;
        for (int i = 0; i < this.HPP.size() ; i++) {
            if(max < this.HPP.get(i)) {
                max = this.HPP.get(i);
            }
        }
        this.maxH = max;
    }

    private void findMaxReward() {
        double reward = this.maxH;
        for (int i = 0; i <this.partition.size(); i++) {
            if(reward < this.partition.get(i).getReward()) {
                reward = this.partition.get(i).getReward();
            }
        }
        this.maxH = reward;
    }

    public void setmaxH(double num) {
        this.maxH = num;
    }

    public void addToParitionList(Transition trans) {
        this.partition.add(trans);
        addStateToList(trans.getState());
        //addsNextToList(trans.getsNext());
    }

    private void updateStatesList() {
        for (int i = 0; i < partition.size(); i++) {
            addStateToList(this.partition.get(i).getState());
            //addsNextToList(this.partition.get(i).getsNext());
        }
    }

    private void addStateToList(int state) {

        boolean inState = false;

        if(this.statesList.size() > 0) {
            for (int i = 0; i <this.statesList.size() ; i++) {
                if(this.statesList.get(i) == state ) {
                    inState = true;
                }
            }
            if(!inState) {
                this.statesList.add(state);
            }
        } else {
            this.statesList.add(state);
        }
    }

    public ArrayList<Transition> getPartitionList() {
        return this.partition;
    }

    public ArrayList<Integer> getStatesList() {
        return this.statesList;
    }

    public int getID() {
        return this.id;
    }

    public double getPrioirty() {
        return this.maxH;
    }

    public ArrayList<Transition> getPartition() {
        return partition;
    }

    public String toString() {
        return this.partition.get(0).toString();
    }

    public ArrayList<PDP> getPDP() {
        return PDP;
    }

    public ArrayList<Integer> getSNextforState(int state, int actionNum) {

        for (int i = 0; i < this.statesList.size(); i++) {
            if(this.statesList.get(i) == state) {
                return this.partition.get(i).getAction(actionNum).getsNext();
            }
        }
        System.out.println("Shit fuck");
        return null;
    }
}
