package com.example.movesensehealthtrackerapp.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.movesensehealthtrackerapp.activity.ProgressReportActivity;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDBConnection{

    private  FirebaseFirestore fd;
    public FirebaseDBConnection() {
        fd = FirebaseFirestore.getInstance();
    }

    private static final String LOG_TAG = FirebaseDBConnection.class.getSimpleName();


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
        BalanceData balanceData = new BalanceData(Collections.max(accMovementList), Collections.min(accMovementList),
                calcAverage(accMovementList), new Timestamp(new Date()), accMovementList) ;

        fd.collection("balanceEx1_balance_score")
                .add(balanceData)
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


    public void getBalanceProgress(Context context, List<BalanceData> balanceDataList, ProgressReportActivity activity){
        fd.collection("balanceEx1_balance_score")
            .orderBy("Date_set")
            .limit(2)
            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : list) {
                            BalanceData balanceData = document.toObject(BalanceData.class);
                            balanceDataList.add(balanceData);
                        }
                        activity.initialiseChart();
                    } else {
                        // if the snapshot is empty we are displaying a toast message.
                        Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // if we do not get any data or any error we are displaying
                    // a toast message that we do not get any data
                    Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
                }
            });
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
