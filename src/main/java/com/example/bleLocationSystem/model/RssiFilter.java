package com.example.bleLocationSystem.model;


import java.util.stream.DoubleStream;

public class RssiFilter {
    float tempAlpha = -59;
    int lossNum = 2;

    public void setRssiVo(double width, double height, VO beforeFilteredVo, VO originalVo) {
        double maxRadius = calcRssi(Math.max(width, height));

        System.out.printf("%.3f\t%.3f\t%.3f\n", width,height,maxRadius);

        originalVo.setRssi1(checkRssi(beforeFilteredVo.getRssi1(), originalVo.getRssi1(), maxRadius));
        originalVo.setDistance1(calcDistance(originalVo.getRssi1()));

        originalVo.setRssi2(checkRssi(beforeFilteredVo.getRssi2(), originalVo.getRssi2(), maxRadius));
        originalVo.setDistance2(calcDistance(originalVo.getRssi2()));

        originalVo.setRssi3(checkRssi(beforeFilteredVo.getRssi3(), originalVo.getRssi3(), maxRadius));
        originalVo.setDistance3(calcDistance(originalVo.getRssi3()));

        //return originalVo;
    }


    public double calcRssi(double distance) {

        double rssi = -10*lossNum*Math.log10(distance)+tempAlpha;

        return rssi;
    }


    public double calcDistance(double rssi) {

        double distance = Math.pow(10, (tempAlpha-rssi)/(10*lossNum));

        return distance;
    }


    public double checkRssi(double beforeRssi, double originalRssi, double max){

        if(Math.abs(beforeRssi-originalRssi)>20)
            return beforeRssi;

        else if(originalRssi<max)
            return max;

        return originalRssi;
    }
}
