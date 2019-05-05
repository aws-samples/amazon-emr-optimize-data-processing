package com.optimize.downstream.lambda;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.PutRecordRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordResult;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.optimize.downstream.additionaldata.AdditionalIOTData;
import com.optimize.downstream.additionaldata.TestAdditonalData;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class ProcessKinesisRecords implements RequestHandler<KinesisEvent, Void>{
    private static Charset charset = Charset.forName("UTF-8");
    private String hashDeviceId;
    private String processedDate;
    private Random random = new Random();
    private Gson gson;
    private AmazonKinesisFirehoseClient kinesisFirehoseClient;
    private String firehoseStreamName = System.getenv("kinesisfirehosestream");
    private String REGION=System.getenv("kinesis_region");

    @Override
    public Void handleRequest(KinesisEvent event, Context context)
    {
        //System.out.print("In Handle Request");
        gson = new Gson();
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxErrorRetry(5);
        config.setSocketTimeout(100);
        kinesisFirehoseClient = new AmazonKinesisFirehoseClient(config);
        kinesisFirehoseClient.setRegion(Region.getRegion(Regions.fromName(REGION)));
        String mergedJsonString = "";
        String recordId;
        try {
            for (KinesisEventRecord rec : event.getRecords())
            {
                //System.out.println(new String(rec.getKinesis().getData().array()));
                String jsonMessage = new String(rec.getKinesis().getData().array());
                //System.out.println("Kinesis JSON Message is ::: ");
                //System.out.println(jsonMessage);
                AdditionalIOTData additionalDeviceMessage = generateAdditionalIOTDeviceData();
                String addJson = gson.toJson(additionalDeviceMessage);
                //System.out.println("Additional JSON Is  :: " + addJson);

                try {
                    mergedJsonString = mergeJsonStrings(jsonMessage, addJson);
                    System.out.println(mergedJsonString);
                    System.out.println("Sending record to Firehose");
                    recordId = sendToFireHose(mergedJsonString);
                    System.out.println("Record sent to Firehose. Result Record Id is : " + recordId);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception ie)
        {
            ie.getStackTrace();
        }
        return null;
    }

    private String mergeJsonStrings(String kinJsonMessage, String addJson)
    {
        JSONObject kinesisJsonObject;
        JSONObject addJsonObject;
        kinesisJsonObject = new JSONObject(kinJsonMessage);
        addJsonObject = new JSONObject(addJson);
        JSONObject mergedJson = new JSONObject();
        if (kinesisJsonObject.length()>0){
            mergedJson = new JSONObject(kinesisJsonObject, JSONObject.getNames(kinesisJsonObject));
        }
        if (addJsonObject.length()>0){
            for(String key : JSONObject.getNames(addJsonObject))
            {
                mergedJson.put(key, addJsonObject.get(key));
            }
        }
        return mergedJson.toString();
    }

    private static byte[] compressMessage(byte[] inputDataMessage) throws IOException
    {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        GZIPOutputStream output = new GZIPOutputStream(array);
        try
        {
            output.write(inputDataMessage);
            output.finish();
            output.close();
            array.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return array.toByteArray();
    }

    private AdditionalIOTData generateAdditionalIOTDeviceData()
    {
        UUID uuid = UUID.randomUUID();
        hashDeviceId = uuid.toString();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        processedDate = dateFormat.format(date);
        //AdditionalIOTData additionalIOTData = new AdditionalIOTData(hashDeviceId, processedDate, getAdditionalData());
        return (new AdditionalIOTData(hashDeviceId, processedDate, getAdditionalData()));
    }

    private ArrayList<TestAdditonalData> getAdditionalData()
    {
        ArrayList<TestAdditonalData> additionalIOTDataArrayList = new ArrayList();

        // Adding extra content to make the message size more than 1 MB.
        // The below generated data will be appended/merged with the message coming from Kineisis Stream.
        // It is just to demonstrate that if the message size is more than 1MB, we can gzip the message and send it to KinesisFirehose.
        for(int i =0;i <5000; i++)
        {
            TestAdditonalData t = new TestAdditonalData();
            t.setDimension_X(getRandomInt(10,1));
            t.setDimension_Y(getRandomInt(10,1));
            t.setDimension_Z(getRandomInt(10,1));
            additionalIOTDataArrayList.add(t);
        }
        return additionalIOTDataArrayList;
    }

    private String sendToFireHose(String mergedJsonString)
    {
        PutRecordResult res = null;
        try {
            //To Firehose -
            System.out.println("MESSAGE SIZE BEFORE COMPRESSION IS : " + mergedJsonString.toString().getBytes(charset).length);
            System.out.println("MESSAGE SIZE AFTER GZIP COMPRESSION IS : " + compressMessage(mergedJsonString.toString().getBytes(charset)).length);
            PutRecordRequest req = new PutRecordRequest()
                    .withDeliveryStreamName(firehoseStreamName);

            // Without compression - Send to Firehose
            //Record record = new Record().withData(ByteBuffer.wrap((mergedJsonString.toString() + "\r\n").getBytes()));

            // With compression - send to Firehose
            Record record = new Record().withData(ByteBuffer.wrap(compressMessage((mergedJsonString.toString() + "\r\n").getBytes())));
            req.setRecord(record);
            res = kinesisFirehoseClient.putRecord(req);
        }
        catch (IOException ie) {
            ie.printStackTrace();
        }
        return res.getRecordId();
    }
    private int getRandomInt(int max, int min)
    {
        return random.nextInt(max - min + 1) + min;
    }
}
