package com.example.movesensehealthtrackerapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.movesensehealthtrackerapp.R;

public class BalanceExOneActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = InitialBalanceActivity.class.getSimpleName();
    private String connectedSerial;

    private Button setInitialScore;
    private Button performExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_ex_one);
        Bundle extras = getIntent().getExtras();
        connectedSerial = extras.getString("serial");

        setInitialScore = (Button) findViewById(R.id.setInitialScore);
        setInitialScore.setOnClickListener(this);

        performExercise = (Button) findViewById(R.id.performExercise);
        performExercise.setOnClickListener(this);// calling onClick() method


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setInitialScore:
                Intent accIntent = new Intent(this, InitialBalanceActivity.class);
                accIntent.putExtra("serial", connectedSerial);
                startActivity(accIntent);
                break;
            case R.id.performExercise:
                Intent exerciseIntent = new Intent(this, BalanceExerciseActivity.class);
                exerciseIntent.putExtra("serial", connectedSerial);
                startActivity(exerciseIntent);
                break;
            default:
                break;
        }
    }
}