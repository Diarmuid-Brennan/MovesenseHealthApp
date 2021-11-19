package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.AngularVelocity;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

public class GyroActivity extends AppCompatActivity {

    static private String ANGULAR_VELOCITY_PATH_13 = "/Meas/Gyro/13";
    private MdsSubscription mdsSubscription;
    private String subscribedDeviceSerial;
    private static final String LOG_TAG = AccActivity.class.getSimpleName();
    private String connectedSerial;

    private Mds mMds;
    public static final String URI_CONNECTEDDEVICES = "suunto://MDS/ConnectedDevices";
    public static final String URI_EVENTLISTENER = "suunto://MDS/EventListener";
    public static final String SCHEME_PREFIX = "suunto://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        connectedSerial = extras.getString("serial");
        setContentView(R.layout.activity_gyro);

        subscribeToGyroSensor();
    }

        public void subscribeToGyroSensor() {
        // Clean up existing subscription (if there is one)
        if (mdsSubscription != null) {
            unsubscribe();
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        StringBuilder sb = new StringBuilder();
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(ANGULAR_VELOCITY_PATH_13).append("\"}").toString();
        Log.d(LOG_TAG, strContract);
        //final View sensorUI = findViewById(R.id.ma);

        subscribedDeviceSerial = connectedSerial;

        mdsSubscription = mMds.builder().build(this).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);

//                        // If UI not enabled, do it now
//                        if (sensorUI.getVisibility() == View.GONE)
//                            sensorUI.setVisibility(View.VISIBLE);

                        AngularVelocity gyroResponse = new Gson().fromJson(data, AngularVelocity.class);
                        if (gyroResponse != null && gyroResponse.body.array.length > 0) {

                            String gyroStr =
                                    String.format("%.02f, %.02f, %.02f", gyroResponse.body.array[0].x, gyroResponse.body.array[0].y, gyroResponse.body.array[0].z);

                            ((TextView)findViewById(R.id.gyroTextView)).setText(gyroStr);
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

        subscribedDeviceSerial = null;

        // If UI not invisible, do it now
//        final View sensorUI = findViewById(R.id.sensorUI);
//        if (sensorUI.getVisibility() != View.GONE)
//            sensorUI.setVisibility(View.GONE);

    }
}