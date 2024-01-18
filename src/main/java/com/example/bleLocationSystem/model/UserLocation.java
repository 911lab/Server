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
    //1
//    private double realLocX = 1;
//    private double realLocY = 1;

    //2
//    private double realLocX = 7.5;
//    private double realLocY = 12;

    //3
//    private double realLocX = 15;
//    private double realLocY = 1;

    //4
//    private double realLocX = 22.5;
//    private double realLocY = 12;

    //5
//    private double realLocX = 30;
//    private double realLocY = 1;

    //6
//    private double realLocX = 37.5;
//    private double realLocY = 12;

    //7
//    private double realLocX = 45;
//    private double realLocY = 1;

    //8
//    private double realLocX = 51.5;
//    private double realLocY = 12;

    //9
//    private double realLocX = 45;
//    private double realLocY = 6.5;

    //10
//    private double realLocX = 37.5;
//    private double realLocY = 6.5;

    //11
//    private double realLocX = 30;
//    private double realLocY = 6.5;

    //12
//    private double realLocX = 22.5;
//    private double realLocY = 6.5;

    //13
//    private double realLocX = 15;
//    private double realLocY = 6.5;

    //14
//    private double realLocX = 7.5;
//    private double realLocY = 6.5;

    //15
//    private double realLocX = 4;
//    private double realLocY = 3;

    //16
//    private double realLocX = 15;
//    private double realLocY = 10;

    //17
//    private double realLocX = 26;
//    private double realLocY = 3;

    //18
//    private double realLocX = 26;
//    private double realLocY = 10;

    //19
//    private double realLocX = 37.5;
//    private double realLocY = 3;

    //20
    private double realLocX = 48.5;
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
