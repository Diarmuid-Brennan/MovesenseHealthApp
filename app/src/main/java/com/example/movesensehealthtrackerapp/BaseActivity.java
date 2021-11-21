package com.example.movesensehealthtrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.model.AccData;
import com.example.movesensehealthtrackerapp.model.HeartRate;
import com.example.movesensehealthtrackerapp.model.LinearAcceleration;
import com.example.movesensehealthtrackerapp.view.AccActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    //    // Sensor subscription
    static private String URI_MEAS_ACC_13 = "/Meas/Acc/13";
    static private String HEART_RATE_PATH = "/Meas/hr";
    private MdsSubscription mdsSubscription;
    private MdsSubscription mdsSubscriptionHR;

    private static final String LOG_TAG = AccActivity.class.getSimpleName();
    private String connectedSerial;

    private Mds mMds;
    public static final String URI_CONNECTEDDEVICES = "suunto://MDS/ConnectedDevices";
    public static final String URI_EVENTLISTENER = "suunto://MDS/EventListener";
    public static final String SCHEME_PREFIX = "suunto://";

    //view
    private LineChart mChart;
    private TextView xAxisTextView;
    private TextView yAxisTextView;
    private TextView zAxisTextView;
    private TextView heartRateTextView;
    protected List<AccData> accDataList = new ArrayList<>();
    protected List<Integer> rrDataList = new ArrayList<>();
    protected List<Float> bpmDataList = new ArrayList<>();

    private Button exitButton;

    //Database
    protected FirebaseFirestore fd;
    protected DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        connectedSerial = extras.getString("serial");
        setContentView(R.layout.activity_acc);

        mChart = (LineChart) findViewById(R.id.linearAcc_lineChart);
        xAxisTextView = (TextView) findViewById(R.id.x_axis_textView);
        yAxisTextView = (TextView) findViewById(R.id.y_axis_textView);
        zAxisTextView = (TextView) findViewById(R.id.z_axis_textView);
        heartRateTextView = (TextView) findViewById(R.id.heartRateTextview);

        xAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        yAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        zAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(this);


        // Init Empty Chart
        mChart.setData(new LineData());
        mChart.getDescription().setText("Linear Acc");
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();

        fd = FirebaseFirestore.getInstance();


        subscribeToAccSensor();
        subscribeToHrSensor();
    }

    public void subscribeToHrSensor() {
        // Clean up existing subscription (if there is one)
        if (mdsSubscriptionHR != null) {
            unsubscribe();
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        StringBuilder sb = new StringBuilder();
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(HEART_RATE_PATH).append("\"}").toString();
        Log.d(LOG_TAG, strContract);

        mdsSubscriptionHR = mMds.builder().build(this).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);

                        HeartRate hrResponse = new Gson().fromJson(data, HeartRate.class);
                        if (hrResponse != null) {

                            heartRateTextView.setText(String.format(Locale.getDefault(),
                                    "RR [ms]: %d   Beat interval [bpm]: %.2f", hrResponse.body.rrData[0], hrResponse.body.average));

                            rrDataList.add(hrResponse.body.rrData[0]);
                            bpmDataList.add(hrResponse.body.average);
                        }
                    }

                    @Override
                    public void onError(MdsException e) {
                        Log.e(LOG_TAG, "subscription onError(): ", e);
                        unsubscribe();
                    }

                });

    }

    public void subscribeToAccSensor() {
        // Clean up existing subscription (if there is one)
        if (mdsSubscription != null) {
            unsubscribe();
        }

        final LineData mLineData = mChart.getData();

        ILineDataSet xSet = mLineData.getDataSetByIndex(0);
        ILineDataSet ySet = mLineData.getDataSetByIndex(1);
        ILineDataSet zSet = mLineData.getDataSetByIndex(2);

        if (xSet == null) {
            xSet = createSet("Data x", getResources().getColor(android.R.color.holo_red_dark));
            ySet = createSet("Data y", getResources().getColor(android.R.color.holo_green_dark));
            zSet = createSet("Data z", getResources().getColor(android.R.color.holo_blue_dark));
            mLineData.addDataSet(xSet);
            mLineData.addDataSet(ySet);
            mLineData.addDataSet(zSet);
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        StringBuilder sb = new StringBuilder();
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(URI_MEAS_ACC_13).append("\"}").toString();
        Log.d(LOG_TAG, strContract);

        mdsSubscription = mMds.builder().build(this).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);

                        LinearAcceleration accResponse = new Gson().fromJson(data, LinearAcceleration.class);
                        if (accResponse != null && accResponse.body.array.length > 0) {

                            LinearAcceleration.Array arrayData = accResponse.body.array[0];

                            xAxisTextView.setText(String.format(Locale.getDefault(),
                                    "x: %.6f", arrayData.x));
                            yAxisTextView.setText(String.format(Locale.getDefault(),
                                    "y: %.6f", arrayData.y));
                            zAxisTextView.setText(String.format(Locale.getDefault(),
                                    "z: %.6f", arrayData.z));

                            AccData newData = new AccData();
                            newData.setX(arrayData.x);
                            newData.setY(arrayData.y);
                            newData.setZ(arrayData.z);
                            accDataList.add(newData);

                            mLineData.addEntry(new Entry(accResponse.body.timestamp / 100, (float) arrayData.x), 0);
                            mLineData.addEntry(new Entry(accResponse.body.timestamp / 100, (float) arrayData.y), 1);
                            mLineData.addEntry(new Entry(accResponse.body.timestamp / 100, (float) arrayData.z), 2);
                            mLineData.notifyDataChanged();

                            // let the chart know it's data has changed
                            mChart.notifyDataSetChanged();

                            // limit the number of visible entries
                            mChart.setVisibleXRangeMaximum(50);

                            // move to the latest entry
                            mChart.moveViewToX(accResponse.body.timestamp / 100);
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
        if (mdsSubscriptionHR != null) {
            mdsSubscriptionHR.unsubscribe();
            mdsSubscriptionHR = null;
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

    @Override
    public void onClick(View v) {
        unsubscribe();
        addScoreToDatabase();
        //Intent balanceExListIntent = new Intent(this, BalanceExOneActivity.class);
        //startActivity(balanceExListIntent);
    }

//    private void exitButtonClicked(View view){
//        unsubscribe();
//        addScoreToDatabase();
//        //Intent balanceExListIntent = new Intent(this, BalanceExOneActivity.class);
//        //startActivity(balanceExListIntent);
//    }

    private float calculateMaxValue(){
        return 0;
    }

    private void addScoreToDatabase(){
        addBalanceScoreToDB();
        addHeartRateScoreToDB();
    }

    protected abstract void addHeartRateScoreToDB();

    protected abstract void addBalanceScoreToDB();
}