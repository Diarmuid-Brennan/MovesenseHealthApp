package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.model.MyScanResult;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;

import java.util.ArrayList;
import java.util.List;

public class BalanceExerciseListActivity extends BaseActivity implements View.OnClickListener {

        private static final String LOG_TAG = BalanceExerciseListActivity.class.getSimpleName();
        private List<BalanceActivity> activities = new ArrayList<>();
        private String connectedSerial;

        private CustomButtonView activityDescription;
        private CustomButtonView beginActivity;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_balance_exercise_list);
            Bundle extras = getIntent().getExtras();
            connectedSerial = extras.getString(Constant.SERIAL);

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

            activityDescription = (CustomButtonView) findViewById(R.id.activity_desc);
            activityDescription.setOnClickListener(this);

            beginActivity = (CustomButtonView) findViewById(R.id.begin_exercise);
            beginActivity.setOnClickListener(this);

            //retrieveActivitiesFromDatabase();
        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_desc:
                Intent descIntent = new Intent(this, ActivityDescriptionActivity.class);
                startActivity(descIntent);
                break;
            case R.id.begin_exercise:
                Intent beginIntent = new Intent(this, BeginActivitiesActivity.class);
                beginIntent.putExtra(Constant.SERIAL, connectedSerial);
                startActivity(beginIntent);
                break;
            case R.id.view_progress:
                Intent progressIntent = new Intent(this, ProgressReportActivity.class);
                startActivity(progressIntent);
                break;
            default:
                break;
        }
    }
}