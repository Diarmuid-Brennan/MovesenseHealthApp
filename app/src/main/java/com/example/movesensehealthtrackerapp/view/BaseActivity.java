package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.utils.TextViewLight;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

abstract class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progressBar;
    protected TextViewLight progress;
    protected boolean doubleClickToExit = false;


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

    public void hideProgressDialog(){
        progressBar.dismiss();
    }

    protected void doubleBackToExit(){
        if(doubleClickToExit){
            FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
            return;
        }

        this.doubleClickToExit = true;
        Toast.makeText(this, getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleClickToExit = false;
            }
        },2000);
    }
}