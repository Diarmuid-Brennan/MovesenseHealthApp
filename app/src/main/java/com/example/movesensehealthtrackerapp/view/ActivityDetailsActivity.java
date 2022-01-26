package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;

public class ActivityDetailsActivity extends BaseActivity implements View.OnClickListener{

    private static final String LOG_TAG = ActivityDetailsActivity.class.getSimpleName();
    private BalanceActivity balanceActivity;
    private String connectedSerial;

    private CustomButtonView activityDetails;
    private CustomButtonView performExercise;
    private CustomButtonView viewProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Bundle extras = getIntent().getExtras();
        connectedSerial = extras.getString(Constant.SERIAL);
        balanceActivity = extras.getParcelable(Constant.PARSED);

        activityDetails = (CustomButtonView) findViewById(R.id.balance_description);
        activityDetails.setOnClickListener(this);

        performExercise = (CustomButtonView) findViewById(R.id.performExercise);
        performExercise.setOnClickListener(this);

        viewProgress = (CustomButtonView) findViewById(R.id.viewProgressButton);
        viewProgress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.balance_description:
                Intent descIntent = new Intent(this, ExerciseDescriptionActivity.class);
                descIntent.putExtra(Constant.DESCRIPTION, balanceActivity.getDescription());
                startActivity(descIntent);
                break;
            case R.id.performExercise:
                Intent exerciseIntent = new Intent(this, BalanceExerciseActivity.class);
                exerciseIntent.putExtra(Constant.SERIAL, connectedSerial);
                exerciseIntent.putExtra(Constant.TIME_LIMIT, balanceActivity.getTime_limit());
                exerciseIntent.putExtra(Constant.NAME, balanceActivity.getActivityName());
                startActivity(exerciseIntent);
                break;
            case R.id.viewProgressButton:
                Intent progressIntent = new Intent(this, ProgressReportActivity.class);
                progressIntent.putExtra(Constant.NAME, balanceActivity.getActivityName());
                startActivity(progressIntent);
                break;
            default:
                break;
        }
    }
}