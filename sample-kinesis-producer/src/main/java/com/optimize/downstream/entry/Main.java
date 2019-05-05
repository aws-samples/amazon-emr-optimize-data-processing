package com.optimize.downstream.entry;

import com.optimize.downstream.samplekinesisproducer.IOTDevice;
import com.optimize.downstream.samplekinesisproducer.IOTDeviceConsumerFromBlockingQueueToKinesisStreams;
import com.optimize.downstream.samplekinesisproducer.IOTDeviceProducerToBlockingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

// This will throw an error - as the messeage size will be 1054382 bytes(more than 1 MB) when we used 2300 as parameter.
// java -cp sample-kinesis-producer-1.0-SNAPSHOT-jar-with-dependencies.jar com.awsblogs.smallfiles.entry.Main 100 2300
// java -Xms1024m -Xmx3072m -XX:+UseG1GC -cp sample-kinesis-producer-1.0-SNAPSHOT-jar-with-dependencies.jar com.awsblogs.smallfiles.entry.Main 10000 2000

public class Main {
    public static void main(String [] args)
    {
        if(args.length !=1)
        {
            System.out.println("Needs an argument");
            System.out.println("Argument : Number of Messages to send to Kinesis");
            //System.out.println("2nd Argument : Number of Threads");
        }
        else {
            int numberOfSamplesInEachMessage = 2200; // Used 2200 to make the message size closer to 1 MB.
            // Will use one thread for simplicity.
            int numberOfThreads = 1; //Integer.parseInt(args[1]);
            int numberOfMessages = Integer.parseInt(args[0]);
            BlockingQueue<IOTDevice> inputQueue = new LinkedBlockingDeque();

            Thread[] consumerThread = new Thread[numberOfThreads];
            Thread producerThread = new Thread(new IOTDeviceProducerToBlockingQueue(inputQueue, numberOfMessages, numberOfSamplesInEachMessage));
            System.out.println("Starting producer and consumer.....");
            producerThread.start();

            for (int i = 0; i < numberOfThreads; i++) {
                consumerThread[i] = new Thread(new IOTDeviceConsumerFromBlockingQueueToKinesisStreams(inputQueue));
                consumerThread[i].start();
            }
        }
    }
}
