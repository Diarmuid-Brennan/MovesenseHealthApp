package com.example.movesensehealthtrackerapp.model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class AccData {
    private double x;
    private double y;
    private double z;

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setX(double x) {
        this.x = x;
    }


    public double getX() {return x; }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
