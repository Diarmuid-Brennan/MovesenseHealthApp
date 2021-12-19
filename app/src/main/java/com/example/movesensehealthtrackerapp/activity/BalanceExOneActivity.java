package com.example.movesensehealthtrackerapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.movesensehealthtrackerapp.R;

public class BalanceExOneActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = BalanceExOneActivity.class.getSimpleName();
    private String connectedSerial;

    private Button setInitialScore;
    private Button performExercise;
    private Button viewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_ex_one);
        Bundle extras = getIntent().getExtras();
        connectedSerial = extras.getString("serial");

        setInitialScore = (Button) findViewById(R.id.balance_description);
        setInitialScore.setOnClickListener(this);

        performExercise = (Button) findViewById(R.id.performExercise);
        performExercise.setOnClickListener(this);

        viewProgress = (Button) findViewById(R.id.viewProgressButton);
        viewProgress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balance_description:
                Intent accIntent = new Intent(this, ExerciseDescriptionActivity.class);
                startActivity(accIntent);
                break;
            case R.id.performExercise:
                Intent exerciseIntent = new Intent(this, BalanceExerciseActivity.class);
                exerciseIntent.putExtra("serial", connectedSerial);
                startActivity(exerciseIntent);
                break;
            case R.id.viewProgressButton:
                Intent progressIntent = new Intent(this, ProgressReportActivity.class);
                startActivity(progressIntent);
                break;
            default:
                break;
        }
    }
}