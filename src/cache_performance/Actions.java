package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 01-Nov-17.
 */
public class Actions {

    private int actionNum;
    private ArrayList<Integer> sNextList;

    public Actions(int action, ArrayList<Integer> sNextList) {
        this.actionNum = action;
        this.sNextList = sNextList;
    }

    public int getActionNum(){
        return actionNum;
    }

    public ArrayList<Integer> getsNextList() {
        return sNextList;
    }
}
