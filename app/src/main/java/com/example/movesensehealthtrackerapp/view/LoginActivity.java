/**
 * Diarmuid Brennan
 * 10/03/22
 * Login Activity - Allows user to log on to the application
 */
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

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.controller.LoginController;
import com.example.movesensehealthtrackerapp.model.User;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;
import com.example.movesensehealthtrackerapp.utils.TextViewBold;
import com.example.movesensehealthtrackerapp.utils.TextViewLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextViewBold tv_register;
    private CustomButtonView btn_login;
    private TextViewLight forgot_password;
    private FirebaseAuth mAuth;
    private FirebaseDBConnection firebaseDBConnection;
    private LoginController loginController;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        mAuth = FirebaseAuth.getInstance();
        firebaseDBConnection = new FirebaseDBConnection();
        loginController = new LoginController();

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        tv_register = findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        forgot_password = findViewById(R.id.tv_forgot_password);
        forgot_password.setOnClickListener(this);
    }

    /**
     * Authenticates the entered user details with firestore
     */
    private void loginInRegisteredUser() {
        String validatedEmail = email.getText().toString().trim();
        String validatedPassword = password.getText().toString().trim();
        if (loginController.validateLoginDetails(validatedEmail, validatedPassword)) {

            showProgressDialog(getString(R.string.please_wait));
            mAuth.signInWithEmailAndPassword(validatedEmail, validatedPassword)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "loginUserWithEmail:success");
                                firebaseDBConnection.getCurrentUserDetails(LoginActivity.this);
                            } else {
                                hideProgressDialog();
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "login:failure", task.getException());
                                showErrorSnackBar(task.getException().getMessage(), true);
                            }
                        }
                    });
        }
        else{
            showErrorSnackBar(getString(R.string.err_msg_enter_password), true);
        }
    }

    /**
     * Method is called after successful authentication
     * Brings the user to the main activity page
     * @param user - contains the details of the logged in user
     */
    public void userLoggedIn(User user){
        hideProgressDialog();
        Log.i("User Details: ", user.getFirstname() + " " + user.getLastName() + " " + user.getEmail());

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgot_password:
                Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                loginInRegisteredUser();
                break;
            case R.id.tv_register:
                Intent intent2 = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
