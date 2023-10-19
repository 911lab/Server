package com.example.bleLocationSystem.model;

public class RemoveOutlier {

    double outlier = -86;

    //이상치 제거
    public boolean rmOutlier(double rssi1, double rssi2, double rssi3){
        return !(rssi1 <= outlier) && !(rssi2 <= outlier) && !(rssi3 <= outlier);
    }

}
