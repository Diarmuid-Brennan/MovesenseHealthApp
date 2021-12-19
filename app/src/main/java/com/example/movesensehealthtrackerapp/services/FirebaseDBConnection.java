package com.example.movesensehealthtrackerapp.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.movesensehealthtrackerapp.activity.BalanceExerciseActivity;
import com.example.movesensehealthtrackerapp.activity.LoginActivity;
import com.example.movesensehealthtrackerapp.activity.ProgressReportActivity;
import com.example.movesensehealthtrackerapp.activity.RegisterActivity;
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
import com.google.firebase.firestore.SetOptions;

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
                        activity.userRegistrationSuccess();
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

    public void addBalanceScoreToDB(List<Double> accMovementList, Context context, BalanceExerciseActivity activity) {
        BalanceData balanceData = new BalanceData(Collections.max(accMovementList), Collections.min(accMovementList),
                calcAverage(accMovementList), new Timestamp(new Date()), accMovementList) ;

        firestore.collection(Constant.BALANCE_EXERCISE1_SCORE)
                .document(getCurrentUserID())
                .collection(Constant.BALANCE_DATA)
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

    public void getBalanceProgress(Context context, List<BalanceData> balanceDataList, ProgressReportActivity activity){
        firestore.collection(Constant.BALANCE_EXERCISE1_SCORE)
                .document(getCurrentUserID())
                .collection(Constant.BALANCE_DATA)
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
