package com.example.movesensehealthtrackerapp.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceData;
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
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgressReportActivity extends BaseActivity {

    private LineChart mChart;
    private FirebaseDBConnection firebaseDBConnection;
    public static Context context;
    private List<String> dates = new ArrayList<>();
    private List<BalanceData> balanceProgress = new ArrayList<>();
    private List<Integer> hrProgress;
    private FirebaseFirestore fd = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_report);

        context = getApplicationContext();
        firebaseDBConnection = new FirebaseDBConnection();
        mChart = (LineChart) findViewById(R.id.progress_lineChart);
        hrProgress = firebaseDBConnection.getHeartRateProgress();
        retrieveProgressFromDatabase();
    }

    private void retrieveProgressFromDatabase(){
        showProgressDialog(getString(R.string.please_wait));
        firebaseDBConnection.getBalanceProgress(context, balanceProgress, this);
    }

    public void progressRetrievedSuccess(){
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.retrieved_progress_results), Toast.LENGTH_SHORT).show();
        initialiseChart();
    }

    public void progressRetrievedFailed(){
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
        initialiseChart();
    }

    private void initialiseChart() {
        mChart.setData(new LineData());
        mChart.getDescription().setText(Constant.PROGRESS_REPORT);
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(balanceProgress.size(), true);
        xAxis.setGranularity(1.0f);
        xAxis.setDrawLabels(true);

        formatResultsDates();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setEnabled(true);

        mChart.getAxisRight().setEnabled(false);
        displayProgress();
    }

    private void formatResultsDates(){
        if (balanceProgress != null) {
            for (int currentVal = 0; currentVal < balanceProgress.size(); currentVal++) {
                Date date = balanceProgress.get(currentVal).getDate_set().toDate();
                dates.add((String) DateFormat.format("dd",   date) + "/" + (String) DateFormat.format("MM",   date));
            }
        }
    }

    private void displayProgress(){
        final LineData mLineData = mChart.getData();
        ILineDataSet balanceSet = mLineData.getDataSetByIndex(0);
        ILineDataSet heartRateSet = mLineData.getDataSetByIndex(1);

        if (balanceSet == null) {
            balanceSet = createSet(Constant.BALANCE, getResources().getColor(android.R.color.holo_red_dark));
            heartRateSet = createSet(Constant.HEART_RATE, getResources().getColor(android.R.color.holo_green_dark));
            mLineData.addDataSet(balanceSet);
            mLineData.addDataSet(heartRateSet);

            if (balanceProgress != null){
                for(int currentVal =0; currentVal < balanceProgress.size(); currentVal++)
                {
                    mLineData.addEntry(new Entry((float)currentVal, (float) balanceProgress.get(currentVal).getAvg_Value()), 0);
                    mLineData.addEntry(new Entry((float)currentVal, (float) hrProgress.get(currentVal)), 1);
                    mLineData.notifyDataChanged();
                    mChart.notifyDataSetChanged();
                }
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