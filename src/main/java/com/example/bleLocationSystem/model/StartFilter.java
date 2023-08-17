package com.example.bleLocationSystem.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StartFilter {

    VO firstVo;

    List<Double> rssi1;
    List<Double> rssi2;
    List<Double> rssi3;

    float tempAlpha;
    int lossNum;

    int count;

    public StartFilter() {
        rssi1 = new ArrayList<Double>();
        rssi2 = new ArrayList<Double>();
        rssi3 = new ArrayList<Double>();
        count = 0;
    }

    public VO initFirstValue(VO originalVo, int num) {

        rssi1.add(originalVo.getRssi1());
        rssi2.add(originalVo.getRssi2());
        rssi3.add(originalVo.getRssi3());

        if (num == 10) {
            firstVo = setFirstVo(getMaxRssi(rssi1), getMaxRssi(rssi2), getMaxRssi(rssi3));
            return firstVo;
        }
        return null;
    }

    public double getMaxRssi(List<Double> rssi) {
        double maxRssi = Collections.max(rssi);

        return maxRssi;
    }

    public VO setFirstVo(double rssi1, double rssi2, double rssi3) {
        VO vo = new VO();

        vo.setDeviceName("dddddddddd");
        vo.setRssi1(rssi1);
        vo.setDistance1(calcDistance(rssi1));

        vo.setRssi2(rssi2);
        vo.setDistance2(calcDistance(rssi2));

        vo.setRssi3(rssi3);
        vo.setDistance3(calcDistance(rssi3));

        return vo;
    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -59;
        lossNum = 2;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }






}
