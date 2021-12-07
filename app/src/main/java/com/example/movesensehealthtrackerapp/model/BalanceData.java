package com.example.movesensehealthtrackerapp.model;

import com.google.firebase.Timestamp;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class BalanceData {
    private double Max_Value;
    private double Min_Value;
    private com.google.firebase.Timestamp Date_set;
    private double Avg_Value;
    private List<Double> accData;

    public BalanceData(){

    }

    public BalanceData(double max_value, double min_value, double avg_Value, Timestamp date_set, List<Double> accData) {
        Max_Value = max_value;
        Min_Value = min_value;
        Date_set = date_set;
        Avg_Value = avg_Value;
        this.accData = accData;
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

    public List<Double> getAccData() {
        return accData;
    }

    public void setAccData(List<Double> accData) {
        this.accData = accData;
    }
}
