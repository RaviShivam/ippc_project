package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 24-Oct-17.
 */
public class PossibleStates {

    private ArrayList<Integer> sNext;

    public PossibleStates() {
        this.sNext = new ArrayList<Integer>();
    }

    public void addState(int nextState) {
        this.sNext.add(nextState);
    }

    public int getAmountOfNextStates() {
        return this.sNext.size();
    }

    public int getSNext(int i){
        return this.sNext.get(i);
    }

}
