package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 24-Oct-17.
 */
public class Actions {

    private ArrayList<Integer> sNext;
    private int actionNum;

    public Actions(int action) {
        this.actionNum = action;
        sNext = new ArrayList<>();
    }

    public Actions(int action, ArrayList<Integer> sNext) {
        this.actionNum = action;
        this.sNext = sNext;
    }

    public void addsNext(int sNext) {
        this.sNext.add(sNext);
    }

    public int getActionNum() {
        return this.actionNum;
    }

    public int getSizesNext() {
        return this.sNext.size();
    }

    public ArrayList<Integer> getsNext() {
        return sNext;
    }

    public String toString(){
        return this.actionNum + ", " + this.sNext.size();
    }

}
