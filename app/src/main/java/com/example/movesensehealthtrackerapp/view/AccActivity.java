package com.example.movesensehealthtrackerapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.BaseActivity;
import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.AccData;
import com.example.movesensehealthtrackerapp.model.HeartRate;
import com.example.movesensehealthtrackerapp.model.LinearAcceleration;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class AccActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void addHeartRateScoreToDB() {
        Map<String, Object> initialHeartRateScore = new HashMap<>();

        initialHeartRateScore.put("Max_Value", 0);
        initialHeartRateScore.put("Min_Value", 0);
        initialHeartRateScore.put("Avg_Value", 0);
        initialHeartRateScore.put("Date_set", new Timestamp(new Date()));
        initialHeartRateScore.put("rrData", rrDataList);
        initialHeartRateScore.put("bpmData", bpmDataList);

        // Add a new document with a generated ID
        fd.collection("initial_hr_score").document("Heart Rate Data")
                .set(initialHeartRateScore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AccActivity.this, "Updated database", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccActivity.this, "Updated database: failed", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    protected void addBalanceScoreToDB() {
        Map<String, Object> initialBalanceScore = new HashMap<>();

        initialBalanceScore.put("Max_Value", 0);
        initialBalanceScore.put("Min_Value", 0);
        initialBalanceScore.put("Avg_Value", 0);
        initialBalanceScore.put("Date_set", new Timestamp(new Date()));
        initialBalanceScore.put("accData", accDataList);

        // Add a new document with a generated ID
        fd.collection("initial_balance_score").document("Balance Data")
                .set(initialBalanceScore)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AccActivity.this, "Updated database", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccActivity.this, "Updated database: failed", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
