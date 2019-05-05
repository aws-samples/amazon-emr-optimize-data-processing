package com.optimize.downstream.sensors;

import java.io.Serializable;

public class TemperatureSensor implements Serializable
{
    private double celsius;
    private double fahrenheit;
    private double kelvin;

    public double getCelsius() {
        return celsius;
    }

    public double getFahrenheit() {
        return fahrenheit;
    }

    public double getKelvin() {
        return kelvin;
    }

    public void setCelsius(double celsius) {
        this.celsius = celsius;
    }

    public void setFahrenheit(double fahrenheit) {
        this.fahrenheit = fahrenheit;
    }

    public void setKelvin(double kelvin) {
        this.kelvin = kelvin;
    }
}
