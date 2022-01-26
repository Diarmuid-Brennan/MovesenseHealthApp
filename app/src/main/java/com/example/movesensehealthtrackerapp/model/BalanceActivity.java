package com.example.movesensehealthtrackerapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.List;

public class BalanceActivity implements Parcelable {
    private String name;
    private String description;
    private int time_limit;

    public BalanceActivity(){

    }

    public BalanceActivity(String name, String description, int time_limit) {
        this.name = name;
        this.description = description;
        this.time_limit = time_limit;
    }

    protected BalanceActivity(Parcel in) {
        name = in.readString();
        description = in.readString();
        time_limit = in.readInt();
    }

    public static final Creator<BalanceActivity> CREATOR = new Creator<BalanceActivity>() {
        @Override
        public BalanceActivity createFromParcel(Parcel in) {
            return new BalanceActivity(in);
        }

        @Override
        public BalanceActivity[] newArray(int size) {
            return new BalanceActivity[size];
        }
    };
    public String getDescription() {
        return description;
    }
    public int getTime_limit() {
        return time_limit;
    }
    public String getActivityName() {
        return name;
    }



    public String toString() {
        return ( name );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(time_limit);
    }
}
