package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.utils.Constant;


public class SplashActivity extends AppCompatActivity {

    @Override
    @SuppressWarnings("DEPRECATION")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        bAdapter.enable();

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                Intent intent = new Intent(getBaseContext(), BalanceExerciseActivity.class);
//                intent.putExtra("serial", "1");
//                intent.putExtra("time_limit", 15);
                startActivity(intent);
                finish();
            }
        },2500);

    }

}