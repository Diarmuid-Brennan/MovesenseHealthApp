package com.example.movesensehealthtrackerapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

public class ProgressReportActivity extends AppCompatActivity {

    private LineChart mChart;
    private FirebaseDBConnection firebaseDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_report);

        mChart = (LineChart) findViewById(R.id.progress_lineChart);

        // Init Empty Chart
        mChart.setData(new LineData());
        mChart.getDescription().setText("Progress Report");
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();

        firebaseDBConnection = new FirebaseDBConnection();

        displayProgress();
    }

    private void displayProgress(){
        List<Float> balanceProgress = firebaseDBConnection.getBalanceProgress();
        List<Integer> hrProgress = firebaseDBConnection.getHeartRateProgress();
        double x = 0;

        final LineData mLineData = mChart.getData();

        ILineDataSet balanceSet = mLineData.getDataSetByIndex(0);
        ILineDataSet heartRateSet = mLineData.getDataSetByIndex(1);

        if (balanceSet == null) {
            balanceSet = createSet("Data x", getResources().getColor(android.R.color.holo_red_dark));
            heartRateSet = createSet("Data y", getResources().getColor(android.R.color.holo_green_dark));
            mLineData.addDataSet(balanceSet);
            mLineData.addDataSet(heartRateSet);

            for(int currentVal =0; currentVal < balanceProgress.size(); currentVal++)
            {
                mLineData.addEntry(new Entry((float)currentVal, (float) balanceProgress.get(currentVal)), 0);
                mLineData.addEntry(new Entry((float)currentVal, (float) hrProgress.get(currentVal)), 1);
                mLineData.notifyDataChanged();

                // let the chart know it's data has changed
                mChart.notifyDataSetChanged();

                // limit the number of visible entries
                mChart.setVisibleXRangeMaximum(50);

                // move to the latest entry
                mChart.moveViewToX((float) currentVal);
            }


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
}