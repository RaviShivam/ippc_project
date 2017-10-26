package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 24-Oct-17.
 */
public class Transition {

    private int state;
    public ArrayList<Actions> actions;
    private double reward;
    private ArrayList<Integer> feasibleSNext;

    public Transition(int state, ArrayList<Actions> actions) {
        this.state = state;
        this.actions = actions;
        this.reward = reward;
    }



    public int getState() {
        return this.state;
    }

    public ArrayList<Actions> getAction() {
        return this.actions;
    }

    //public int getsNext() {
    //    return this.sNext;
    //}

    //public String toString() {
    //    String temp = "[" + this.state + " , " + this.action + " , " + this.sNext + "]\n";
    //    return temp;
    //}

    public String toString() {
        String temp = "[" + this.state + " , " + this.actions.toString() + "]\n";
        return temp;
    }

    public double getReward() {
        return this.reward;
    }
}
