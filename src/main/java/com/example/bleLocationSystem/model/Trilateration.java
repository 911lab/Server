package com.example.bleLocationSystem.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
//삼변측량
public class Trilateration {
    private String deviceName;
    private Ap ap1;
    private Ap ap2;
    private Ap ap3;

    // 삼변측량 기법을 통한 사용자 위치 계산
    public UserLocation calcUserLocation() {
        double A = 2*(ap2.getX()-ap1.getX());
        double B = 2*(ap2.getY()-ap1.getY());
        double C = Math.pow(ap1.getDistance(), 2) - Math.pow(ap2.getDistance(),2) - Math.pow(ap1.getX(),2) + Math.pow(ap2.getX(),2) - Math.pow(ap1.getY(),2) + Math.pow(ap2.getY(),2);
        double D = 2*(ap3.getX() - ap2.getX());
        double E = 2*(ap3.getY() - ap2.getY());
        double F = Math.pow(ap2.getDistance(), 2) - Math.pow(ap3.getDistance(),2) - Math.pow(ap2.getX(),2) + Math.pow(ap3.getX(),2) - Math.pow(ap2.getY(),2) + Math.pow(ap3.getY(),2);

        double userX = ( (F * B) - (E * C) ) / ( (B * D) - (E * A) );
        double userY = ( (F * A) - (D * C) ) / ( (A * E) - (D * B) );


        log.info("AP1 = {}", ap1.toString());
        log.info("AP2 = {}", ap2.toString());
        log.info("AP3 = {}", ap3.toString());

        UserLocation ul = new UserLocation(userX, userY);


        return ul;
    }

    // 화면밖으로 넘어가는경우 화면내로 좌표이동
    public UserLocation moveUserLocation(UserLocation ul) {
        System.out.printf("Before Location : (%.2f, %.2f) \t\t", ul.getX(), ul.getY());

        if(ul.getX() < ap1.getX())
            ul.setX(ap1.getX());
        else if(ul.getX() > ap2.getX())
            ul.setX(ap2.getX());

        if(ul.getY() < ap1.getY())
            ul.setY(ap1.getY());
        else if(ul.getY() > ap3.getY())
            ul.setY(ap3.getY());

        System.out.printf("Moved Location : (%.2f, %.2f)\n", ul.getX(), ul.getY());

        return ul;
    }

}
