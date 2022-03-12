/**
 * Diarmuid Brennan
 * 13/03/22
 * My Scan result class - Contains the results of the results of the Bluetooth Low Energy scan for nearby devices
 */
package com.example.movesensehealthtrackerapp.model;

import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.scan.ScanResult;

public class MyScanResult {
    public int rssi;
    public String macAddress;
    public String name;
    public String connectedSerial;

    /**
     * Constructor - Takes in the BLE scan results
     * @param scanResult - mac-address, rssi and name of found devices
     */
    public MyScanResult(ScanResult scanResult) {
        this.macAddress = scanResult.getBleDevice().getMacAddress();
        this.rssi = scanResult.getRssi();
        this.name = scanResult.getBleDevice().getName();
    }

    /**
     * Check if the device is connected
     * @return true if connected
     */
    public boolean isConnected() {return connectedSerial != null;}

    /**
     * Marks the scanned item as connected to the application
     * @param serial - returns the serial number of the device
     */
    public void markConnected(String serial) {connectedSerial = serial;}
    /**
     * Marks the scan item as disconnected from the application
     */
    public void markDisconnected() {connectedSerial = null;}

    /**
     * Checks if the selected device is connected to the correct Movesense device
     * @param object- passes in a scanned object
     * @return - returns true if the application has connected to this device
     */
    public boolean equals(Object object) {
        if(object instanceof MyScanResult && ((MyScanResult)object).macAddress.equals(this.macAddress)) {
            return true;
        }
        else if(object instanceof RxBleDevice && ((RxBleDevice)object).getMacAddress().equals(this.macAddress)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * toString method
     * @return - name, rssi and mac-address of the scanned device
     */
    public String toString() {
        return ( name + "\n" + macAddress + "\n" + " [" + rssi + "]");
    }
}

