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
    private String Date_set;
    //private com.google.firebase.Timestamp Date_set;
    private double Avg_Value;
    private List<Double> accData;
    private boolean completed;
    private String activityName;

    public BalanceData(){

    }

    public BalanceData(double max_value, double min_value, double avg_value, String date_set, List<Double> accData, boolean completed, String activityName) {
    //public BalanceData(double max_value, double min_value, double avg_Value, Timestamp date_set,  boolean completed, String activityName) {
        Max_Value = max_value;
        Min_Value = min_value;
        Date_set = date_set;
        Avg_Value = avg_value;
        this.accData = accData;
        this.completed = completed;
        this.activityName = activityName;
    }

    public BalanceData(double max_value, double min_value, double avg_value, String date_set,  boolean completed, String activityName) {
        Max_Value = max_value;
        Min_Value = min_value;
        Date_set = date_set;
        Avg_Value = avg_value;
        this.accData = accData;
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

    public String getDate_set() {
        return Date_set;
    }

    public void setDate_set(String date_set) {
        Date_set = date_set;
    }

    public double getAvg_Value() {
        return Avg_Value;
    }

    public void setAvg_Value(float avg_Value) {
        Avg_Value = avg_Value;
    }

    public List<Double> getAccData() {
        return accData;
    }

    public void setAccData(List<Double> accData) {
        this.accData = accData;
    }

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


}
