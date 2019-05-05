package com.optimize.downstream.sensors;

import java.io.Serializable;

public class Accelerometer implements Serializable
{
    private int accelerometer_X;
    private int accelerometer_Y;
    private int accelerometer_Z;

    private int linearAccelerationSensor_X;
    private int linearAccelerationSensor_Y;
    private int linearAccelerationSensor_Z;

    private int gravitySensor_X;
    private int gravitySensor_Y;
    private int gravitySensor_Z;


    public int getAccelerometer_X() {
        return accelerometer_X;
    }

    public int getAccelerometer_Y() {
        return accelerometer_Y;
    }

    public int getAccelerometer_Z() {
        return accelerometer_Z;
    }

    public int getLinearAccelerationSensor_X() {
        return linearAccelerationSensor_X;
    }

    public int getLinearAccelerationSensor_Y() {
        return linearAccelerationSensor_Y;
    }

    public int getLinearAccelerationSensor_Z() {
        return linearAccelerationSensor_Z;
    }

    public int getGravitySensor_X() {
        return gravitySensor_X;
    }

    public int getGravitySensor_Y() {
        return gravitySensor_Y;
    }

    public int getGravitySensor_Z() {
        return gravitySensor_Z;
    }

    public void setAccelerometer_X(int accelerometer_X) {
        this.accelerometer_X = accelerometer_X;
    }

    public void setAccelerometer_Y(int accelerometer_Y) {
        this.accelerometer_Y = accelerometer_Y;
    }

    public void setAccelerometer_Z(int accelerometer_Z) {
        this.accelerometer_Z = accelerometer_Z;
    }

    public void setGravitySensor_X(int gravitySensor_X) {
        this.gravitySensor_X = gravitySensor_X;
    }

    public void setGravitySensor_Y(int gravitySensor_Y) {
        this.gravitySensor_Y = gravitySensor_Y;
    }

    public void setGravitySensor_Z(int gravitySensor_Z) {
        this.gravitySensor_Z = gravitySensor_Z;
    }

    public void setLinearAccelerationSensor_X(int linearAccelerationSensor_X) {
        this.linearAccelerationSensor_X = linearAccelerationSensor_X;
    }

    public void setLinearAccelerationSensor_Y(int linearAccelerationSensor_Y) {
        this.linearAccelerationSensor_Y = linearAccelerationSensor_Y;
    }

    public void setLinearAccelerationSensor_Z(int linearAccelerationSensor_Z) {
        this.linearAccelerationSensor_Z = linearAccelerationSensor_Z;
    }
}
