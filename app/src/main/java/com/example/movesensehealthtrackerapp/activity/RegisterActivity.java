package com.example.movesensehealthtrackerapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.movesensehealthtrackerapp.MainActivity;
import com.example.movesensehealthtrackerapp.R;
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

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseAuth mAuth;

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

        tv_login = findViewById(R.id.tv_login);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firstname = findViewById(R.id.et_first_name);
        lastname = findViewById(R.id.et_last_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        registerButton = findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
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
            //showProgressDialog();

            String validatedEmail = email.getText().toString().trim();
            String validatedPassword = password.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(validatedEmail, validatedPassword)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgressDialog();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                showErrorSnackBar("You have successfully registered.", false);

                                FirebaseAuth.getInstance().signOut();
                                finish();
                            } else {
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