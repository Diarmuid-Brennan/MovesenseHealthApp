/**
 * Diarmuid Brennan
 * 10/03/22
 * Progress Report Activity - Displays a report of the results from the activities carried out
 */
package com.example.movesensehealthtrackerapp.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceData;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ProgressReportActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private LineChart mChart;
    private FirebaseDBConnection firebaseDBConnection;
    public static Context context;
    private List<String> dates = new ArrayList<>();
    private List<BalanceData> balanceProgress = new ArrayList<>();
    private List<BalanceData> feetTogether = new ArrayList<>();
    private List<BalanceData> instepStance = new ArrayList<>();
    private List<BalanceData> tandemStance = new ArrayList<>();
    private List<BalanceData> oneFoot = new ArrayList<>();
    private FirebaseFirestore fd = FirebaseFirestore.getInstance();
    private List<Map<String,Object>> retrieveResults;

    private TextView lastActivityTaken;
    private TextView together;
    private TextView instep;
    private TextView tandem;
    private TextView onefoot;

    private Spinner spinner;
    private static final String[] paths = {"Stand with your feet side-by-side", "Instep Stance", "Tandem Stance", "Stand on one foot"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_report);

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

        lastActivityTaken = (TextView) findViewById(R.id.lastActivity);
        together = (TextView) findViewById(R.id.feetTogether);
        instep = (TextView) findViewById(R.id.instepStance);
        tandem = (TextView) findViewById(R.id.tandemStance);
        onefoot = (TextView) findViewById(R.id.oneFoot);

        context = getApplicationContext();
        firebaseDBConnection = new FirebaseDBConnection();
        mChart = (LineChart) findViewById(R.id.progress_lineChart);
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProgressReportActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        retrieveProgressFromDatabase();
    }

    /**
     * retrieves the users previous results from the database
     */
    private void retrieveProgressFromDatabase(){
        showProgressDialog(getString(R.string.please_wait));
        firebaseDBConnection.getBalanceProgress(this);
    }

    /**
     * return method containing th previous results gathered from the database
     * @param results - List of results from previous activities carried out
     */
    public void progressRetrievedSuccess(List<Map<String,Object>> results){
        retrieveResults = results;
        hideProgressDialog();
        separateActivityResults();
    }

    /**
     * return method containing th previous results gathered from the database failed
     */
    public void progressRetrievedFailed(){
        hideProgressDialog();
        Toast.makeText(this, getString(R.string.no_data_found), Toast.LENGTH_SHORT).show();
    }

    /**
     * Separates the results gathered from the database into their separate activities
     */
    private void separateActivityResults(){
        Iterator<Map<String, Object>> iterator = retrieveResults.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> activity = iterator.next();
            Set<Map.Entry<String, Object>> entrySet =activity.entrySet();

            // for-each loop
            for(Map.Entry<String, Object> entry : entrySet) {
                if(entry.getKey().equals("Stand with your feet side-by-side")){
                    Object act = entry.getValue();
                    BalanceData data = convertToObject(act);
                    feetTogether.add(data);
                }
                else if(entry.getKey().equals("Instep Stance")){
                    Object act = entry.getValue();
                    BalanceData data = convertToObject(act);
                    instepStance.add(data);
                }
                else if(entry.getKey().equals("Tandem Stance")){
                    Object act = entry.getValue();
                    BalanceData data = convertToObject(act);
                    tandemStance.add(data);
                }
                else{
                    Object act = entry.getValue();
                    BalanceData data = convertToObject(act);
                    oneFoot.add(data);
                }
            }
        }
        displayData();
    }

    /**
     * displays the number of completed activities
     */
    private void displayData(){
        String lastDate = feetTogether.get(0).getDate_set();
        lastActivityTaken.setText("Last Activity taken : " + lastDate);
        together.setText(getString(R.string.feet_together) + getCompletedValues(feetTogether));
        instep.setText(getString(R.string.instep) + getCompletedValues(instepStance));
        tandem.setText(getString(R.string.tandem) + getCompletedValues(tandemStance));
        onefoot.setText(getString(R.string.one_foot) + getCompletedValues(oneFoot));
    }

    /**
     * Calculates the number of successfully completed activities
     * @param activity - takes in the activity
     * @return -returns the calculated result
     */
    private String getCompletedValues( List<BalanceData> activity){
        int completed = 0;
        for (BalanceData data : activity){
            if(data.getCompleted()){
                completed++;
            }
        }
        String total = String.valueOf(completed) + "/" +String.valueOf(activity.size());
        return total;
    }

    /**
     * Converts a Firestore object to a Balance Data class object
     * @param obj - Firestore object
     * @return - Balance Data object
     */
    private BalanceData convertToObject(Object obj){
        HashMap hashMap = (HashMap) obj;
        BalanceData data = new BalanceData(
                (double)hashMap.get("max_value"),
                (double)hashMap.get("min_value"),
                (double)hashMap.get("avg_value"),
                hashMap.get("date_set").toString(),
                (boolean)hashMap.get("completed"),
                hashMap.get("activityName").toString()
        );
        return data;
    }

    /**
     * Spinner view - allows user to select from a dropdown list
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                initialiseChart(feetTogether);
                break;
            case 1:
                initialiseChart(instepStance);
                break;
            case 2:
                initialiseChart(tandemStance);
                break;
            case 3:
                initialiseChart(oneFoot);
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //initialiseChart(feetTogether);
    }

    /**
     * Creates a chart displaying the progress results for a selected activity
     * @param data - Takes in a list of Balance Data objects
     */
    private void initialiseChart(List<BalanceData> data) {
        mChart.setData(new LineData());
        if(!data.isEmpty()) mChart.getDescription().setText(data.get(0).getActivityName());
        mChart.setTouchEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.invalidate();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(data.size(), true);
        xAxis.setGranularity(1.0f);
        xAxis.setDrawLabels(true);

        if(!data.isEmpty()){
            formatResultsDates(data);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        }

        YAxis yAxisLeft = mChart.getAxisLeft();
        yAxisLeft.setEnabled(true);

        mChart.getAxisRight().setEnabled(false);
        displayProgress(data);
    }

    /**
     * formats the dates an activity was carried out to be displayed on the graph
     * @param data - Takes in the list of Balance Data objects for selected activities
     */
    private void formatResultsDates(List<BalanceData> data){
        for (int currentVal = data.size()-1; currentVal >= 0; currentVal--) {
            String date = data.get(currentVal).getDate_set();
            dates.add(date.substring(5));
        }
    }

    /**
     * Displays the results to the line graph chart
     * @param data - Takes in the list of Balance Data objects for selected activities
     */
    private void displayProgress(List<BalanceData> data){
        final LineData mLineData = mChart.getData();
        ILineDataSet balanceSet = mLineData.getDataSetByIndex(0);

        if (balanceSet == null) {
            balanceSet = createSet(getString(R.string.averageScores), getResources().getColor(android.R.color.holo_red_dark));

            mLineData.addDataSet(balanceSet);

            if (!data.isEmpty()){
                int num = 0;
                for(int currentVal =data.size()-1; currentVal >=0; currentVal--)
                {
                    mLineData.addEntry(new Entry((float)num, (float) data.get(currentVal).getAvg_Value()), 0);
                    mLineData.notifyDataChanged();
                    mChart.notifyDataSetChanged();
                    num++;
                }
            }
        }
    }

    /**
     * Creates the Line to be displayed on the graph chart
     * @param name - Gives a name to the line data
     * @param color - Gives a color to the line data
     * @return - returns a LineDataSet object
     */
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