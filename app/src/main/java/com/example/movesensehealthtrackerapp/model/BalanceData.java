/**
 * Diarmuid Brennan
 * 13/03/22
 * Balance Data - Contains the movement data gathered from a carried out activity
 */
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
    private double Avg_Value;
    private List<Double> accData;
    private boolean completed;
    private String activityName;

    public BalanceData(){

    }

    /**
     * Constructor
     * @param max_value - Activity max movement value
     * @param min_value - Activity min movement value
     * @param avg_value - Activity average movement value
     * @param date_set - Date activity set
     * @param accData - List of movement data from activity
     * @param completed - boolean if activity was successfully completed
     * @param activityName - Activities name
     */
    public BalanceData(double max_value, double min_value, double avg_value, String date_set, List<Double> accData, boolean completed, String activityName) {
        Max_Value = max_value;
        Min_Value = min_value;
        Date_set = date_set;
        Avg_Value = avg_value;
        this.accData = accData;
        this.completed = completed;
        this.activityName = activityName;
    }

    /**
     * Constructor
     * @param max_value - Activity max movement value
     * @param min_value - Activity min movement value
     * @param avg_value - Activity average movement value
     * @param date_set - Date activity set
     * @param completed - boolean if activity was successfully completed
     * @param activityName - Activities name
     */
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
