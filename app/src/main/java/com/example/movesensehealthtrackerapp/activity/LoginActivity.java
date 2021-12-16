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

import com.example.movesensehealthtrackerapp.MainActivity;
import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;
import com.example.movesensehealthtrackerapp.utils.TextViewBold;
import com.example.movesensehealthtrackerapp.utils.TextViewLight;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextViewBold tv_register;
    private CustomButtonView btn_login;
    private TextViewLight forgot_password;
    private FirebaseAuth mAuth;

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

        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        mAuth = FirebaseAuth.getInstance();

        tv_register = findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        forgot_password = findViewById(R.id.tv_forgot_password);
        forgot_password.setOnClickListener(this);
    }

    private void loginInRegisteredUser() {
        if (validateLoginDetails()) {

            showProgressDialog(getString(R.string.please_wait));
            String validatedEmail = email.getText().toString().trim();
            String validatedPassword = password.getText().toString().trim();


            mAuth.signInWithEmailAndPassword(validatedEmail, validatedPassword)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            hideProgressDialog();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "loginUserWithEmail:success");
                                showErrorSnackBar("You have successfully logged in", false);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                showErrorSnackBar(task.getException().getMessage(), true);
                            }
                        }
                    });
        }
    }


    private boolean validateLoginDetails(){
        if(TextUtils.isEmpty(email.getText().toString().trim())){
            showErrorSnackBar(getString(R.string.err_msg_enter_email), true);
            return false;
        }
        if(TextUtils.isEmpty(password.getText().toString().trim()) || password.getText().toString().trim().length() < 6){
            showErrorSnackBar(getString(R.string.err_msg_enter_password), true);
            return false;
        }
        return true;
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
                Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.putExtra("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent1.putExtra("email_id", email.getText());
                startActivity(intent1);
                finish();
            case R.id.tv_register:
                Intent intent2 = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
