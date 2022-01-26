package com.example.movesensehealthtrackerapp.view;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.movesensehealthtrackerapp.R;
import com.example.movesensehealthtrackerapp.model.MyScanResult;
import com.example.movesensehealthtrackerapp.utils.Constant;
import com.example.movesensehealthtrackerapp.utils.CustomButtonView;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsConnectionListener;
import com.movesense.mds.MdsException;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    static MyScanResult device = null;
    // MDS
    private Mds mMds;
    static private RxBleClient mBleClient;
    // UI
    private ListView mScanResultListView;
    private ArrayList<MyScanResult> mScanResArrayList = new ArrayList<>();
    private ArrayAdapter<MyScanResult> mScanResArrayAdapter;
    private CustomButtonView exerciseListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // Init Scan UI
        mScanResultListView = (ListView)findViewById(R.id.listScanResult);
        mScanResArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mScanResArrayList);
        mScanResultListView.setAdapter(mScanResArrayAdapter);
        mScanResultListView.setOnItemClickListener(this);
        exerciseListButton = (CustomButtonView) findViewById(R.id.balanceExListButton);

        requestNeededPermissions();

        initMds();
        initializeScan();
        //balList();

        //when logout button is implemented
//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private void initMds() {
        mMds = Mds.builder().build(this);
    }

    void requestNeededPermissions() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //continue
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onBackPressed(){
        doubleBackToExit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constant.MY_PERMISSIONS_REQUEST_LOCATION){
            //continue
        }else{
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    private RxBleClient getBleClient() {
        if (mBleClient == null) mBleClient = RxBleClient.create(this);
        return mBleClient;
    }

    Disposable mScanSubscription;
    public void initializeScan() {


        mScanResArrayList.clear();
        mScanResArrayAdapter.notifyDataSetChanged();

        mScanSubscription = getBleClient().scanBleDevices(
                new ScanSettings.Builder()
                        .build()
        )
                .subscribe(
                        scanResult -> {
                            Log.d(LOG_TAG,"scanResult: " + scanResult);

                            if (scanResult.getBleDevice()!=null &&
                                    scanResult.getBleDevice().getName() != null &&
                                    scanResult.getBleDevice().getName().startsWith(Constant.MOVESENSE)) {

                                MyScanResult msr = new MyScanResult(scanResult);
                                if (mScanResArrayList.contains(msr)) mScanResArrayList.set(mScanResArrayList.indexOf(msr), msr);
                                else mScanResArrayList.add(0, msr);
                                mScanResArrayAdapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> {
                            Log.e(LOG_TAG,"scan error: " + throwable);
                            onScanStopClicked(null);
                        }
                );
    }

    public void onScanStopClicked(View view) {
        if (mScanSubscription != null)
        {
            mScanSubscription.dispose();
            mScanSubscription = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || position >= mScanResArrayList.size())
            return;

        device = mScanResArrayList.get(position);
        if (!device.isConnected()) {
            onScanStopClicked(null);
            connectBLEDevice(device);
        }
    }

    private void connectBLEDevice(MyScanResult device) {
        RxBleDevice bleDevice = getBleClient().getBleDevice(device.macAddress);

        Log.i(LOG_TAG, "Connecting to BLE device: " + bleDevice.getMacAddress());
        mMds.connect(bleDevice.getMacAddress(), new MdsConnectionListener() {
            @Override
            public void onConnect(String s) {
                Log.d(LOG_TAG, "onConnect:" + s);
            }

            @Override
            public void onConnectionComplete(String macAddress, String serial) {
                for (MyScanResult sr : mScanResArrayList) {
                    if (sr.macAddress.equalsIgnoreCase(macAddress)) {
                        sr.markConnected(serial);
                        break;
                    }
                }
                onConnectionSuccessDisplayMessage();
                exerciseListButton.setEnabled(true);
                mScanResArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(MdsException e) {
                Log.e(LOG_TAG, "onError:" + e);
                showConnectionError(e);
            }

            @Override
            public void onDisconnect(String bleAddress) {
                Log.d(LOG_TAG, "onDisconnect: " + bleAddress);
                for (MyScanResult sr : mScanResArrayList) {
                    if (bleAddress.equals(sr.macAddress))
                        sr.markDisconnected();
                }
                mScanResArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void onConnectionSuccessDisplayMessage(){
        Toast.makeText(this, getString(R.string.connected_to_movesense_device), Toast.LENGTH_SHORT).show();
    }

    private void showConnectionError(MdsException e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.connection_error))
                .setMessage(e.getMessage());
        builder.create().show();
    }

    private void balList(){
        Intent balanceExListIntent = new Intent(this, BalanceExerciseListActivity.class);
        balanceExListIntent.putExtra("serial", "2");
        startActivity(balanceExListIntent);//refresh is onClick name given to the button
    }

    public void balanceExListClicked(View view){
        Intent balanceExListIntent = new Intent(this, BalanceExerciseListActivity.class);
        balanceExListIntent.putExtra(Constant.SERIAL, device.connectedSerial);
        startActivity(balanceExListIntent);
    }
}