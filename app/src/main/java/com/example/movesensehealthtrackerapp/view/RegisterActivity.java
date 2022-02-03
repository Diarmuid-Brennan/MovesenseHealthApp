package com.example.movesensehealthtrackerapp.view;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.User;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;
import com.example.movesensehealthtrackerapp.utils.TextViewBold;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;



public class RegisterActivity extends BaseActivity {

    private TextViewBold tv_login;
    private Toolbar toolbar;
    private TextInputEditText firstname;
    private TextInputEditText lastname;
    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
    private CustomButtonView registerButton;
    private FirebaseUser user;

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseDBConnection firebaseDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        setupBackFunction();
        firstname = findViewById(R.id.et_first_name);
        lastname = findViewById(R.id.et_last_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        registerButton = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseDBConnection = new FirebaseDBConnection();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupBackFunction(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_register_activity);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_keyboard_arrow_left_24);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(getString(R.string.please_wait));

            String validatedEmail = email.getText().toString().trim();
            String validatedPassword = password.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(validatedEmail, validatedPassword)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                user = mAuth.getCurrentUser();

                                User newUser = new User(
                                        user.getUid(),
                                        firstname.getText().toString().trim(),
                                        lastname.getText().toString().trim(),
                                        email.getText().toString().trim()
                                );
                                firebaseDBConnection.registerUser(RegisterActivity.this, newUser);
                            } else {
                                hideProgressDialog();
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                showErrorSnackBar(task.getException().getMessage(), true);
                            }
                        }
                    });
        }
    }



    private boolean validateRegisterDetails(){
        if(TextUtils.isEmpty(firstname.getText().toString().trim()) || firstname.getText().toString().trim().length() < 3){
            showErrorSnackBar(getString(R.string.err_msg_enter_first_name), true);
            return false;
        }
        if(TextUtils.isEmpty(lastname.getText().toString().trim())){
            showErrorSnackBar(getString(R.string.err_msg_enter_last_name), true);
            return false;
        }
        if(TextUtils.isEmpty(email.getText().toString().trim())){
            showErrorSnackBar(getString(R.string.err_msg_enter_email), true);
            return false;
        }
        if(TextUtils.isEmpty(password.getText().toString().trim()) || password.getText().toString().trim().length() < 6){
            showErrorSnackBar(getString(R.string.err_msg_enter_password), true);
            return false;
        }
        if(TextUtils.isEmpty(confirmPassword.getText().toString().trim())){
            showErrorSnackBar(getString(R.string.err_msg_enter_confirm_password), true);
            return false;
        }
        if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
            showErrorSnackBar(getString(R.string.err_msg_password_and_confirm_password_mismatch), true);
            return false;
        }
        return true;
    }
}