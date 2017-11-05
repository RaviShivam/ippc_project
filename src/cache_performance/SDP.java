package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 03-Nov-17.
 */
public class SDP {

    private int partitionNumber;
    private ArrayList<State> state;

    public SDP(int partNum){
        this.partitionNumber = partNum;
        this.state = new ArrayList<>();
    }

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public void addState(State s) {
        if(!state.contains(s)){
            this.state.add(s);
        }

    }

    public ArrayList<State> getStateList() {
        return state;
    }

    public String toString() {
        String val = String.valueOf(partitionNumber);
        return "P: " + val + " S: " + state.toString();
    }

}
