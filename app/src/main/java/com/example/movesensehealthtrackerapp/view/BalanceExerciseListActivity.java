package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.movesensehealthtrackerapp.R;

    public class BalanceExerciseListActivity extends AppCompatActivity implements View.OnClickListener {

        //buttons
        private Button balanceExOne;
        Button two;
        Button three;
        Button four;

        private String connectedSerial;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_balance_exercise_list);
            Bundle extras = getIntent().getExtras();
            connectedSerial = extras.getString("serial");

            balanceExOne = (Button) findViewById(R.id.balanceEx1Button);
            balanceExOne.setOnClickListener(this); // calling onClick() method
            two = (Button) findViewById(R.id.gyroButton);
            two.setOnClickListener(this);
            three = (Button) findViewById(R.id.magnButton);
            three.setOnClickListener(this);
            four = (Button) findViewById(R.id.ecgButton);
            four.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.balanceEx1Button:
                    Intent ex1Intent = new Intent(this, BalanceExOneActivity.class);
                    ex1Intent.putExtra("serial", connectedSerial);
                    startActivity(ex1Intent);
                    break;
                case R.id.gyroButton:
                    Intent gyroIntent = new Intent(this, GyroActivity.class);
                    gyroIntent.putExtra("serial", connectedSerial);
                    startActivity(gyroIntent);
                    break;
                case R.id.magnButton:
                    Intent magnIntent = new Intent(this, MagnActivity.class);
                    magnIntent.putExtra("serial", connectedSerial);
                    startActivity(magnIntent);
                    break;
                case R.id.ecgButton:
                    Intent ecgIntent = new Intent(this, EcgActivity.class);
                    ecgIntent.putExtra("serial", connectedSerial);
                    startActivity(ecgIntent);
                    break;
                default:
                    break;
            }
        }
    }