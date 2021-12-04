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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDBConnection {

    private  FirebaseFirestore fd;
    public FirebaseDBConnection() {
        fd = FirebaseFirestore.getInstance();
    }

    public void addHeartRateScoreToDB(List<Integer> ecgSampleDataList, Context context) {
        Map<String, Object> initialHeartRateScore = new HashMap<>();

        initialHeartRateScore.put("Max_Value", Collections.max(ecgSampleDataList));
        initialHeartRateScore.put("Min_Value", Collections.min(ecgSampleDataList));
        initialHeartRateScore.put("Avg_Value", calcAverage1(ecgSampleDataList));
        initialHeartRateScore.put("Date_set", new Timestamp(new Date()));
        initialHeartRateScore.put("ecgData", ecgSampleDataList);

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

    public void addBalanceScoreToDB(List<Double> accMovementList, Context context) {
        Map<String, Object> initialBalanceScore = new HashMap<>();

        initialBalanceScore.put("Max_Value", Collections.max(accMovementList));
        initialBalanceScore.put("Min_Value", Collections.min(accMovementList));
        initialBalanceScore.put("Avg_Value", calcAverage(accMovementList));
        initialBalanceScore.put("Date_set", new Timestamp(new Date()));
        initialBalanceScore.put("accData", accMovementList);

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

    public void addInitialHeartRateScoreToDB(List<Integer> ecgSampleDataList, Context context) {
        Map<String, Object> initialHeartRateScore = new HashMap<>();

        initialHeartRateScore.put("Max_Value", Collections.max(ecgSampleDataList));
        initialHeartRateScore.put("Min_Value", Collections.min(ecgSampleDataList));
        initialHeartRateScore.put("Avg_Value", calcAverage1(ecgSampleDataList));
        initialHeartRateScore.put("Date_set", new Timestamp(new Date()));
        initialHeartRateScore.put("ecgData", ecgSampleDataList);

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

    public void addInitialBalanceScoreToDB(List<Double> accMovementList, Context context) {
        Map<String, Object> initialBalanceScore = new HashMap<>();

        initialBalanceScore.put("Max_Value", Collections.max(accMovementList));
        initialBalanceScore.put("Min_Value", Collections.min(accMovementList));
        initialBalanceScore.put("Avg_Value", calcAverage(accMovementList));
        initialBalanceScore.put("Date_set", new Timestamp(new Date()));
        initialBalanceScore.put("accData", accMovementList);

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

    private double calcAverage(List<Double> accMovementList)
    {
        double sum = 0;
        for (double i : accMovementList) {
            sum+=i;
        }
        return sum/(double) accMovementList.size();
    }

    private double calcAverage1(List<Integer> accMovementList)
    {
        double sum = 0;
        for (int i : accMovementList) {
            sum+=i;
        }
        return sum/(double) accMovementList.size();
    }

//    public static double calculateAverage(List<T> accMovementList)
//    {
//        T sum = 0;
//        for (T i : accMovementList) {
//            sum+=i;
//        }
//        return sum/(double) accMovementList.size();
//    }

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
