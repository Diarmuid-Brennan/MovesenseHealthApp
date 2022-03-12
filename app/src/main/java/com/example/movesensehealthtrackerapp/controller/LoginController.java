/**
 * Diarmuid Brennan
 * 10/03/22
 * Controller class for the Login Activity
 */
package com.example.movesensehealthtrackerapp.controller;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.view.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginController {
    private static final String TAG = LoginController.class.getSimpleName();

    /**
     * Constructor
     */
    public LoginController(){
    }

    /**
     * Validates the data entered from the user
     * @param email - entered email
     * @param password -entered password
     * @return - boolean showing wheter the entered data has been validated
     */
    public boolean validateLoginDetails(String email, String password){
        if(TextUtils.isEmpty(email)){
            return false;
        }
        if(TextUtils.isEmpty(password) || password.length() < 6){
            return false;
        }
        return true;
    }

}
