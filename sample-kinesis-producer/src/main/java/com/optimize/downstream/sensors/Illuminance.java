package com.optimize.downstream.sensors;

import java.io.Serializable;

public class Illuminance implements Serializable
{
    private double illuminance;

    public double getIlluminance() {
        return illuminance;
    }

    public void setIlluminance(double illuminance) {
        this.illuminance = illuminance;
    }
}
