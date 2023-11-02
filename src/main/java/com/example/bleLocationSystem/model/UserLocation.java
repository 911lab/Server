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
    private double realLocX = 5;
    //best
    //private double realLocY = 5.0*Math.sqrt(3)/3.0;
    //worst
    private double realLocY = (5.0*Math.sqrt(3))-1.0;

    //15m
    //private double realLocX = 15.0/2.0;
    //best
    //private double realLocY = 5.0*Math.sqrt(3)/2.0;

    //worst
    //private double realLocY = (15.0*Math.sqrt(3)/2.0)-1.0;

    //거리 편차
    private double distanceDev;


    public UserLocation(double x, double y) {
        this.x = x;
        this.y = y;

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
