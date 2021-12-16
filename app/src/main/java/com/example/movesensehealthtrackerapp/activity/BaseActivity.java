package com.example.movesensehealthtrackerapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.utils.TextViewLight;
import com.google.android.material.snackbar.Snackbar;

abstract class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progressBar;
    protected TextViewLight progress;


    protected void showErrorSnackBar(String message, boolean error){
        Snackbar snackbar =Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();

        if(error){
            snackBarView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorSnackBarError));
        }
        else{
            snackBarView.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorSnackBarSuccess));
        }
        snackbar.show();
    }

    protected void showProgressDialog(String display){
        progressBar = new ProgressDialog(this);
        progressBar.setContentView(R.layout.dialog_progress);

//        progress = progressBar.findViewById(R.id.tv_progress_text);
//        progress.setText(display);
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.show();
    }

    protected void hideProgressDialog(){
        progressBar.dismiss();
    }
}