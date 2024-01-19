package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;

import java.util.ArrayList;

public class DynamicTestService {

    RemoveOutlier rm;
    StartFilter startFilter1;
    StartFilter startFilter2;
    StartFilter startFilter3;
    StartFilter startFilter4;
    StartFilter startFilter5;
    StartFilter startFilter6;
    StartFilter startFilter7;
    StartFilter startFilter8;
    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;
    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;
    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;

    LocMAFilter locMAFilter1;
    LocMAFilter locMAFilter2;

    VO originalVo;
    VO removedVo;
    VO kalmanVo;

    SelectedVO selectedVO;

    Ap ap1;
    Ap ap2;
    Ap ap3;

    UserLocation ul;
    UserLocation filteredUl;


    // 1m=-23, n=4.68 : 15m =  -78.0411
    double outlier15m = -78.0411;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99
    private double tempAlpha;
    private double lossNum;
    int i=0;

    ArrayList<UserLocation> ulList;

    public DynamicTestService() {
        ulList = new ArrayList<UserLocation>();

        rm = new RemoveOutlier();
        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();
        kFilterForAp4 = new KalmanFilter();
        kFilterForAp5 = new KalmanFilter();
        kFilterForAp6 = new KalmanFilter();
        kFilterForAp7 = new KalmanFilter();
        kFilterForAp8 = new KalmanFilter();

        startFilter1 = new StartFilter();
        startFilter2 = new StartFilter();
        startFilter3 = new StartFilter();
        startFilter4 = new StartFilter();
        startFilter5 = new StartFilter();
        startFilter6 = new StartFilter();
        startFilter7 = new StartFilter();
        startFilter8 = new StartFilter();

        locMAFilter1 = new LocMAFilter();
        locMAFilter2 = new LocMAFilter();

    }

    public ArrayList<UserLocation> trilateration(VO vo) {
        i++;

        return ulList;

    }

}
