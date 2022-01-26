package com.example.movesensehealthtrackerapp.view;


import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.EcgModel;
import com.example.movesensehealthtrackerapp.model.HeartRate;
import com.example.movesensehealthtrackerapp.model.LinearAcceleration;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BalanceExerciseActivity extends BaseActivity implements View.OnClickListener{
    // Sensor subscription
    private MdsSubscription mdsSubscription;

    public static Context context;
    //private final int MS_IN_SECOND = 1000;
    private static final String LOG_TAG = BalanceExerciseActivity.class.getSimpleName();
    private String connectedSerial;
    private int time_limit;
    private String activityName;
    private Mds mMds;

    //view
    private LineChart mChart;
    private TextView xAxisTextView;
    private TextView yAxisTextView;
    private TextView zAxisTextView;
    private TextView heartRateTextView;
    private TextView beatIntervalTextview;

    private List<Integer> ecgSampleDataList = new ArrayList<>();
    private List<Double> accMovementList = new ArrayList<>();
    private double[] previousValue = {0, 0, 0};
    private FirebaseDBConnection firebaseDBConnection;
    private Button exitButton;
    private long timestamp = 0;
    private long SetExerciseTimeLength;
    private ToneGenerator tg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        connectedSerial = extras.getString(Constant.SERIAL);

        //change to int and set to SetExerciseTimeLength
        activityName = extras.getString(Constant.NAME);
        time_limit = extras.getInt(Constant.TIME_LIMIT)*1000;
        SetExerciseTimeLength = time_limit;

        setContentView(R.layout.activity_acc);

        mChart = (LineChart) findViewById(R.id.linearAcc_lineChart);
        xAxisTextView = (TextView) findViewById(R.id.x_axis_textView);
        yAxisTextView = (TextView) findViewById(R.id.y_axis_textView);
        zAxisTextView = (TextView) findViewById(R.id.z_axis_textView);
        heartRateTextView = (TextView) findViewById(R.id.heartRateTextview);
        beatIntervalTextview = (TextView) findViewById(R.id.beatIntervalTextView);

        xAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        yAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        zAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);

        context = getApplicationContext();
        // Init Empty Chart
        mChart.setData(new LineData());
        mChart.getDescription().setText("Linear Acc");
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();

        firebaseDBConnection = new FirebaseDBConnection();
        tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        subscribeToSensors();
    }


    private void subscribeToSensors() {
        if (mdsSubscription != null) {
            unsubscribe();
        }
        final LineData mLineData = mChart.getData();
        ILineDataSet xSet = mLineData.getDataSetByIndex(0);

        if (xSet == null) {
            xSet = createSet(getString(R.string.data_x), getResources().getColor(android.R.color.holo_red_dark));
            mLineData.addDataSet(xSet);
        }

        StringBuilder sb = new StringBuilder();
        String strContract = sb.append(Constant.URI).append(connectedSerial).append(Constant.URI_MEAS_ACC_13).append(Constant.URI_CLOSING_BACKET).toString();
        Log.d(LOG_TAG, strContract);
        mdsSubscription = mMds.builder().build(this).subscribe(Constant.URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);

                        LinearAcceleration accResponse = new Gson().fromJson(data, LinearAcceleration.class);
                        if (accResponse != null && accResponse.body.array.length > 0) {
                            if(timestamp == 0) {
                                timestamp = accResponse.body.timestamp;
                            }else if(accResponse.body.timestamp > timestamp+SetExerciseTimeLength){
                                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                                timestamp = 0;
                                unsubscribe();
                                addScoreToDatabase();
                            }

                            LinearAcceleration.Array arrayData = accResponse.body.array[0];
                            xAxisTextView.setText(String.format(Locale.getDefault(),
                                    "x: %.6f", arrayData.x));
                            yAxisTextView.setText(String.format(Locale.getDefault(),
                                    "y: %.6f", arrayData.y));
                            zAxisTextView.setText(String.format(Locale.getDefault(),
                                    "z: %.6f", arrayData.z));

                            double currentValue[] = {arrayData.x, arrayData.y, arrayData.z};
                            double movement = Math.sqrt((currentValue[0] - previousValue[0]) * (currentValue[0] - previousValue[0])
                                    + (currentValue[1] - previousValue[1]) * (currentValue[1] - previousValue[1])
                                    + (currentValue[2] - previousValue[2]) * (currentValue[2] - previousValue[2])
                            );
                            previousValue = Arrays.copyOf(currentValue, currentValue.length);
                            mLineData.addEntry(new Entry(accResponse.body.timestamp / 100, (float) movement), 0);
                            accMovementList.add(movement);


                            mChart.notifyDataSetChanged();
                            mChart.setVisibleXRangeMaximum(Constant.DISPLAY_LIMIT);
                            mChart.moveViewToX(accResponse.body.timestamp / Constant.ONE_HUNDRED);
                        }
                    }

                    @Override
                    public void onError(MdsException e) {
                        Log.e(LOG_TAG, "subscription onError(): ", e);
                        unsubscribe();
                    }

                });
    }


    private void unsubscribe() {
        if (mdsSubscription != null) {
            mdsSubscription.unsubscribe();
            mdsSubscription = null;
        }
    }

    private LineDataSet createSet(String name, int color) {
        LineDataSet set = new LineDataSet(null, name);
        set.setLineWidth(2.5f);
        set.setColor(color);
        set.setDrawCircleHole(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(0f);
        return set;
    }

    public void onClick(View v) {
        onBackPressed();
    }

    private void addScoreToDatabase() {
        showProgressDialog(getString(R.string.please_wait));
        firebaseDBConnection.addBalanceScoreToDB(accMovementList, activityName, context, this);
    }

    public void resultsUploadedSuccess(){
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.balnce_results_uploaded_successfully), Toast.LENGTH_SHORT).show();
    }

}