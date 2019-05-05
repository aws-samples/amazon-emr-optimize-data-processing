package com.optimize.downstream.datagenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class GenerateDataMain
{

    public ArrayList<String> getDeviceIDS(int totalDeviceIds)
    {
        ArrayList<String> listOfUUIDS = new ArrayList<String>();
        for(int i =0; i< totalDeviceIds; i++)
        {
            UUID uuid = UUID.randomUUID();
            listOfUUIDS.add(uuid.toString());
        }
        return listOfUUIDS;
    }

    public void kickAllWorkers()
    {
        ArrayList<String> allDeviceIds = getDeviceIDS(5);
        List<GenerateDataWorker> workers = new ArrayList<GenerateDataWorker>();

        for (int i=0; i<allDeviceIds.size(); i++)
        {
            workers.add(new GenerateDataWorker());
        }
    }

    public static void main(String [] args)
    {
        GenerateDataMain g = new GenerateDataMain();
        g.kickAllWorkers();
    }
}
