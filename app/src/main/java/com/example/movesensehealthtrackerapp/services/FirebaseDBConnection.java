package com.example.movesensehealthtrackerapp.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.view.BalanceExerciseActivity;
import com.example.movesensehealthtrackerapp.view.BalanceExerciseListActivity;
import com.example.movesensehealthtrackerapp.view.BeginActivitiesActivity;
import com.example.movesensehealthtrackerapp.view.LoginActivity;
import com.example.movesensehealthtrackerapp.view.ProgressReportActivity;
import com.example.movesensehealthtrackerapp.view.RegisterActivity;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.example.movesensehealthtrackerapp.model.User;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    public void getBalanceActivities(BeginActivitiesActivity activity){
        List<BalanceActivity> activities = new ArrayList<>();
        firestore.collection(Constant.ACTIVITIES)
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

    public void checkActivities(BeginActivitiesActivity activity){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference docRef = firestore.collection(Constant.PATIENT_SCORES)
                //.document(getCurrentUserEmail())
                .document("malone@gmail.com")
                .collection(Constant.SCORES)
                .document(date);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        activity.doesDocumentExist(true);
                    } else {
                        activity.doesDocumentExist(false);
                        Log.d(TAG, "No such document");
                    }
                } else {
                    activity.hideProgressDialog();
                    Log.d(TAG, "get failed with ", task.getException());
                }
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


    public void addBalanceScoreListToDB(List<BalanceData> balanceScores, BeginActivitiesActivity activity) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Map<String,Object> map = new HashMap<String,Object>();
        map = balanceScores.get(0).ConvertObjectToMap(balanceScores);
        firestore.collection(Constant.PATIENT_SCORES)
                //.document(getCurrentUserEmail())
                .document("malone@gmail.com")
                .collection(Constant.SCORES)
                .document(date)
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        activity.resultsUploadedSuccess();
                        Log.d(TAG, "Document uploaded to database");
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

}
