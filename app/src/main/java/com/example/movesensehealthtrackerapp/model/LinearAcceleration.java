/**
 * Diarmuid Brennan
 * 13/03/22
 * Linear Acceleration - Serializes the linear acceleration data gathered from the Movesense sensor device
 */
package com.example.movesensehealthtrackerapp.model;
import com.google.gson.annotations.SerializedName;

public class LinearAcceleration {

    @SerializedName("Body")
    public final Body body;

    /**
     * Constructor - Takes in the data passed from the sensor
     * @param body - sensor data passed
     */
    public LinearAcceleration(Body body) {
        this.body = body;
    }

    public static class Body {
        @SerializedName("Timestamp")
        public final long timestamp;

        @SerializedName("ArrayAcc")
        public final Array[] array;

        @SerializedName("Headers")
        public final Headers header;

        public Body(long timestamp, Array[] array, Headers header) {
            this.timestamp = timestamp;
            this.array = array;
            this.header = header;
        }
    }

    public static class Array {
        @SerializedName("x")
        public final double x;
        @SerializedName("y")

        public final double y;
        @SerializedName("z")
        public final double z;

        public Array(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Headers {
        @SerializedName("Param0")
        public final int param0;

        public Headers(int param0) {
            this.param0 = param0;
        }
    }
}

