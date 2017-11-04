package cache_performance;

import parser.POMDP;

import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by yuupv on 01-Nov-17.
 */
public class Partition {

    private int id;
    private ArrayList<State> stateList;
    private ArrayList<Integer> sNextList;
    private double maxPriority;
    private ArrayList<SDP> SDP;
    private ArrayList<Integer> PDP;
    private ArrayList<Double> HPP;


    public Partition(int id, ArrayList<State> stateList, int partitionAmount){
        this.id = id;
        this.stateList = stateList;

        //Save all sNext for easy access
        sNextList = initializesNextList();

        //Only need to do this once
        this.SDP = new ArrayList<>();
        this.PDP = new ArrayList<>();
        this.maxPriority = findMaxReward();
        initializeHPP(partitionAmount);
    }

    private ArrayList<Integer> initializesNextList() {
        ArrayList<Integer> temp = new ArrayList<>();

        for (int i = 0; i < getNumberOfStates(); i++) {
            temp.addAll(this.stateList.get(i).getSNextList());
        }
        return temp;
    }

    private double findMaxReward() {
        double max = 0.0;
        for (int i = 0; i < getPartitionSize() ; i++) {
            if(max < this.stateList.get(i).getMaxReward()) {
                max = this.stateList.get(i).getMaxReward();
            }
        }
        return max;
    }

    private void initializeHPP(int amount) {
        HPP = new ArrayList<>();
        for (int i = 0; i < amount ; i++) {
            HPP.add( (double) 0 );
        }
    }

    public void setHPP(int p, double amount) {
        HPP.set(p, amount);
    }

    public void findMaxHPP() {
        double max = 0.0;
        for (int i = 0; i < HPP.size() ; i++) {
            if(i != id && max < HPP.get(i)) {
                max = HPP.get(i);
            }
        }
        maxPriority = max;

    }

    public void setMaxPrioirity(double val) {
        maxPriority = val;
    }

    public void updateSDS(){
        // For each state in this partition
        for (int i = 0; i < getNumberOfStates() ; i++) {

            // For each
        }
    }

    private int getID() {
        return this.id;
    }

    public int getPartitionSize() {
        return this.stateList.size();
    }

    public double getMaxReward() {
        return maxPriority;
    }

    public int getNumberOfStates() {
        return this.stateList.size();
    }

    public ArrayList<State> getStateList() {
        return this.stateList;
    }

    public ArrayList<Integer> getAllSDS() {
        ArrayList<Integer> sNextList = new ArrayList<>();

        for (int i = 0; i < getNumberOfStates(); i++) {
            if(i == 0) {
                sNextList.addAll(stateList.get(i).getSDSList());
            } else {
                for (int j = 0; j < stateList.get(i).getSDSList().size(); j++) {
                    if(!sNextList.contains(stateList.get(i).getSDSList().get(j))){
                        sNextList.add(stateList.get(i).getSDSList().get(j));
                    }
                }
            }
        }
        return sNextList;
    }

    public ArrayList<Integer> getPDP() {
        return PDP;
    }

    public ArrayList<SDP> getSDP() {
        return SDP;
    }

    public void checkPartitionDependence(Partition p) {

        ArrayList<Integer> sds = getAllSDS();

        SDP sdp = new SDP(p.getID());

        for (int i = 0; i < sds.size(); i++) {

            for (int j = 0; j < p.getStateList().size(); j++) {

                if(sds.contains(p.getStateList().get(j).getStateNum())){
                    sdp.addState(p.getStateList().get(j));
                }

            }

        }
        if(!sdp.getStateList().isEmpty()){
            PDP.add(sdp.getPartitionNumber());
            SDP.add(sdp);
        }

    }

    public void printDependentPartitions() {

        for (int i = 0; i < SDP.size(); i++) {
            System.out.println("This p: " + getID() + " these partioins " + SDP.get(i).toString());
        }

    }

    public void printHPP() {
        System.out.println("P:" + id + " HPP: " + HPP.toString());
    }



}
