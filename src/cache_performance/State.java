package cache_performance;

import parser.POMDP;

import java.util.ArrayList;

/**
 * Created by yuupv on 01-Nov-17.
 */
public class State {

    private int stateNum;
    private POMDP mdp;
    private ArrayList<Actions> actionList;
    private double maxReward;
    private ArrayList<SDP> SDP;
    private double ht;

    public State(int state, ArrayList<Actions> actionList, POMDP mdp) {
        this.stateNum = state;
        this.actionList = actionList;
        this.mdp = mdp;
        this.ht = 0.0;

        findMaxReward();
    }

    public void findMaxReward() {
        double max = 0.0;
        for (int i = 0; i < numActions(); i++) {
            if(max < this.mdp.getReward(stateNum, getActionNumber(i))) {
                max = this.mdp.getReward(stateNum, getActionNumber(i));
            }
        }

        this.maxReward = max;
    }

    public double getMaxReward() {
        return this.maxReward;
    }

    public int getStateNum() {
        return this.stateNum;
    }

    public int getActionNumber(int i) {
        return this.actionList.get(i).getActionNum();
    }

    public Actions getAction(int i) {
        return this.actionList.get(i);
    }

    public int numActions() {
        return this.actionList.size();
    }

    public ArrayList<Integer> getSNextList() {
        ArrayList<Integer> temp = new ArrayList<>();

        for (int i = 0; i < numActions(); i++) {
            temp.addAll(this.actionList.get(i).getsNextList());
        }
        return temp;
    }

    // The State Dependents of a State is known as a SDS
    // Actions have a list of all possible sNext and adding all
    // sNext into one list will create for this state the SDS
    public ArrayList<Integer> getSDSList() {
        ArrayList<Integer> SDS = new ArrayList<>();

        for (int i = 0; i < numActions(); i++) {
            SDS.addAll(this.actionList.get(i).getsNextList());
        }
        return SDS;
    }

    public ArrayList<SDP> getSDP() {
        return SDP;
    }

    public void setHT(double val) {
        ht = val;
    }

    public double getHT(){
        return ht;
    }


}
