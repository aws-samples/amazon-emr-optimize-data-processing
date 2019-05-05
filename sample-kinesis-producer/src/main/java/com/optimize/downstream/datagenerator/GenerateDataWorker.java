package com.optimize.downstream.datagenerator;

import java.util.ArrayList;
import java.util.UUID;

public class GenerateDataWorker implements Runnable
{
    //DataFactory df;
    public boolean running = false;

    GenerateDataWorker()
    {
        Thread t = new Thread(this);
        t.start();
    }
    @Override
    public void run()
    {
        this.running = true;
        System.out.print("Need to generate data and insert into Blocked Queue. The Id is : " + Thread.currentThread().getId() );
    }

}
