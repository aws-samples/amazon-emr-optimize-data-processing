package com.optimize.downstream.additionaldata;

import java.util.*;

public class AdditionalIOTData
{
    private String hashDeviceId;
    private String processedDate;
    private List<TestAdditonalData> testAdditonalDataList;
    private Random random = new Random();

    public AdditionalIOTData(String hashDeviceId,
              String processedDate,
                      List<TestAdditonalData> testAdditonalDataList)
    {
        this.hashDeviceId = hashDeviceId;
        this.processedDate = processedDate;
        this.testAdditonalDataList = testAdditonalDataList;
    }

    public String getHashDeviceId() {
        return hashDeviceId;
    }

    public String getProcessedDate() {
        return processedDate;
    }

    public List<TestAdditonalData> getTestAdditonalDataList() {
        return testAdditonalDataList;
    }
}
