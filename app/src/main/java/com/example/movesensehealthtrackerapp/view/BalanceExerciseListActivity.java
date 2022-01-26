package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.BalanceActivity;
import com.example.movesensehealthtrackerapp.model.MyScanResult;
import com.example.movesensehealthtrackerapp.services.FirebaseDBConnection;
import com.example.movesensehealthtrackerapp.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class BalanceExerciseListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

        private FirebaseDBConnection firebaseDBConnection;
        private static final String LOG_TAG = BalanceExerciseListActivity.class.getSimpleName();
        private ListView mActivityListView;
        private List<BalanceActivity> activities = new ArrayList<>();
        private ArrayAdapter<BalanceActivity> mActivityArrayAdapter;
        private String connectedSerial;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_balance_exercise_list);
            Bundle extras = getIntent().getExtras();
            connectedSerial = extras.getString(Constant.SERIAL);
            firebaseDBConnection = new FirebaseDBConnection();

            mActivityListView = (ListView)findViewById(R.id.listActivityResult);
            mActivityArrayAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, activities);
            mActivityListView.setAdapter(mActivityArrayAdapter);
            mActivityListView.setOnItemClickListener(this);

            retrieveActivitiesFromDatabase();
        }

        private void retrieveActivitiesFromDatabase(){
            showProgressDialog(getString(R.string.please_wait));
            firebaseDBConnection.getBalanceActivities(this);
        }

        public void progressRetrievedSuccess(List<BalanceActivity> activity){
            hideProgressDialog();
            Toast.makeText(this, getString(R.string.retrieved_progress_results), Toast.LENGTH_SHORT).show();
            activities = activity;
            for(BalanceActivity act : activities)
                mActivityArrayAdapter.add(act);
            mActivityArrayAdapter.notifyDataSetChanged();
        }

        public void progressRetrievedFailed(){
            hideProgressDialog();
            Toast.makeText(this, getString(R.string.no_activities_set), Toast.LENGTH_SHORT).show();
        }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BalanceActivity activity = activities.get(position);
        Log.d(LOG_TAG,"Selected Activity " + activity);

        Intent intent = new Intent(this,ActivityDetailsActivity.class);
        Bundle b = new Bundle();
        b.putString(Constant.SERIAL, connectedSerial);
        b.putParcelable(Constant.PARSED, activity);
        intent.putExtras(b);
        startActivity(intent);
    }
}