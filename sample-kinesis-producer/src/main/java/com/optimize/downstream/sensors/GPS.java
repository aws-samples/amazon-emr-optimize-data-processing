package com.optimize.downstream.sensors;

import java.io.Serializable;

public class GPS implements Serializable
{
    private double latitude; // -90.0 to 90.0
    private double longitude; // -180.0 to 180.0
    private double altitude; //In meters
    private double heading; // 0 to 359.999

    public double getAltitude() {
        return altitude;
    }

    public double getHeading() {
        return heading;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
