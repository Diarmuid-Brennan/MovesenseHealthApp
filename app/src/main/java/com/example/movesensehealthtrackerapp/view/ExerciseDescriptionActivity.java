package com.example.movesensehealthtrackerapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.movesensehealthtrackerapp.R;

public class ExerciseDescriptionActivity extends BaseActivity {

    private String description;
    private TextView activityDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_description);
        Bundle extras = getIntent().getExtras();
        description = extras.getString("description");

        activityDescription = findViewById (R.id.tv_description);
        activityDescription.setText(description);
    }


}