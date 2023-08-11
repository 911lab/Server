package com.example.bleLocationSystem.model;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Up implements Runnable {

    //실제 위치
    private double realLocX = 0;
    private double realLocY = 5;
    //끝 위치
    private double endLocX = 9;
    private double endLocY = 5;

    @Override
    public void run() {
        double start = realLocX;
        System.out.println("run start");
        for(double i = start; i <= endLocX ; i++){
//            System.out.printf("i=%f%n",i);
//            System.out.printf("x:%f, y:%f %n",x,y);
            System.out.printf("<<<  realX=%f, realY=%f   >>>%n", realLocX, realLocY);
            try {
                Thread.sleep(1000);
                realLocX++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
