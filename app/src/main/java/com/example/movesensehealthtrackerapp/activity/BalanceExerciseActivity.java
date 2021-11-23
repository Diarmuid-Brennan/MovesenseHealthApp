package com.example.movesensehealthtrackerapp.activity;


import android.content.Context;
import android.os.Bundle;

import com.example.movesensehealthtrackerapp.BaseActivity;

public class BalanceExerciseActivity extends BaseActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }

    @Override
    protected void addScoreToDatabase() {
        firebaseDBConnection.addBalanceScoreToDB(accDataList, context);
        firebaseDBConnection.addHeartRateScoreToDB(rrDataList, bpmDataList, context);
    }

}