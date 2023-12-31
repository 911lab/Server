package com.example.bleLocationSystem.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
//User Location Result
public class UserLocation {

    //측정된 위치
    private double x;
    private double y;

    //실제 위치
    //10m
    private double realLocX = 48.5;
    //best
    private double realLocY = 10;


    //worst
//    private double realLocY = (5.0*Math.sqrt(3))-1.0; //7.66


    //15m
//    private double realLocX = 15.0/2.0;

    //best
//    private double realLocY = 5.0*Math.sqrt(3)/2.0;       //4.33
    //worst
//    private double realLocY = (15.0*Math.sqrt(3)/2.0)-1.0;    //11.99

    //거리 편차
    private double distanceDev;

    private String deviceName;


    public UserLocation(double x, double y, String deviceName) {
        this.x = x;
        this.y = y;
        this.deviceName = deviceName;

//        double realLocX= UserPoint.getRealLocX();
//        double realLocY= UserPoint.getRealLocY();

//        log.info("realLocX = {}", realLocX);
//        log.info("realLocY = {}", realLocY);
        setDistanceDeviation(realLocX,realLocY);
    }

    //실제 위치와의 거리편차 구하는 함수
    public void setDistanceDeviation(double realLocX, double realLocY) {
        distanceDev = Math.sqrt( ( Math.pow( (realLocX-x), 2) ) + ( Math.pow(realLocY-y, 2) ) );
    }


}
