package com.example.movesensehealthtrackerapp.services;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetDataFromDB extends AsyncTask<Void, Void, Void> {

    private FirebaseFirestore fd;
    List<Float> result = new ArrayList<>();

    @Override
    protected Void doInBackground(Void... voids) {
        getBalanceProgress();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        returnProgress();
    }

    private List<Float> returnProgress(){
        return result;
    }

    private void getBalanceProgress(){

        fd.collection("balanceEx1_balance_score")
                .limit(2)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are adding
                            // our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Object o = d.get("Avg_Value");
                                Float r = ((Double) o).floatValue();
                                result.add(r);
                            }
                        }
                    }
                });
    }
}
