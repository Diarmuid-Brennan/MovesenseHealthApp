package com.example.movesensehealthtrackerapp.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceData {
    private double Max_Value;
    private double Min_Value;
    private com.google.firebase.Timestamp Date_set;
    private double Avg_Value;
   // private List<Double> accData;
    private boolean completed;
    private String activityName;

    public BalanceData(){

    }

    //public BalanceData(double max_value, double min_value, double avg_Value, Timestamp date_set, List<Double> accData, boolean completed, String activityName) {
    public BalanceData(double max_value, double min_value, double avg_Value, Timestamp date_set,  boolean completed, String activityName) {
        Max_Value = max_value;
        Min_Value = min_value;
        Date_set = date_set;
        Avg_Value = avg_Value;
        //this.accData = accData;
        this.completed = completed;
        this.activityName = activityName;
    }

    public double getMax_value() {
        return Max_Value;
    }

    public void setMax_value(float max_value) {
        Max_Value = max_value;
    }

    public double getMin_value() {
        return Min_Value;
    }

    public void setMin_value(float min_value) {
        Min_Value = min_value;
    }

    public Timestamp getDate_set() {
        return Date_set;
    }

    public void setDate_set(Timestamp date_set) {
        Date_set = date_set;
    }

    public double getAvg_Value() {
        return Avg_Value;
    }

    public void setAvg_Value(float avg_Value) {
        Avg_Value = avg_Value;
    }

//    public List<Double> getAccData() {
//        return accData;
//    }
//
//    public void setAccData(List<Double> accData) {
//        this.accData = accData;
//    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public  Map<String,Object> ConvertObjectToMap(List<BalanceData> balanceData) {
        Map<String,Object> list = new HashMap<String,Object>();
        Map<String, Object> scoreMap = new HashMap<String,Object>();

        for(int i =0; i< balanceData.size(); i++){

            scoreMap.put("activityName", balanceData.get(i).getActivityName());
            scoreMap.put("completed", balanceData.get(i).getCompleted());
            scoreMap.put("date_set", balanceData.get(i).getDate_set());
            scoreMap.put("avg_Value", balanceData.get(i).getAvg_Value());
            scoreMap.put("max_value", balanceData.get(i).getMax_value());
            scoreMap.put("min_value", balanceData.get(i).getMin_value());
            list.put(balanceData.get(i).getActivityName(),scoreMap);

        }
        return list;
    }

}
