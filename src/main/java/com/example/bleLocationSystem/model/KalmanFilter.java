package com.example.bleLocationSystem.model;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KalmanFilter {

    //Q
    private double processNoise;
    //R
    private double measurementNoise;
    private boolean initialized;
    //st,  이전 RSSI
    private double priorRssi;
    //Pt, 이전 오차 공분산
    private double priorErrorCovariance;
    //Pt 오차 공분산
    private double errorCovariance;
    //st
    private double predictedRssi;
    //Kt
    private double kalmanGain;


    public KalmanFilter() {
        initialized = false;
        processNoise = 0.005;
        measurementNoise = 1.25;
        predictedRssi = 0;
        errorCovariance = 0;

        log.info("predictedRssi = {}, errorConvariance = {}", predictedRssi, errorCovariance);
    }

//    public KalmanFilter(double processNoise, double measurementNoise) {
//        initialized = false;
//        this.processNoise = processNoise;
//        this.measurementNoise = measurementNoise;
//        predictedRssi = 0;
//        errorCovariance = 0;
//
//        log.info("predictedRssi = {}, errorConvariance = {}", predictedRssi, errorCovariance);
//    }


    public double kalmanFiltering(double rssi) {
        if(!initialized) {
            initialized = true;
            priorRssi = rssi;
            priorErrorCovariance = 1;
        }
        else {
            priorRssi = predictedRssi;
            priorErrorCovariance = errorCovariance + processNoise;
//            log.info("priorRssi = {}, priorErrorConvariance = {}, errorConvariance = {}", priorRssi, priorErrorCovariance, errorCovariance);
        }
        kalmanGain = priorErrorCovariance / (priorErrorCovariance + measurementNoise);
//        log.info("kalmanGain = {}", kalmanGain);

        predictedRssi = priorRssi + (kalmanGain * (rssi - priorRssi));

        errorCovariance = (1-kalmanGain) * priorErrorCovariance;

        return predictedRssi;
    }
}
