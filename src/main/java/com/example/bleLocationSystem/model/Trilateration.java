package com.example.bleLocationSystem.model;

import lombok.*;
import org.apache.catalina.User;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
//삼변측량
public class Trilateration {
    private String deviceName;
    private Ap ap1;
    private Ap ap2;
    private Ap ap3;

    // 삼변측량 기법을 통한 사용자 위치 계산
    public UserLocation calcUserLocation(Up UserPoint) {
        double A = 2*(ap2.getX()-ap1.getX());
        double B = 2*(ap2.getY()-ap1.getY());
        double C = Math.pow(ap1.getDistance(), 2) - Math.pow(ap2.getDistance(),2) - Math.pow(ap1.getX(),2) + Math.pow(ap2.getX(),2) - Math.pow(ap1.getY(),2) + Math.pow(ap2.getY(),2);
        double D = 2*(ap3.getX() - ap2.getX());
        double E = 2*(ap3.getY() - ap2.getY());
        double F = Math.pow(ap2.getDistance(), 2) - Math.pow(ap3.getDistance(),2) - Math.pow(ap2.getX(),2) + Math.pow(ap3.getX(),2) - Math.pow(ap2.getY(),2) + Math.pow(ap3.getY(),2);

        double userX = ( (F * B) - (E * C) ) / ( (B * D) - (E * A) );
        double userY = ( (F * A) - (D * C) ) / ( (A * E) - (D * B) );



        UserLocation ul = new UserLocation(userX, userY, UserPoint);


        return ul;
    }



}
