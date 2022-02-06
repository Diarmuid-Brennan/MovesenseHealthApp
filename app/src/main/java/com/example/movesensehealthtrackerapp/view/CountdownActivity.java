package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.movesensehealthtrackerapp.R;

public class CountdownActivity extends AppCompatActivity {
    public int counter = 3;
    private ToneGenerator tg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics((dm));

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.4), (int)(height* 0.4));

        final TextView counttime=findViewById(R.id.counttime);
        new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 50000);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP,500);
                counttime.setText(String.valueOf(counter));
                counter--;

            }
            @Override
            public void onFinish() {
                counttime.setText("Begin");
                finish();
            }
        }.start();
    }
}