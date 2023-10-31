package com.example.bleLocationSystem.model;

import java.sql.SQLOutput;

public class RemoveOutlier {

    double outlier = -86;
    double maxX = 20;
    double maxY = 20;
    double minX = -10;
    double minY = -10;
    //RSSI 이상치 제거
    public boolean rmOutlier(double rssi1, double rssi2, double rssi3){
        return !(rssi1 <= outlier) && !(rssi2 <= outlier) && !(rssi3 <= outlier);
    }

    //좌표 이상치 제거
    public boolean rmXYOutlier(UserLocation ul){
        System.out.println("x,y=\t"+ul.getX()+",\t"+ul.getY());
        if (ul.getY()>10){
            System.out.println("yCUT");
            return true;
        }
        if (ul.getY()<0){
            System.out.println("yCUT");
            return true;
        }
       if (ul.getX()>35){
            System.out.println("xCUT");
            return true;
        }
       if (ul.getX()<0) {
           System.out.println("xCUT");
           return true;
       }
        return false;
    }
}
