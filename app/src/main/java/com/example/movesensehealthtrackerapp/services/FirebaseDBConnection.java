package com.example.movesensehealthtrackerapp.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.movesensehealthtrackerapp.activity.InitialBalanceActivity;
import com.example.movesensehealthtrackerapp.activity.ProgressReportActivity;
import com.example.movesensehealthtrackerapp.model.AccData;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


//        Map<String, Object> initialBalanceScore = new HashMap<>();
//
//        initialBalanceScore.put("Max_Value", Collections.max(accMovementList));
//        initialBalanceScore.put("Min_Value", Collections.min(accMovementList));
//        initialBalanceScore.put("Avg_Value", calcAverage(accMovementList));
//        initialBalanceScore.put("Date_set", new Timestamp(new Date()));
//        initialBalanceScore.put("accData", accMovementList);
//
//        // Add a new document with a generated ID
//        fd.collection("balanceEx1_balance_score")
//                .add(initialBalanceScore)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(context, "Updated database", Toast.LENGTH_SHORT).show();
//                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(context, "Updated database: failed", Toast.LENGTH_SHORT).show();
//                        //Log.w(TAG, "Error adding document", e);
//                    }
//                });
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

    public List<BalanceData> getBalanceProgress(Context context){
        List<BalanceData> balanceDataList = new ArrayList<>();
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
        return balanceDataList;
    }

//    public static double calculateAverage(List<T> accMovementList)
//    {
//        T sum = 0;
//        for (T i : accMovementList) {
//            sum+=i;
//        }
//        return sum/(double) accMovementList.size();
//    }


//    public List<Float> getBalanceProgress(Context context, ProgressReportActivity.IQuery iQuery){
//        List<Float> result = new ArrayList<>();
//
//        fd.collection("balanceEx1_balance_score")
//                .limit(2)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Object o = document.get("Avg_Value");
//                                Float r = ((Double) o).floatValue();
//                                result.add(r);
//                                iQuery.onSuccess(result);
//                            }
//                        } else {
//                            Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        return result;
//    }

//    public List<Float> getBalanceProgress1(Context context){
//        List<Float> result = new ArrayList<>();
//
//        //Task<QuerySnapshot> querySnapshotTask =
//        fd.collection("balanceEx1_balance_score")
//                .limit(2)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        // after getting the data we are calling on success method
//                        // and inside this method we are checking if the received
//                        // query snapshot is empty or not.
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            // if the snapshot is not empty we are adding
//                            // our data in a list.
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for (DocumentSnapshot d : list) {
//                                // after getting this list we are passing
//                                // that list to our object class.
//                                Object o = d.get("Avg_Value");
//                                Float r = ((Double) o).floatValue();
//                                result.add(r);
//                            }
//                        } else {
//                            // if the snapshot is empty we are displaying a toast message.
//                            Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // if we do not get any data or any error we are displaying
//                        // a toast message that we do not get any data
//                        Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
//                    }
//                });
////                .addOnCompleteListener(task -> {
////                    if (task.isSuccessful()) {
////                        for (QueryDocumentSnapshot document : task.getResult()) {
//////                            BalanceData data = document.toObject(BalanceData.class);
//////                            result.add(data.getAvg_Value());
////                            Object d = document.get("Avg_Value");
////                            Float r = ((Double) d).floatValue();
////                            result.add(r) ;
////                            //Log.d(TAG, document.getId() + " => " + document.getData());
////                        }
////                    } else {
////                        Toast.makeText(context, "Error getting documents", Toast.LENGTH_SHORT).show();
////                        //Log.d(TAG, "Error getting documents: ", task.getException());
////                    }
////                });
//        return result;
//    }

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

    public List<Float> getBalanceProgress2(){
        List<Float> result = new ArrayList<>();
        float num =0;
        for(int i = 0; i< 10; i++)
        {
            num +=  10;
            result.add(num);
        }
        return result;
    }

}
