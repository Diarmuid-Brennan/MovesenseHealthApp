package com.example.movesensehealthtrackerapp.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.example.movesensehealthtrackerapp.model.LinearAcceleration;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BeginActivitiesActivity extends BaseActivity implements View.OnClickListener{

    // Sensor subscription
    private MdsSubscription mdsSubscription;
    public static Context context;
    private int time_limit;
    private String activityName;
    private Mds mMds;
    private boolean activityCancelled = false;
    private boolean activityCompleted = false;

    //view
    private LineChart mChart;
    private TextView xAxisTextView;
    private TextView yAxisTextView;
    private TextView zAxisTextView;
    private TextView displayActName;
    private Button startActivity;

    private List<Integer> ecgSampleDataList = new ArrayList<>();
    private List<Double> accMovementList = new ArrayList<>();
    private double[] previousValue = {0, 0, 0};
    private Button exitButton;
    private long timestamp = 0;
    private long SetExerciseTimeLength;
    private ToneGenerator tg;
    private int activityListPosition = 0;

    private FirebaseDBConnection firebaseDBConnection;
    private static final String LOG_TAG = BeginActivitiesActivity.class.getSimpleName();

    private List<BalanceActivity> activities = new ArrayList<>();
    private List<BalanceData> balanceResults = new ArrayList<>();
    private String connectedSerial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_activities);
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
        firebaseDBConnection = new FirebaseDBConnection();
        SetExerciseTimeLength = 10;

        mChart = (LineChart) findViewById(R.id.linearAcc_lineChart);
        xAxisTextView = (TextView) findViewById(R.id.x_axis_textView);
        yAxisTextView = (TextView) findViewById(R.id.y_axis_textView);
        zAxisTextView = (TextView) findViewById(R.id.z_axis_textView);
        displayActName = (TextView) findViewById(R.id.activity_name);

        xAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        yAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        zAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        startActivity = (Button) findViewById(R.id.startActivity);
        startActivity.setOnClickListener(this);
        startActivity.setEnabled(false);

        context = getApplicationContext();
        checkIfActivityAlreadyCompleted();

    }

    private void checkIfActivityAlreadyCompleted(){
        showProgressDialog(getString(R.string.please_wait));
        firebaseDBConnection.checkActivities(this);
    }

    private void initialiseChart() {
        // Init Empty Chart
        mChart.setData(new LineData());
        mChart.getDescription().setText(activities.get(activityListPosition).getActivityName());
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();


    }

    public void doesDocumentExist(boolean exists){
        hideProgressDialog();
        if(exists){
            Toast.makeText(this, getString(R.string.activities_completed), Toast.LENGTH_SHORT).show();
        }
        else{
            retrieveActivitiesFromDatabase();
        }
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
                            }else if(accResponse.body.timestamp > timestamp+time_limit){
                                timestamp = 0;
                                tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 50000);
                                tg.startTone(ToneGenerator.TONE_PROP_BEEP,2000);
                                activityCompleted = true;
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
                            if(movement> 30){
                                tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 50000);
                                tg.startTone(ToneGenerator.TONE_PROP_BEEP,2000);
                                unsubscribe();
                                activityCancelled = true;
                                addScoreToDatabase();
                            }

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

    private double calcAverage(List<Double> accMovementList)
    {
        double sum = 0;
        for (double i : accMovementList) {
            sum+=i;
        }
        return sum/(double) accMovementList.size();
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

    private void addScoreToDatabase() {
        showProgressDialog(getString(R.string.please_wait));
        BalanceData balanceData = new BalanceData(Collections.max(accMovementList), Collections.min(accMovementList),
                calcAverage(accMovementList), new Timestamp(new Date()), activityCompleted, activityName) ;
        balanceResults.add(balanceData);
        firebaseDBConnection.addBalanceScoreListToDB(balanceResults,this);

    }

    public void resultsUploadedSuccess(){
        activityCompleted = false;
        hideProgressDialog();
        if(activityCancelled){
            Intent displayMessageIntent = new Intent(this, DisplayMessageActivity.class);
            displayMessageIntent.putExtra("message", getString(R.string.you_failed_an_activity));
            displayMessageIntent.putExtra("heading", getString(R.string.Hard_Luck));
            startActivity(displayMessageIntent);
            startActivity.setEnabled(false);
        }
        else{
            if(activityListPosition < 3){
                Intent displayMessageIntent = new Intent(this, DisplayMessageActivity.class);
                displayMessageIntent.putExtra("message", getString(R.string.you_completed_an_activity));
                displayMessageIntent.putExtra("heading", getString(R.string.Congratulations));
                startActivity(displayMessageIntent);
                activityListPosition++;
                accMovementList.clear();
                startActivity.setEnabled(true);
                startActivity();
            }
            else{
                Intent displayMessageIntent = new Intent(this, DisplayMessageActivity.class);
                displayMessageIntent.putExtra("message", getString(R.string.you_completed_all_activities));
                displayMessageIntent.putExtra("heading", getString(R.string.Congratulations));
                startActivity(displayMessageIntent);
                startActivity.setEnabled(false);
            }
        }

    }

    private void retrieveActivitiesFromDatabase(){
        showProgressDialog(getString(R.string.please_wait));
        firebaseDBConnection.getBalanceActivities(this);
    }

    public void progressRetrievedSuccess(List<BalanceActivity> activity){
        hideProgressDialog();
        activities = activity;
        startActivity();
        startActivity.setEnabled(true);
    }

    public void progressRetrievedFailed(){
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.no_activities_set), Toast.LENGTH_SHORT).show();
    }

    public void startActivity(){
        activityName = activities.get(activityListPosition).getActivityName();
        time_limit = activities.get(activityListPosition).getTime_limit()*1000;
        displayActName.setText(activities.get(activityListPosition).getActivityName());
        initialiseChart();
    }

    //public void onClick(View v) {
    //    onBackPressed();
    //}
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startActivity:
                startActivity.setEnabled(false);
                Intent displayCountdown = new Intent(this, CountdownActivity.class);
               //startActivity(displayCountdown);
                startActivityForResult(displayCountdown, 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                subscribeToSensors();
                break;
            default:
                break;
        }
    }

}