package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 25-Oct-17.
 */
public class Partition {

    private ArrayList<Transition> partition;
    public ArrayList<Integer> PDP = new ArrayList<>();
    private ArrayList<Integer> statesList = new ArrayList<>();
    //private ArrayList<Integer> sNextList = new ArrayList<>();
    private ArrayList<Integer> priorityPartitions;

    private int id;
    private double prioirty = 0;
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

    public void initializePriorityPartitions(int NumberOfPartitions) {
        this.priorityPartitions = new ArrayList<>();
        for (int i = 0; i < NumberOfPartitions ; i++) {
            this.priorityPartitions.add(0);
        }
        this.priorityPartitions.set(this.getID(), -1);
    }

    public void comparePartitions(Partition comP) {
        if(comP.getID() == this.id) {
            return;
        }

        boolean isSet = false;
        // Do partitions share states?
        for (int i = 0; i < this.statesList.size() ; i++) {
            for (int j = 0; j < comP.getStatesList().size() ; j++) {
                if(this.statesList.get(i) == comP.getStatesList().get(j)) {
                    isSet = true;
                }
            }
        }
        // Does nState exist in other Partition
        //for (int i = 0; i < this.sNextList.size() ; i++) {
        //    for (int j = 0; j < comP.getStatesList().size() ; j++) {
        //        if(this.sNextList.get(i) == comP.getStatesList().get(j)) {
        //            isSet = true;
        //        }
        //    }
        //}

        if(isSet) { this.PDP.add(comP.getID());}

    }

    public void addPDP(int id){
        boolean exist = false;
        if(this.PDP.size() > 0) {
            this.PDP.add(id);
        } else {
            for (int i = 0; i < this.PDP.size(); i++) {
                if(this.PDP.get(i) == id) {
                    exist = true;
                }
            }
        }
        if (!exist) {
            this.PDP.add(id);
        }
    }

    private void findMaxReward() {
        double reward = this.prioirty;
        for (int i = 0; i <this.partition.size(); i++) {
            if(reward < this.partition.get(i).getReward()) {
                reward = this.partition.get(i).getReward();
            }
        }
        this.prioirty = reward;
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

    /*
    private void addsNextToList(int state) {

        boolean inState = false;

        if(this.sNextList.size() > 0) {
            for (int i = 0; i <this.sNextList.size() ; i++) {
                if(this.sNextList.get(i) == state ) {
                    inState = true;
                }
            }
            if(!inState) {
                this.sNextList.add(state);
            }
        } else {
            this.sNextList.add(state);
        }
    }*/

    public ArrayList<Transition> getPartitionList() {
        return this.partition;
    }

    public ArrayList<Integer> getStatesList() {
        return this.statesList;
    }

    /*
    public ArrayList<Integer> getsNextList() {
        return this.sNextList;
    }*/

    public int getID() {
        return this.id;
    }

    public double getPrioirty() {
        return prioirty;
    }

    public ArrayList<Transition> getPartition() {
        return partition;
    }
}
