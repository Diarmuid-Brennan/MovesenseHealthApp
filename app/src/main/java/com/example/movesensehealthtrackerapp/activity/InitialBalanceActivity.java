package com.example.movesensehealthtrackerapp.activity;

import androidx.annotation.NonNull;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.BaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InitialBalanceActivity extends BaseActivity {

    public static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

    }

    @Override
    protected void addScoreToDatabase() {
        firebaseDBConnection.addInitialBalanceScoreToDB(accDataList, context);
        firebaseDBConnection.addInitialHeartRateScoreToDB(rrDataList, bpmDataList, context);
    }

}
