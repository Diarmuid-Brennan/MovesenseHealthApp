package com.example.movesensehealthtrackerapp.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.view.BalanceExerciseActivity;
import com.example.movesensehealthtrackerapp.view.BalanceExerciseListActivity;
import com.example.movesensehealthtrackerapp.view.LoginActivity;
import com.example.movesensehealthtrackerapp.view.ProgressReportActivity;
import com.example.movesensehealthtrackerapp.view.RegisterActivity;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.example.movesensehealthtrackerapp.model.User;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDBConnection{

    private  FirebaseFirestore firestore;

    private static final String TAG = FirebaseDBConnection.class.getSimpleName();

    public FirebaseDBConnection() {
        firestore = FirebaseFirestore.getInstance();
    }


    public void registerUser(RegisterActivity activity, User user){
        firestore.collection(Constant.USER)
                .document(user.getUserUID())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Log.d(TAG, "User registered successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideProgressDialog();
                        Log.w(TAG, "Error registering user", e);
                    }
                });
    }


    public String getCurrentUserID(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = "";
        if(currentUser != null) currentUserID = currentUser.getUid();
        return currentUserID;
    }

    public String getCurrentUserEmail(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserEmail = "";
        if(currentUser != null) currentUserEmail = currentUser.getEmail();
        return currentUserEmail;
    }

    public void getBalanceActivities(BalanceExerciseListActivity activity){
        List<BalanceActivity> activities = new ArrayList<>();
        firestore.collection(Constant.PATIENT_ACTIVITIES)
                .document(getCurrentUserEmail())
                .collection(Constant.ACTIVITIES)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                BalanceActivity activityData = new BalanceActivity((String)document.get("name"), (String)document.get("description"), ((Long)document.get("time_limit")).intValue());
                                activities.add(activityData);
                            }
                            activity.progressRetrievedSuccess(activities);
                        } else {
                            activity.progressRetrievedFailed();
                            Log.d(TAG, "No data currently stored in database");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.hideProgressDialog();
                Log.w(TAG, "Error retrieving data", e);
            }
        });
    }

    public void getCurrentUserDetails(LoginActivity activity){
        firestore.collection(Constant.USER)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentReference) {
                        Log.d(TAG, "Retrieved user details: " + documentReference.getId());
                        User user = documentReference.toObject(User.class);
                        activity.userLoggedIn(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.hideProgressDialog();
                        Log.w(TAG, "Error retrieving user details", e);
                    }
                });
    }

    public void addBalanceScoreToDB(List<Double> accMovementList, String activityName, Context context, BalanceExerciseActivity activity) {
        BalanceData balanceData = new BalanceData(Collections.max(accMovementList), Collections.min(accMovementList),
                calcAverage(accMovementList), new Timestamp(new Date()), accMovementList) ;

        firestore.collection(Constant.PATIENT_ACTIVITIES)
            .document(getCurrentUserEmail())
            .collection(Constant.ACTIVITIES)
                .document(activityName)
                .collection(Constant.SCORES)
            .add(balanceData)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                activity.resultsUploadedSuccess();
                Log.d(TAG, "Document uploaded to database with ID: " + documentReference.getId());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.hideProgressDialog();
                Log.w(TAG, "Error adding document", e);
            }
        });

    }

    public void getBalanceProgress(Context context, List<BalanceData> balanceDataList, String activityName, ProgressReportActivity activity){
        firestore.collection(Constant.PATIENT_ACTIVITIES)
                .document(getCurrentUserEmail())
                .collection(Constant.ACTIVITIES)
                .document(activityName)
                .collection(Constant.SCORES)
                .orderBy(Constant.DATE_SET, Query.Direction.DESCENDING)
                .limit(7)
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
                            activity.progressRetrievedSuccess();
                        } else {
                            activity.progressRetrievedFailed();
                            Log.d(TAG, "No data currently stored in database");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.hideProgressDialog();
                Log.w(TAG, "Error retrieving data", e);
            }
        });
    }

    public void addHeartRateScoreToDB(List<Integer> ecgSampleDataList, Context context) {
        Map<String, Object> initialHeartRateScore = new HashMap<>();

        initialHeartRateScore.put("Max_Value", Collections.max(ecgSampleDataList));
        initialHeartRateScore.put("Min_Value", Collections.min(ecgSampleDataList));
        initialHeartRateScore.put("Avg_Value", calcAverage1(ecgSampleDataList));
        initialHeartRateScore.put("Date_set", new Timestamp(new Date()));
        initialHeartRateScore.put("ecgData", ecgSampleDataList);

        // Add a new document with a generated ID
        firestore.collection("balanceEx1_hr_score")
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



}
