package com.example.movesensehealthtrackerapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
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

import java.util.ArrayList;
import java.util.List;

public class ProgressReportActivity extends AppCompatActivity {

    private LineChart mChart;
    private FirebaseDBConnection firebaseDBConnection;

    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};

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

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(10, true);
        xAxis.setGranularity(1.0f);
        xAxis.setDrawLabels(true);
//        xAxis.setCenterAxisLabels(true);
//        xAxis.setLabelRotationAngle(-90);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));


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
            balanceSet = createSet("Balance", getResources().getColor(android.R.color.holo_red_dark));
            heartRateSet = createSet("Heart Rate", getResources().getColor(android.R.color.holo_green_dark));
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
    //        YAxis left = mChart.getAxisLeft();
//        left.setDrawLabels(true); // no axis labels
//        left.setDrawAxisLine(true); // no axis line
//        left.setDrawGridLines(true); // no grid lines
//        left.setDrawZeroLine(true); // draw a zero line
//        left.setLabelCount(7);
//        mChart.getAxisRight().setEnabled(false);
//
//        final List list_x_axis_name = new ArrayList<>();
//        list_x_axis_name.add("label1");
//        list_x_axis_name.add("label2");
//        list_x_axis_name.add("label3");
//        list_x_axis_name.add("label4");
//        list_x_axis_name.add("label5");
//
//        left.setCenterAxisLabels(true);
//        left.setValueFormatter(new IndexAxisValueFormatter(){
//            public String getFormattedValue(float value, AxisBase axis){
//                if(value >=0){
//                    if(value <= list_x_axis_name.size() - 1){
//                        return (String) list_x_axis_name.get((int) value);
//                    }
//                    return "";
//                }
//                return "";
//            }
//        });
}