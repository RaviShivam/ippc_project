package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 24-Oct-17.
 */
public class Actions {

    private ArrayList<Integer> sNext;
    private int actionNum;

    public Actions(int action, ArrayList<Integer> sNext) {
        this.actionNum = action;
        this.sNext = sNext;
    }

    public String toString(){
        return this.actionNum + "," + this.sNext.toString();
    }

}
