package com.example.bleLocationSystem.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
//User Location Result
public class UserLocation {

    //측정된 위치
    private double x;
    private double y;

    //실제 위치
    private double realLocX = 5;
    private double realLocY = 5;

    //거리 편차
    private double distanceDev;

    public UserLocation(double x, double y) {
        this.x = x;
        this.y = y;

        setDistanceDeviation();
    }

    //실제 위치와의 거리편차 구하는 함수
    public void setDistanceDeviation() {
        distanceDev = Math.sqrt( ( Math.pow( (realLocX-x), 2) ) + ( Math.pow(realLocY-y, 2) ) );
    }
}