package com.example.bleLocationSystem.model;

import java.util.ArrayList;
import java.util.List;

public class WeightFilter {
    List<Double> queue;

    double preAvg;

    double preSum;

    int num;
    int size;

    int queueSize;
    int tempNum;

    public WeightFilter() {
        queue = new ArrayList<Double>();
        size = 5;
        preSum = 0;
        preAvg = 0;
        num = 0;
    }

    public double feedBack(double rssi) {
        preSum = 0;
        tempNum = num%size;

        queueSize = queue.size();

        if(queueSize == 0) {
            queue.add(rssi);
            return 1;
        }
        else if(queueSize < size) {
            queue.add(rssi);
            return 1;
        }
        else {
            queue.set(tempNum, 0.0);
            num++;
        }

        for(double r : queue) {
            preSum = preSum+r;
        }
        preAvg = preSum / (queue.size()-1.0);

        queue.set(tempNum, rssi);

        return calcFeedBack(preAvg, rssi);
    }

    public double calcFeedBack(double preAvg, double lastRssi) {
        double w = 0.3;

        return (preAvg*(1.0-w)) + (lastRssi*w);
    }

}
