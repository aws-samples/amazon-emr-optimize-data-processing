package com.optimize.downstream.samplekinesisproducer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.google.gson.Gson;

public class IOTDeviceConsumerFromBlockingQueueToKinesisStreams implements Runnable
{
    private AmazonKinesis kinesis;
    private List<PutRecordsRequestEntry> entries;
    private BlockingQueue<IOTDevice> inputQueue;
    private int dataSize;
    Gson gson;
    private final String STREAM_NAME = "AWS-Blog-BaseKinesisStream";
    private final String  REGION = "us-east-1";
    private static Charset charset = Charset.forName("UTF-8");

    public IOTDeviceConsumerFromBlockingQueueToKinesisStreams(BlockingQueue<IOTDevice> inputQueue)
    {
        gson = new Gson();
        this.inputQueue = inputQueue;
        kinesis = new AmazonKinesisClient().withRegion(Regions.fromName(REGION));
        entries = new ArrayList();
        dataSize = 0;
    }

    @Override
    public void run()
    {
        long threadId = Thread.currentThread().getId();
        System.out.println("Thread # " + threadId + " is doing this task");
        while(!inputQueue.isEmpty())
        {
            try {
                IOTDevice deviceMessage = inputQueue.take();
                String partitionKey = deviceMessage.getDeviceId();

                String json = gson.toJson(deviceMessage);

                //System.out.println("================= JSON String IS ================");
                //System.out.println(json);
                //System.out.println("Partition Key / Device Id before inserting into Kinesis stream is : " + partitionKey);

                //System.out.println("SRIKANTH : SIZE IS : " + json.getBytes(charset).length);
                //ByteBuffer data = ByteBuffer.wrap(SerializationUtils.serialize(deviceMessage));
                ByteBuffer data = ByteBuffer.wrap(json.getBytes());
                pushToKinesis(new PutRecordsRequestEntry().withPartitionKey(partitionKey).withData(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
    private void flush() {
        System.out.println("Sending a record to Kinesis Stream with " + entries.size() + " messages grouped together.");
        kinesis.putRecords(new PutRecordsRequest()
                .withStreamName(STREAM_NAME)
                .withRecords(entries));
        entries.clear();

    }
    private void pushToKinesis(PutRecordsRequestEntry entry)
    {
        /*System.out.println("===================================================================");
        System.out.println("Data Size is : " + dataSize);
        System.out.println("Remaining Data is : " + entry.getData().remaining());
        System.out.println("Partition Key length is : " + entry.getPartitionKey().length());*/

        int newDataSize = dataSize + entry.getData().remaining() +
                entry.getPartitionKey().length();
        if (newDataSize <= 5 * 1024 * 1024 && entries.size() < 500)
        {
            dataSize = newDataSize;
            entries.add(entry);
            //System.out.println("Data size is : " + dataSize );
        }
        else {
            //System.out.println("In Else : Entries size is : " + entries.size() + " --- New Data size is ::: " + newDataSize);
            //System.out.println("Sending records to Kinesis Stream... Size is ::: " + dataSize);
            /*kinesis.putRecords(new PutRecordsRequest()
                .withStreamName(STREAM_NAME)
                .withRecords(entry));*/
            flush();
            System.out.println("Record sent to Kinesis Stream. Record size is ::: " + dataSize + " KB");
            dataSize = 0;
            pushToKinesis(entry);
        }
    }

    /*private String generateJSONObject(IOTDevice deviceMessage)
    {
        GPS gps;
        TemperatureSensor tempSensor;

        JSONObject mainObj = new JSONObject();
        mainObj.put("deviceid", deviceMessage.getDeviceId());
        mainObj.put("currentDate", deviceMessage.getCurrentDate());
        mainObj.put("accelerometerreadings", getAccelerometerReadings(deviceMessage.accelerometerSensor));
        mainObj.put("gpsreadings", getGPSReadings(deviceMessage.gpsSensor));
        mainObj.put("temperaturereadings", getTemperatureReadings(deviceMessage.tempSensor));
        mainObj.put("illuminancereadings", getIlluminanceReadings(deviceMessage.illuminancesSensor));

        return mainObj.toJSONString();
    }

    private JSONArray getAccelerometerReadings(ArrayList<Accelerometer> acc)
    {
        JSONArray accelerometerReadings = new JSONArray();
        for (Accelerometer a : acc)
        {
            JSONObject accelerometerObj = new JSONObject();

            accelerometerObj.put("accelerometer_X", a.getAccelerometer_X());

            accelerometerObj.put("accelerometer_Y", a.getAccelerometer_Y());
            accelerometerObj.put("accelerometer_Z", a.getAccelerometer_Z());

            accelerometerObj.put("linearAccelerationSensor_X", a.getLinearAccelerationSensor_X());
            accelerometerObj.put("linearAccelerationSensor_Y", a.getLinearAccelerationSensor_Y());
            accelerometerObj.put("linearAccelerationSensor_Z", a.getLinearAccelerationSensor_Z());

            accelerometerObj.put("gravitySensor_X", a.getGravitySensor_X());
            accelerometerObj.put("gravitySensor_Y", a.getGravitySensor_Y());
            accelerometerObj.put("gravitySensor_Z", a.getGravitySensor_Z());

            accelerometerReadings.add(accelerometerObj);
        }
        return accelerometerReadings;
    }

    private JSONArray getTemperatureReadings(ArrayList<TemperatureSensor> temp)
    {
        JSONArray temperatureReadings = new JSONArray();
        for (TemperatureSensor t : temp)
        {
            JSONObject accelerometerObj = new JSONObject();
            accelerometerObj.put("celcius", t.getCelsius());
            accelerometerObj.put("fahrenheit", t.getFahrenheit());
            accelerometerObj.put("kelvin", t.getKelvin());

            temperatureReadings.add(accelerometerObj);
        }
        return temperatureReadings;
    }

    private JSONArray getGPSReadings(ArrayList<GPS> gps)
    {
        JSONArray gpsReadings = new JSONArray();
        int gpsLength = gps.size();

        for (GPS g : gps)
        {
            JSONObject gpsObj = new JSONObject();
            gpsObj.put("altitude", g.getAltitude());
            gpsObj.put("heading", g.getHeading());
            gpsObj.put("lat", g.getLatitude());
            gpsObj.put("long", g.getLongitude());

            gpsReadings.add(gpsObj);
        }
        return gpsReadings;
    }

    private JSONArray getIlluminanceReadings(ArrayList<Illuminance> illuminances)
    {
        JSONArray illuminancesReadings = new JSONArray();
        int gpsLength = illuminances.size();

        for (Illuminance i : illuminances)
        {
            JSONObject illuminancesObj = new JSONObject();
            illuminancesObj.put("illuminance", i.getIlluminance());
            illuminancesReadings.add(illuminancesObj);
        }
        return illuminancesReadings;
    }*/
}
