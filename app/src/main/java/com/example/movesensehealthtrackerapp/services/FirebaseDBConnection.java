package com.example.movesensehealthtrackerapp.services;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.movesensehealthtrackerapp.activity.InitialBalanceActivity;
import com.example.movesensehealthtrackerapp.model.AccData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDBConnection {
    //Database
    private  FirebaseFirestore fd;
    private DatabaseReference dbRef;


    public FirebaseDBConnection() {
        fd = FirebaseFirestore.getInstance();
    }

    public void addHeartRateScoreToDB(List<Integer> rrDataList, List<Float> bpmDataList, Context context) {
        Map<String, Object> initialHeartRateScore = new HashMap<>();

        initialHeartRateScore.put("Max_Value", 0);
        initialHeartRateScore.put("Min_Value", 0);
        initialHeartRateScore.put("Avg_Value", 0);
        initialHeartRateScore.put("Date_set", new Timestamp(new Date()));
        initialHeartRateScore.put("rrData", rrDataList);
        initialHeartRateScore.put("bpmData", bpmDataList);

        // Add a new document with a generated ID
        fd.collection("balanceEx1_hr_score")
                .add(initialHeartRateScore)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Updated database", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Updated database: failed", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void addBalanceScoreToDB(List<AccData> accDataList, Context context) {
        Map<String, Object> initialBalanceScore = new HashMap<>();

        initialBalanceScore.put("Max_Value", 0);
        initialBalanceScore.put("Min_Value", 0);
        initialBalanceScore.put("Avg_Value", 0);
        initialBalanceScore.put("Date_set", new Timestamp(new Date()));
        initialBalanceScore.put("accData", accDataList);

        // Add a new document with a generated ID
        fd.collection("balanceEx1_balance_score")
                .add(initialBalanceScore)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, "Updated database", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Updated database: failed", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void addInitialHeartRateScoreToDB(List<Integer> rrDataList, List<Float> bpmDataList, Context context) {
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
                        Toast.makeText(context, "Updated database", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Updated database: failed", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void addInitialBalanceScoreToDB(List<AccData> accDataList, Context context) {
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
                        Toast.makeText(context, "Updated database", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Updated database: failed", Toast.LENGTH_SHORT).show();
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public List<Float> getBalanceProgress(){
        List<Float> result = new ArrayList<>();
        float num =0;
        for(int i = 0; i< 10; i++)
        {
            num +=  10;
            result.add(num);
        }
        return result;
    }

    public List<Integer> getHeartRateProgress(){
        List<Integer> result = new ArrayList<>();
        int num =0;
        for(int i = 0; i< 10; i++)
        {
            num +=  5;
            result.add(num);
        }
        return result;
    }


}
