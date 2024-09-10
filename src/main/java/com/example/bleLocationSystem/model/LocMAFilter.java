package com.example.bleLocationSystem.model;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LocMAFilter {
    int size;

    List<Double> xQueue;
    List<Double> yQueue;

    double xAvg;
    double yAvg;

    double xSum;
    double ySum;

    int queueSize;

    public LocMAFilter() {
        size = 5;
        xQueue = new ArrayList<Double>();
        yQueue = new ArrayList<Double>();

        xSum = 0;
        ySum = 0;

        xAvg = 0;
        yAvg = 0;

    }

    public LocMAFilter(int size) {
        this.size = size;
        xQueue = new ArrayList<Double>();
        yQueue = new ArrayList<Double>();

        xSum = 0;
        ySum = 0;

        xAvg = 0;
        yAvg = 0;

    }

    public UserLocation push(UserLocation tempUl) {
        xSum = 0;
        ySum = 0;

        xQueue.add(tempUl.getX());
        yQueue.add(tempUl.getY());

        queueSize = xQueue.size();

//        log.info("queue size = {}", queueSize);

        if(queueSize < size) {
            return null;
        }

        for(int i=0; i<queueSize; i++) {
            xSum = xSum + xQueue.get(i);
            ySum = ySum + yQueue.get(i);
        }

        xAvg = xSum / queueSize;
        yAvg = ySum / queueSize;

        xQueue.clear();
        yQueue.clear();


        xQueue.add(xAvg);
        yQueue.add(yAvg);

        return new UserLocation(xAvg, yAvg, tempUl.getDeviceName());
    }
}
