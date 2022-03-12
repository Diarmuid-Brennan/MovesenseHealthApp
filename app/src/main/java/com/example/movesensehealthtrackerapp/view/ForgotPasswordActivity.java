/**
 * Diarmuid Brennan
 * 10/03/22
 * Forgot Password Activity - User can change password
 * Uses firestore's in-built update password functionality over email
 */
package com.example.movesensehealthtrackerapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity {

    private Toolbar toolbar;
    private CustomButtonView submitButton;
    private TextInputEditText inputEmail;
    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setupBackFunction();
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

        inputEmail= findViewById(R.id.forgot_email);
        submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    showErrorSnackBar(getString(R.string.err_msg_enter_email), true);
                } else {
                    showProgressDialog(getString(R.string.please_wait));
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "EmailSuccessfullySent:success");

                                        Toast.makeText(ForgotPasswordActivity.this, "Email has been successfully sent.", Toast.LENGTH_LONG);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "emailUnsuccessful:failure", task.getException());
                                        showErrorSnackBar(task.getException().getMessage(), true);
                                    }

                                }
                            });
                }
            }
        });
    }

    /**
     * adds a button to the toolbar that returns the user to the previous activity
     */
    private void setupBackFunction(){
        toolbar = (Toolbar) findViewById(R.id.toolbar_forgot_password_activity);
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
}