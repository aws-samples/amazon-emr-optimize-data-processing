package com.optimize.downstream.samplekinesisproducer;

import com.optimize.downstream.sensors.Accelerometer;
import com.optimize.downstream.sensors.GPS;
import com.optimize.downstream.sensors.Illuminance;
import com.optimize.downstream.sensors.TemperatureSensor;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class IOTDeviceProducerToBlockingQueue implements Runnable
{
    private final BlockingQueue<IOTDevice> inputQueue;
    private volatile boolean shutdown = false;
    private final AtomicLong recordsPut = new AtomicLong(0);
    private Random random = new Random();
    private int numberOfSamplesInEachMessage;
    private int numberOfMessages;

    public IOTDeviceProducerToBlockingQueue(BlockingQueue<IOTDevice> inputQueue, int numberOfMessages, int numberOfSamplesInEachMessage)
    {
        this.inputQueue = inputQueue;
        this.numberOfSamplesInEachMessage = numberOfSamplesInEachMessage;
        this.numberOfMessages = numberOfMessages;
    }

    public void run() {
        long threadId = Thread.currentThread().getId();
        //System.out.println("Thread # " + threadId + " is doing this task");

        //while (!shutdown) {
        for(int i=0;i<numberOfMessages;i++)
        {
            try {
                IOTDevice iotDevice = generateIOTDeviceData();
                System.out.println("Inserting a message into blocking queue before sending to Kinesis Firehose and Message number is : " + i);
                inputQueue.put(iotDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i%100 == 0) // For every 100 messages sleep 500ms.
            {
                try {
                    System.out.println("Producer Thread # " + threadId + " is going to sleep mode for 500 ms.");
                    Thread.sleep(500);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private IOTDevice  generateIOTDeviceData()
    {
        String deviceId;
        String currentDate;
        UUID uuid = UUID.randomUUID();
        //System.out.println("Device Id is :: " + uuid);
        deviceId = uuid.toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        currentDate = dateFormat.format(date);

        IOTDevice iotDevice = new IOTDevice(deviceId,
                currentDate,
                getAccelerometerList(),
                getGpsArrayList(),
                getTemperatureSensorArrayList(),
                getIlluminanceArrayList());

        return iotDevice;
    }

    private ArrayList<Accelerometer> getAccelerometerList()
    {
        ArrayList<Accelerometer> accelerometerArrayList = new ArrayList();

        for(int i =0;i <numberOfSamplesInEachMessage; i++)
        {
            Accelerometer a = new Accelerometer();
            a.setAccelerometer_X(getRandomInt(10,1));
            a.setAccelerometer_Y(getRandomInt(10,1));
            a.setAccelerometer_Z(getRandomInt(10,1));
            a.setGravitySensor_X(getRandomInt(10,1));
            a.setGravitySensor_Y(getRandomInt(10,1));
            a.setGravitySensor_Z(getRandomInt(10,1));
            a.setLinearAccelerationSensor_X(getRandomInt(10,1));
            a.setLinearAccelerationSensor_Y(getRandomInt(10,1));
            a.setLinearAccelerationSensor_Z(getRandomInt(10,1));

            //System.out.println("Just for testing ::::: getAccelerometer_X is ::: " + a.getAccelerometer_X());
            accelerometerArrayList.add(a);
        }
        //System.out.println("Acc List size is :::: " + accelerometerArrayList.size());
        return accelerometerArrayList;
    }


    private ArrayList<GPS> getGpsArrayList()
    {
        ArrayList<GPS> gpsArrayList = new ArrayList();

        for(int i =0; i< numberOfSamplesInEachMessage; i++)
        {
            GPS g = new GPS();
            g.setAltitude(getRandomDouble(1,10));
            g.setHeading(getRandomDouble(1,10));
            g.setLatitude(Math.random() * Math.PI * 2);
            g.setLongitude(Math.acos(Math.random() * 2 - 1));

            gpsArrayList.add(g);
        }

        return gpsArrayList;
    }

    private ArrayList<Illuminance> getIlluminanceArrayList()
    {
        ArrayList<Illuminance> illuminanceArrayList = new ArrayList();

        for(int i =0; i< numberOfSamplesInEachMessage; i++)
        {
            Illuminance il = new Illuminance();
            il.setIlluminance(getRandomDouble(1,100));
            illuminanceArrayList.add(il);
        }

        return illuminanceArrayList;
    }

    private ArrayList<TemperatureSensor> getTemperatureSensorArrayList()
    {
        ArrayList<TemperatureSensor> temperatureSensorArrayList = new ArrayList();

        for (int i=0;i<numberOfSamplesInEachMessage;i++)
        {
            TemperatureSensor t = new TemperatureSensor();
            t.setCelsius(getRandomDouble(1,100));
            t.setFahrenheit(getRandomDouble(1,150));
            t.setKelvin(getRandomDouble(1,1000));

            temperatureSensorArrayList.add(t);
        }

        return temperatureSensorArrayList;
    }

    private int getRandomInt(int max, int min)
    {
        return random.nextInt(max - min + 1) + min;
    }

    private double getRandomDouble(int rangeMin, int rangeMax)
    {
        return rangeMin + (rangeMax - rangeMin) * random.nextDouble();
    }
    public long recordsPut() {
        return recordsPut.get();
    }

    public void stop() {
        shutdown = true;
    }

}
