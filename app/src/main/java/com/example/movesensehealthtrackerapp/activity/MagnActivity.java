package com.example.movesensehealthtrackerapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.MagneticField;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

public class MagnActivity extends AppCompatActivity {

    //    // Sensor subscription
    private final String MAGNETIC_FIELD_PATH = "/Meas/Magn/13";
    private MdsSubscription mdsSubscription;
    private String subscribedDeviceSerial;
    private static final String LOG_TAG = InitialBalanceActivity.class.getSimpleName();
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
        setContentView(R.layout.activity_magn);

        subscribeToMagnSensor();
    }

        public void subscribeToMagnSensor() {
        // Clean up existing subscription (if there is one)
        if (mdsSubscription != null) {
            unsubscribe();
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        StringBuilder sb = new StringBuilder();
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(MAGNETIC_FIELD_PATH).append("\"}").toString();
        Log.d(LOG_TAG, strContract);

        subscribedDeviceSerial = connectedSerial;

        mdsSubscription = mMds.builder().build(this).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);


                        MagneticField magnResponse = new Gson().fromJson(data, MagneticField.class);
                        if (magnResponse != null && magnResponse.body.array.length > 0) {

                            String accStr =
                                    String.format("%.02f, %.02f, %.02f", magnResponse.body.array[0].x, magnResponse.body.array[0].y, magnResponse.body.array[0].z);

                            ((TextView)findViewById(R.id.magnTextView)).setText(accStr);
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