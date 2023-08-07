package com.example.bleLocationSystem.model;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
//Anchor Point (비콘 Data)
public class Ap {
    private double x;
    private double y;
    private double distance;    //수신기와의 거리

    public Ap(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
