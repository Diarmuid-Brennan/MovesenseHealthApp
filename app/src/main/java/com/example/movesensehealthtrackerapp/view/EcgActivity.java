package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.EcgModel;
import com.example.movesensehealthtrackerapp.model.LinearAcceleration;
import com.google.gson.Gson;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsSubscription;

public class EcgActivity extends AppCompatActivity {

    //    // Sensor subscription
    private final String ECG_VELOCITY_PATH = "/Meas/ECG/128";
    private final String HEART_RATE_PATH = "/Meas/Hr";
    private final int MS_IN_SECOND = 1000;

    private MdsSubscription mdsSubscriptionHr;
    private MdsSubscription mdsSubscriptionEcg;
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
        setContentView(R.layout.activity_ecg);

        subscribeToEcgSensor();
    }

    public void subscribeToEcgSensor() {
        // Clean up existing subscription (if there is one)
        if (mdsSubscriptionEcg != null) {
            unsubscribe();
        }

        // Build JSON doc that describes what resource and device to subscribe
        // Here we subscribe to 13 hertz accelerometer data
        StringBuilder sb = new StringBuilder();
        String strContract = sb.append("{\"Uri\": \"").append(connectedSerial).append(ECG_VELOCITY_PATH).append("\"}").toString();
        Log.d(LOG_TAG, strContract);

        subscribedDeviceSerial = connectedSerial;

        mdsSubscriptionEcg = mMds.builder().build(this).subscribe(URI_EVENTLISTENER,
                strContract, new MdsNotificationListener() {
                    @Override
                    public void onNotification(String data) {
                        Log.d(LOG_TAG, "onNotification(): " + data);

                        EcgModel ecgResponse = new Gson().fromJson(data, EcgModel.class);

                        String accStr = "";
                        final int[] ecgSamples = ecgResponse.getBody().getData();
                        final int sampleCount = ecgSamples.length;
                        final int ecgSampleRate = 128;
                        final float sampleInterval = (float) MS_IN_SECOND / ecgSampleRate;

                        if (ecgResponse.getBody() != null) {

                            for (int i = 0; i < sampleCount; i++){
                                if (ecgResponse.mBody.timestamp != null) {
                                    accStr = String.format("%d, %s", ecgResponse.mBody.timestamp + Math.round(sampleInterval * i),
                                            String.valueOf(ecgSamples[i]));
                                }

                                ((TextView)findViewById(R.id.ecgTextView)).setText(accStr);
                            }

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
        if (mdsSubscriptionEcg != null) {
            mdsSubscriptionEcg.unsubscribe();
            mdsSubscriptionEcg = null;
        }
        subscribedDeviceSerial = null;
    }

}