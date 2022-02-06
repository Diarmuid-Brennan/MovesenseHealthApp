package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.utils.Constant;

public class DisplayMessageActivity extends AppCompatActivity {
    private String message;
    private String heading;
    private TextView displayMessage;
    private TextView displayHeading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Bundle extras = getIntent().getExtras();
        message = extras.getString("message");
        heading = extras.getString("heading");


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics((dm));

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.6), (int)(height* 0.6));

        displayHeading = (TextView) findViewById(R.id.tv_heading);
        displayHeading.setText(heading);

        displayMessage = (TextView) findViewById(R.id.displayMessage);
        displayMessage.setText(message);
    }
}