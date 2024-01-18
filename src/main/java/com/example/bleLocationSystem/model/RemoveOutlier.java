package com.example.bleLocationSystem.model;

import java.sql.SQLOutput;

public class RemoveOutlier {

    double outlier;
    double maxX = 20;
    double maxY = 20;
    double minX = -10;
    double minY = -10;
    //RSSI 이상치 제거
    public boolean rmOutlier(double rssi1, double rssi2, double rssi3, double out){
        outlier = out;
        return !(rssi1 <= outlier) && !(rssi2 <= outlier) && !(rssi3 <= outlier) && !(rssi1 > 0) && !(rssi2 > 0) && !(rssi3 > 0);
    }

    public boolean rssiOutlier(double rssi, double out) {
        outlier = out;
        return (rssi > outlier) && (rssi < 0) ;
    }

    //좌표 이상치 제거
    public boolean rmXYOutlier(UserLocation ul, double width, double height){
        //System.out.printf("Before RO Location : (%.2f, %.2f)\n", ul.getX(), ul.getY());
        if (ul.getY()>height){
            System.out.println("yCUT");
            return true;
        }
        if (ul.getY()<0){
            System.out.println("yCUT");
            return true;
        }
        if (ul.getX()> ((width*3.0) + width/2.0)){
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
