package com.example.bleLocationSystem.model;

import java.util.ArrayList;
import java.util.List;

public class MAFilter {
    int size;
    List<Double> queue;
    double avg;

    double sum;
    int num;
    int tempNum;

    int queueSize;

    public MAFilter () {
        size = 10;
        queue = new ArrayList<Double>();
        sum = 0;
        avg = 0;
        num = 0;
    }

    public double push(double rssi) {
        sum = 0;
        tempNum = num%10;

        queueSize = queue.size();

        if(queueSize < size) {
            queue.add(rssi);
        }
        else {
            queue.set(tempNum, rssi);
            num++;
        }

        for(double r : queue) {
            sum = sum+r;
        }

        avg = sum / queue.size();

        return avg;
    }
}