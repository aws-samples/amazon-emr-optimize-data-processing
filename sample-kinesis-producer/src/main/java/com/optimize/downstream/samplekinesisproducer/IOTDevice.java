package com.optimize.downstream.samplekinesisproducer;

import com.optimize.downstream.sensors.Accelerometer;
import com.optimize.downstream.sensors.GPS;
import com.optimize.downstream.sensors.Illuminance;
import com.optimize.downstream.sensors.TemperatureSensor;

import java.io.Serializable;
import java.util.List;

public class IOTDevice implements Serializable
{
    private String deviceId;
    private String currentDate;
    private List<Accelerometer> accelerometerSensorList;
    private List<GPS> gpsSensorList;
    private List<TemperatureSensor> tempSensorList;
    private List<Illuminance> illuminancesSensorList;

    IOTDevice(String deviceId,
              String currentDate,
              List<Accelerometer> accelerometerArrayList,
              List<GPS> gpsArrayList,
              List<TemperatureSensor> temperatureSensorArrayList,
              List<Illuminance> illuminanceArrayList
            )
    {
        this.deviceId = deviceId;
        this.currentDate = currentDate;
        this.accelerometerSensorList = accelerometerArrayList;
        this.gpsSensorList = gpsArrayList;
        this.tempSensorList = temperatureSensorArrayList;
        this.illuminancesSensorList = illuminanceArrayList;

    }

    public String getDeviceId()
    {
        return deviceId;
    }

    public String getCurrentDate()
    {
        return currentDate;
    }


}
