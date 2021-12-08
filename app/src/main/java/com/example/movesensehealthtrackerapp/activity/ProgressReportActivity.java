package com.example.movesensehealthtrackerapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.services.GetDataFromDB;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProgressReportActivity extends AppCompatActivity {

    private LineChart mChart;
    private FirebaseDBConnection firebaseDBConnection;
    public static Context context;

    String[] months = {"Jan", "Feb"};
    //List<BalanceData> balanceProgress;
    List<Float> balanceProgress;
    List<Integer> hrProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_report);

        mChart = (LineChart) findViewById(R.id.progress_lineChart);
        initialiseChart();

        context = getApplicationContext();
        firebaseDBConnection = new FirebaseDBConnection();
        getData();


    }

    private void getData(){
//        final TaskCompletionSource<List<BalanceData>> source = new TaskCompletionSource<>();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                balanceProgress  = firebaseDBConnection.getBalanceProgress(context);
//                source.setResult(balanceProgress);
//            }
//        }).start();
//
//        hrProgress = firebaseDBConnection.getHeartRateProgress();
//
//        Task<List<BalanceData>> task = source.getTask();
//        task.addOnCompleteListener(new OnCompleteListener<List<BalanceData>>() {
//            @Override
//            public void onComplete(@NonNull Task<List<BalanceData>> task) {
//                displayProgress();
//            }
//        });

        hrProgress = firebaseDBConnection.getHeartRateProgress();
        balanceProgress = firebaseDBConnection.getBalanceProgress2();
        displayProgress();
    }



//    private void getData(){
//        balanceProgress = firebaseDBConnection.getBalanceProgress(context, resultList -> balanceProgress.add(1F));
//        hrProgress = firebaseDBConnection.getHeartRateProgress();
//        //displayProgress();
//    }

//    public interface IQuery{
//        void onSuccess(List<Float> resultList);
//    }

//        private void getData(){
//            new GetDataFromDB().execute();
//        }

//    private void CompletableFuture<Void> getData1(){
//        try {
//            balanceProgress = firebaseDBConnection.getBalanceProgress(context);
//            reportSuccess(result);
//        } catch (Throwable t) {
//            reportFailure(t);
//        }
//        return completedFuture(null);
//
//        hrProgress = firebaseDBConnection.getHeartRateProgress();
//    }

    private void initialiseChart() {
        // Init Empty Chart
        mChart.setData(new LineData());
        mChart.getDescription().setText("Progress Report");
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(2, true);
        xAxis.setGranularity(1.0f);
        xAxis.setDrawLabels(true);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setEnabled(true);

        mChart.getAxisRight().setEnabled(false);
    }

    public void displayProgress(){

        final LineData mLineData = mChart.getData();

        ILineDataSet balanceSet = mLineData.getDataSetByIndex(0);
        ILineDataSet heartRateSet = mLineData.getDataSetByIndex(1);

        if (balanceSet == null) {
            balanceSet = createSet("Balance", getResources().getColor(android.R.color.holo_red_dark));
            heartRateSet = createSet("Heart Rate", getResources().getColor(android.R.color.holo_green_dark));
            mLineData.addDataSet(balanceSet);
            mLineData.addDataSet(heartRateSet);

            for(int currentVal =0; currentVal < balanceProgress.size(); currentVal++)
            {
                //mLineData.addEntry(new Entry((float)currentVal, (float) balanceProgress.get(currentVal).getAvg_Value()), 0);
                mLineData.addEntry(new Entry((float)currentVal, (float) balanceProgress.get(currentVal)), 0);
                mLineData.addEntry(new Entry((float)currentVal, (float) hrProgress.get(currentVal)), 1);
                mLineData.notifyDataChanged();

                // let the chart know it's data has changed
                mChart.notifyDataSetChanged();
            }


        }
    }

    private LineDataSet createSet(String name, int color) {
        LineDataSet set = new LineDataSet(null, name);
        set.setLineWidth(2.5f);
        set.setColor(color);
        set.setMode(LineDataSet.Mode.LINEAR);
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }
}