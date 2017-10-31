package cache_performance;

import java.util.ArrayList;

/**
 * Created by yuupv on 28-Oct-17.
 */
public class PDP {

    private int partNumber;
    private ArrayList<Integer> sNextList;

    public PDP(int paritionNumber, ArrayList<Integer> sNext) {
        this.partNumber = paritionNumber;
        this.sNextList = sNext;
    }

    public int getID() {
        return this.partNumber;
    }

    public ArrayList<Integer> getsNextList() {
        return this.sNextList;
    }

    public String toString() {
        return this.sNextList.toString();
    }





}
