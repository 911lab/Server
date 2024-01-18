package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ThresholdTestService {

    ExelPOIHelper poiHelper;

    private double tempAlpha;
    private double lossNum;

    int checkProximityNumfor2m;
    int checkProximityNumfor2_5m;
    int checkProximityNumfor3m;
    int checkProximityNumfor3_5m;
    int checkProximityNumfor4m;
    int checkProximityNumfor4_5m;
    int checkProximityNumfor5m;


    VO originalVo;
    VO removedVo;
    VO kalmanVo;

    SelectedVO originalSelectedVo;
    SelectedVO kalmanSelectedVo;

    SelectedVO selectedVOfor2m;
    SelectedVO selectedVOfor2_5m;
    SelectedVO selectedVOfor3m;
    SelectedVO selectedVOfor3_5m;
    SelectedVO selectedVOfor4m;
    SelectedVO selectedVOfor4_5m;
    SelectedVO selectedVOfor5m;

    UserLocation originalUl;
    UserLocation kalmanUl;
    UserLocation proximityUlfor2m;
    UserLocation proximityUlfor2_5m;
    UserLocation proximityUlfor3m;
    UserLocation proximityUlfor3_5m;
    UserLocation proximityUlfor4m;
    UserLocation proximityUlfor4_5m;
    UserLocation proximityUlfor5m;

    UserLocation locfilteredProximityUlfor2m;
    UserLocation locfilteredProximityUlfor2_5m;
    UserLocation locfilteredProximityUlfor3m;
    UserLocation locfilteredProximityUlfor3_5m;
    UserLocation locfilteredProximityUlfor4m;
    UserLocation locfilteredProximityUlfor4_5m;
    UserLocation locfilteredProximityUlfor5m;





    @Getter
    int originalTriangleNum;

    @Getter
    int kalmanTriangleNum;

    // 1m=-23, n=4.68 : 15m =  -78.0411
    double outlier15m = -78.0411;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0 * Math.sqrt(3) / 2;  //12.99

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap ap1forKalman;
    Ap ap2forKalman;
    Ap ap3forKalman;

    Ap proximityApfor2m;
    Ap proximityApfor2_5m;
    Ap proximityApfor3m;
    Ap proximityApfor3_5m;
    Ap proximityApfor4m;
    Ap proximityApfor4_5m;
    Ap proximityApfor5m;



    RemoveOutlier rm;
    StartFilter startFilter1;
    StartFilter startFilter2;
    StartFilter startFilter3;
    StartFilter startFilter4;
    StartFilter startFilter5;
    StartFilter startFilter6;
    StartFilter startFilter7;
    StartFilter startFilter8;


    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;


    LocMAFilter locMAFilter1;
    LocMAFilter locMAFilter2;
    LocMAFilter locMAFilter3;
    LocMAFilter locMAFilter4;
    LocMAFilter locMAFilter5;
    LocMAFilter locMAFilter6;
    LocMAFilter locMAFilter7;



    double[][] xfor2m;
    double[][] x2for2m;
    double[][] tempArrfor2m;

    double[][] xfor2_5m;
    double[][] x2for2_5m;
    double[][] tempArrfor2_5m;

    double[][] xfor3m;
    double[][] x2for3m;
    double[][] tempArrfor3m;

    double[][] xfor3_5m;
    double[][] x2for3_5m;
    double[][] tempArrfor3_5m;

    double[][] xfor4m;
    double[][] x2for4m;
    double[][] tempArrfor4m;

    double[][] xfor4_5m;
    double[][] x2for4_5m;
    double[][] tempArrfor4_5m;

    double[][] xfor5m;
    double[][] x2for5m;
    double[][] tempArrfor5m;

    ArrayList<UserLocation> ulList;

    int totalNum;
    int i;

    // 1m = 23, n=4.68 일때 2m = -37.09
    // 1m = 23, n=4.68 일때 2.5m = -41.62
    // 1m = 23, n=4.68 일때 3m = -45.33
    // 1m = 23, n=4.68 일때 3.5m = -48.46
    // 1m = 23, n=4.68 일때 4m = -51.18
    // 1m = 23, n=4.68 일때 4.5m = -53.57
    // 1m = 23, n=4.68 일때 5m = -55.71

    double threshold2m = -37.09;
    double threshold2_5m = -41.62;
    double threshold3m = -45.33;
    double threshold3_5m = -48.46;
    double threshold4m = -51.18;
    double threshold4_5m = -53.57;
    double threshold5m = -55.71;

    public ThresholdTestService() {
        poiHelper = new ExelPOIHelper();

        //RSSI 보정 프로세스
        startFilter1 = new StartFilter();
        startFilter2 = new StartFilter();
        startFilter3 = new StartFilter();
        startFilter4 = new StartFilter();
        startFilter5 = new StartFilter();
        startFilter6 = new StartFilter();
        startFilter7 = new StartFilter();
        startFilter8 = new StartFilter();

        rm = new RemoveOutlier();

        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();
        kFilterForAp4 = new KalmanFilter();
        kFilterForAp5 = new KalmanFilter();
        kFilterForAp6 = new KalmanFilter();
        kFilterForAp7 = new KalmanFilter();
        kFilterForAp8 = new KalmanFilter();

        //위치 보정 프로세스
        locMAFilter1 = new LocMAFilter();
        locMAFilter2 = new LocMAFilter();
        locMAFilter3 = new LocMAFilter();
        locMAFilter4 = new LocMAFilter();
        locMAFilter5 = new LocMAFilter();
        locMAFilter6 = new LocMAFilter();
        locMAFilter7 = new LocMAFilter();


        ulList = new ArrayList<UserLocation>();

        i=0;
        totalNum = 0;
    }

    public ArrayList<UserLocation> trilateration(VO vo) {

        i++;

        if (i <= 30) {
            System.out.printf("i = { %d }\n", i);
            double rssi1 = startFilter1.startFilterling(vo.getRssi1(), i);
            double rssi2 = startFilter2.startFilterling(vo.getRssi2(), i);
            double rssi3 = startFilter3.startFilterling(vo.getRssi3(), i);
            double rssi4 = startFilter4.startFilterling(vo.getRssi4(), i);
            double rssi5 = startFilter5.startFilterling(vo.getRssi5(), i);
            double rssi6 = startFilter6.startFilterling(vo.getRssi6(), i);
            double rssi7 = startFilter7.startFilterling(vo.getRssi7(), i);
            double rssi8 = startFilter8.startFilterling(vo.getRssi8(), i);

            if(i == 30) {
                originalVo = createVO(vo.getDeviceName(), rssi1, rssi2, rssi3, rssi4, rssi5, rssi6, rssi7, rssi8);
            }
            else {
                System.out.println("Not Yet !!!");
                return null;
            }
        }
        else {
            System.out.printf("Total Num : %d\n", i);
            originalVo = vo;
        }

        removedVo = removeOutlier(originalVo);
        kalmanVo = rssiKalmanFilter(removedVo);

        //============================================================ Original ============================================================
        originalTriangleNum = selectTriangle(originalVo);

        switch (originalTriangleNum) {
            case 0:
                originalSelectedVo = null;
                break;
            case 1:
                originalSelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                break;
            case 2:
                originalSelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                break;
            case 3:
                originalSelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                break;
            case 4:
                originalSelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                break;
            case 5:
                originalSelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                break;
            case 6:
                originalSelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                break;
        }

        if(originalTriangleNum != 0) {
            if (originalTriangleNum % 2 == 0) {
                ap1 = new Ap((w / 2.0) * (originalTriangleNum - 1), h, originalSelectedVo.getDistance1());
                ap2 = new Ap((w / 2.0) * originalTriangleNum, 0, originalSelectedVo.getDistance2());
                ap3 = new Ap((w / 2.0) * (originalTriangleNum + 1), h, originalSelectedVo.getDistance3());
            } else {
                ap1 = new Ap((w / 2.0) * (originalTriangleNum - 1), 0, originalSelectedVo.getDistance1());
                ap2 = new Ap((w / 2.0) * originalTriangleNum, h, originalSelectedVo.getDistance2());
                ap3 = new Ap((w / 2.0) * (originalTriangleNum + 1), 0, originalSelectedVo.getDistance3());
            }

            Trilateration originalTr = new Trilateration(originalSelectedVo.getDeviceName(), ap1, ap2, ap3);
            originalUl = originalTr.calcUserLocation();
        }
        else {
            originalUl = new UserLocation(999, 999, "ddd");
        }

        //============================================================ Kalman && Loc Filtered ============================================================
        kalmanTriangleNum = selectTriangle(kalmanVo);
        switch (kalmanTriangleNum) {
            case 0:
                kalmanSelectedVo = null;
                break;
            case 1:
                kalmanSelectedVo = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                break;
            case 2:
                kalmanSelectedVo = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                break;
            case 3:
                kalmanSelectedVo = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                break;
            case 4:
                kalmanSelectedVo = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                break;
            case 5:
                kalmanSelectedVo = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                break;
            case 6:
                kalmanSelectedVo = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                break;
        }
        if(kalmanTriangleNum != 0) {
            if (kalmanTriangleNum % 2 == 0) {
                ap1forKalman = new Ap((w / 2.0) * (kalmanTriangleNum - 1), h, kalmanSelectedVo.getDistance1());
                ap2forKalman = new Ap((w / 2.0) * kalmanTriangleNum, 0, kalmanSelectedVo.getDistance2());
                ap3forKalman = new Ap((w / 2.0) * (kalmanTriangleNum + 1), h, kalmanSelectedVo.getDistance3());
            } else {
                ap1forKalman = new Ap((w / 2.0) * (kalmanTriangleNum - 1), 0, kalmanSelectedVo.getDistance1());
                ap2forKalman = new Ap((w / 2.0) * kalmanTriangleNum, h, kalmanSelectedVo.getDistance2());
                ap3forKalman = new Ap((w / 2.0) * (kalmanTriangleNum + 1), 0, kalmanSelectedVo.getDistance3());
            }
            Trilateration kalmanTr = new Trilateration(kalmanSelectedVo.getDeviceName(), ap1forKalman, ap2forKalman, ap3forKalman);
            kalmanUl = kalmanTr.calcUserLocation();
        }
        else {
            kalmanUl = new UserLocation(999, 999, "ddd");
        }

//--------------------------------------------------------------Proximity d = 2m  n = 1--------------------------------------------------------------

        checkProximityNumfor2m = checkProximity(kalmanVo, 1);

        if(checkProximityNumfor2m != 0) {
            switch (checkProximityNumfor2m) {
                case 0:
                    selectedVOfor2m = null;
                    break;
                case 1: case 2:
                    selectedVOfor2m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor2m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor2m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor2m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor2m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor2m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor2m == 1) {
                proximityApfor2m = new Ap(0, 0, selectedVOfor2m.getDistance1());
            } else if (checkProximityNumfor2m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor2m = new Ap((w / 2.0) * (checkProximityNumfor2m - 1), h, selectedVOfor2m.getDistance3());
            } else if (checkProximityNumfor2m % 2 == 0) {
                proximityApfor2m = new Ap((w / 2.0) * (checkProximityNumfor2m - 1), h, selectedVOfor2m.getDistance2());
            } else {
                proximityApfor2m = new Ap((w / 2.0) * (checkProximityNumfor2m - 1), 0, selectedVOfor2m.getDistance2());
            }

            Trilateration proximityTrfor2m = new Trilateration(selectedVOfor2m.getDeviceName(), proximityApfor2m, checkProximityNumfor2m);
            proximityUlfor2m = proximityTrfor2m.calcProximityLocation();
        }
        else {
            proximityUlfor2m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor2m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor2m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor2m = locMAFilter1.push(proximityUlfor2m);

            if(locfilteredProximityUlfor2m == null) {
                locfilteredProximityUlfor2m = new UserLocation(999, 999, "ddd");
            }
        }
//--------------------------------------------------------------Proximity d = 2.5m  n = 2--------------------------------------------------------------
        checkProximityNumfor2_5m = checkProximity(kalmanVo, 2);

        if(checkProximityNumfor2_5m != 0) {
            switch (checkProximityNumfor2_5m) {
                case 0:
                    selectedVOfor2_5m = null;
                    break;
                case 1: case 2:
                    selectedVOfor2_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor2_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor2_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor2_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor2_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor2_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor2_5m == 1) {
                proximityApfor2_5m = new Ap(0, 0, selectedVOfor2_5m.getDistance1());
            } else if (checkProximityNumfor2_5m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor2_5m = new Ap((w / 2.0) * (checkProximityNumfor2_5m - 1), h, selectedVOfor2_5m.getDistance3());
            } else if (checkProximityNumfor2_5m % 2 == 0) {
                proximityApfor2_5m = new Ap((w / 2.0) * (checkProximityNumfor2_5m - 1), h, selectedVOfor2_5m.getDistance2());
            } else {
                proximityApfor2_5m = new Ap((w / 2.0) * (checkProximityNumfor2_5m - 1), 0, selectedVOfor2_5m.getDistance2());
            }

            Trilateration proximityTrfor2_5m = new Trilateration(selectedVOfor2_5m.getDeviceName(), proximityApfor2_5m, checkProximityNumfor2_5m);
            proximityUlfor2_5m = proximityTrfor2_5m.calcProximityLocation();
        }
        else {
            proximityUlfor2_5m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor2_5m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor2_5m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor2_5m = locMAFilter2.push(proximityUlfor2_5m);

            if(locfilteredProximityUlfor2_5m == null) {
                locfilteredProximityUlfor2_5m = new UserLocation(999, 999, "ddd");
            }
        }
//--------------------------------------------------------------Proximity d = 3m  n = 3--------------------------------------------------------------
        checkProximityNumfor3m = checkProximity(kalmanVo, 3);

        if(checkProximityNumfor3m != 0) {
            switch (checkProximityNumfor3m) {
                case 0:
                    selectedVOfor3m = null;
                    break;
                case 1: case 2:
                    selectedVOfor3m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor3m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor3m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor3m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor3m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor3m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor3m == 1) {
                proximityApfor3m = new Ap(0, 0, selectedVOfor3m.getDistance1());
            } else if (checkProximityNumfor3m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor3m = new Ap((w / 2.0) * (checkProximityNumfor3m - 1), h, selectedVOfor3m.getDistance3());
            } else if (checkProximityNumfor3m % 2 == 0) {
                proximityApfor3m = new Ap((w / 2.0) * (checkProximityNumfor3m - 1), h, selectedVOfor3m.getDistance2());
            } else {
                proximityApfor3m = new Ap((w / 2.0) * (checkProximityNumfor3m - 1), 0, selectedVOfor3m.getDistance2());
            }

            Trilateration proximityTrfor3m = new Trilateration(selectedVOfor3m.getDeviceName(), proximityApfor3m, checkProximityNumfor3m);
            proximityUlfor3m = proximityTrfor3m.calcProximityLocation();
        }
        else {
            proximityUlfor3m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor3m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor3m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor3m = locMAFilter3.push(proximityUlfor3m);

            if(locfilteredProximityUlfor3m == null) {
                locfilteredProximityUlfor3m = new UserLocation(999, 999, "ddd");
            }
        }

//--------------------------------------------------------------Proximity d = 3.5m  n = 4--------------------------------------------------------------
        checkProximityNumfor3_5m = checkProximity(kalmanVo, 4);

        if(checkProximityNumfor3_5m != 0) {
            switch (checkProximityNumfor3_5m) {
                case 0:
                    selectedVOfor3_5m = null;
                    break;
                case 1: case 2:
                    selectedVOfor3_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor3_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor3_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor3_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor3_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor3_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor3_5m == 1) {
                proximityApfor3_5m = new Ap(0, 0, selectedVOfor3_5m.getDistance1());
            } else if (checkProximityNumfor3_5m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor3_5m = new Ap((w / 2.0) * (checkProximityNumfor3_5m - 1), h, selectedVOfor3_5m.getDistance3());
            } else if (checkProximityNumfor3_5m % 2 == 0) {
                proximityApfor3_5m = new Ap((w / 2.0) * (checkProximityNumfor3_5m - 1), h, selectedVOfor3_5m.getDistance2());
            } else {
                proximityApfor3_5m = new Ap((w / 2.0) * (checkProximityNumfor3_5m - 1), 0, selectedVOfor3_5m.getDistance2());
            }

            Trilateration proximityTrfor3_5m = new Trilateration(selectedVOfor3_5m.getDeviceName(), proximityApfor3_5m, checkProximityNumfor3_5m);
            proximityUlfor3_5m = proximityTrfor3_5m.calcProximityLocation();
        }
        else {
            proximityUlfor3_5m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor3_5m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor3_5m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor3_5m = locMAFilter4.push(proximityUlfor3_5m);

            if(locfilteredProximityUlfor3_5m == null) {
                locfilteredProximityUlfor3_5m = new UserLocation(999, 999, "ddd");
            }
        }
//--------------------------------------------------------------Proximity d = 4m  n = 5--------------------------------------------------------------
        checkProximityNumfor4m = checkProximity(kalmanVo, 5);

        if(checkProximityNumfor4m != 0) {
            switch (checkProximityNumfor4m) {
                case 0:
                    selectedVOfor4m = null;
                    break;
                case 1: case 2:
                    selectedVOfor4m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor4m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor4m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor4m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor4m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor4m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor4m == 1) {
                proximityApfor4m = new Ap(0, 0, selectedVOfor4m.getDistance1());
            } else if (checkProximityNumfor4m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor4m = new Ap((w / 2.0) * (checkProximityNumfor4m - 1), h, selectedVOfor4m.getDistance3());
            } else if (checkProximityNumfor4m % 2 == 0) {
                proximityApfor4m = new Ap((w / 2.0) * (checkProximityNumfor4m - 1), h, selectedVOfor4m.getDistance2());
            } else {
                proximityApfor4m = new Ap((w / 2.0) * (checkProximityNumfor4m - 1), 0, selectedVOfor4m.getDistance2());
            }

            Trilateration proximityTrfor4m = new Trilateration(selectedVOfor4m.getDeviceName(), proximityApfor4m, checkProximityNumfor4m);
            proximityUlfor4m = proximityTrfor4m.calcProximityLocation();
        }
        else {
            proximityUlfor4m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor4m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor4m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor4m = locMAFilter5.push(proximityUlfor4m);

            if(locfilteredProximityUlfor4m == null) {
                locfilteredProximityUlfor4m = new UserLocation(999, 999, "ddd");
            }
        }

//--------------------------------------------------------------Proximity d = 4.5m  n = 6--------------------------------------------------------------
        checkProximityNumfor4_5m = checkProximity(kalmanVo, 6);

        if(checkProximityNumfor4_5m != 0) {
            switch (checkProximityNumfor4_5m) {
                case 0:
                    selectedVOfor4_5m = null;
                    break;
                case 1: case 2:
                    selectedVOfor4_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor4_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor4_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor4_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor4_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor4_5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor4_5m == 1) {
                proximityApfor4_5m = new Ap(0, 0, selectedVOfor4_5m.getDistance1());
            } else if (checkProximityNumfor4_5m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor4_5m = new Ap((w / 2.0) * (checkProximityNumfor4_5m - 1), h, selectedVOfor4_5m.getDistance3());
            } else if (checkProximityNumfor4_5m % 2 == 0) {
                proximityApfor4_5m = new Ap((w / 2.0) * (checkProximityNumfor4_5m - 1), h, selectedVOfor4_5m.getDistance2());
            } else {
                proximityApfor4_5m = new Ap((w / 2.0) * (checkProximityNumfor4_5m - 1), 0, selectedVOfor4_5m.getDistance2());
            }

            Trilateration proximityTrfor4_5m = new Trilateration(selectedVOfor4_5m.getDeviceName(), proximityApfor4_5m, checkProximityNumfor4_5m);
            proximityUlfor4_5m = proximityTrfor4_5m.calcProximityLocation();
        }
        else {
            proximityUlfor4_5m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor4_5m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor4_5m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor4_5m = locMAFilter6.push(proximityUlfor4_5m);

            if(locfilteredProximityUlfor4_5m == null) {
                locfilteredProximityUlfor4_5m = new UserLocation(999, 999, "ddd");
            }
        }

//--------------------------------------------------------------Proximity d = 5m  n = 7--------------------------------------------------------------
        checkProximityNumfor5m = checkProximity(kalmanVo, 7);

        if(checkProximityNumfor5m != 0) {
            switch (checkProximityNumfor5m) {
                case 0:
                    selectedVOfor5m = null;
                    break;
                case 1: case 2:
                    selectedVOfor5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());
                    break;
                case 3:
                    selectedVOfor5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi2(), kalmanVo.getRssi3(), kalmanVo.getRssi4());
                    break;
                case 4:
                    selectedVOfor5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi3(), kalmanVo.getRssi4(), kalmanVo.getRssi5());
                    break;
                case 5:
                    selectedVOfor5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi4(), kalmanVo.getRssi5(), kalmanVo.getRssi6());
                    break;
                case 6:
                    selectedVOfor5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi5(), kalmanVo.getRssi6(), kalmanVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVOfor5m = createSelectVO(kalmanVo.getDeviceName(), kalmanVo.getRssi6(), kalmanVo.getRssi7(), kalmanVo.getRssi8());
                    break;
            }

            if (checkProximityNumfor5m == 1) {
                proximityApfor5m = new Ap(0, 0, selectedVOfor5m.getDistance1());
            } else if (checkProximityNumfor5m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                proximityApfor5m = new Ap((w / 2.0) * (checkProximityNumfor5m - 1), h, selectedVOfor5m.getDistance3());
            } else if (checkProximityNumfor5m % 2 == 0) {
                proximityApfor5m = new Ap((w / 2.0) * (checkProximityNumfor5m - 1), h, selectedVOfor5m.getDistance2());
            } else {
                proximityApfor5m = new Ap((w / 2.0) * (checkProximityNumfor5m - 1), 0, selectedVOfor5m.getDistance2());
            }

            Trilateration proximityTrfor5m = new Trilateration(selectedVOfor5m.getDeviceName(), proximityApfor5m, checkProximityNumfor5m);
            proximityUlfor5m = proximityTrfor5m.calcProximityLocation();
        }
        else {
            proximityUlfor5m = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(proximityUlfor5m, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUlfor5m = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUlfor5m = locMAFilter7.push(proximityUlfor5m);

            if(locfilteredProximityUlfor5m == null) {
                locfilteredProximityUlfor5m = new UserLocation(999, 999, "ddd");
            }
        }

        // End
        //==============================================================================================================================================================================
        //==============================================================================================================================================================================
        //==============================================================================================================================================================================

        System.out.printf("Original Location : (%.2f, %.2f)  Dev : %.2fm\n", originalUl.getX(), originalUl.getY(), originalUl.getDistanceDev());
        System.out.printf("Threshold 2m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor2m.getX(), locfilteredProximityUlfor2m.getY(), locfilteredProximityUlfor2m.getDistanceDev());
        System.out.printf("Threshold 2.5m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor2_5m.getX(), locfilteredProximityUlfor2_5m.getY(), locfilteredProximityUlfor2_5m.getDistanceDev());
        System.out.printf("Threshold 3m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor3m.getX(), locfilteredProximityUlfor3m.getY(), locfilteredProximityUlfor3m.getDistanceDev());
        System.out.printf("Threshold 3.5m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor3_5m.getX(), locfilteredProximityUlfor3_5m.getY(), locfilteredProximityUlfor3_5m.getDistanceDev());
        System.out.printf("Threshold 4m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor4m.getX(), locfilteredProximityUlfor4m.getY(), locfilteredProximityUlfor4m.getDistanceDev());
        System.out.printf("Threshold 4.5m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor4_5m.getX(), locfilteredProximityUlfor4_5m.getY(), locfilteredProximityUlfor4_5m.getDistanceDev());
        System.out.printf("Threshold 5m Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUlfor5m.getX(), locfilteredProximityUlfor5m.getY(), locfilteredProximityUlfor5m.getDistanceDev());


        createThresholdTestCsv(originalUl, locfilteredProximityUlfor2m, locfilteredProximityUlfor2_5m, locfilteredProximityUlfor3m, locfilteredProximityUlfor3_5m, locfilteredProximityUlfor4m, locfilteredProximityUlfor4_5m, locfilteredProximityUlfor5m);

        totalNum++;

        if(totalNum==1){
            ulList.add(0, locfilteredProximityUlfor2m);
            ulList.add(1, locfilteredProximityUlfor2_5m);
            ulList.add(2, locfilteredProximityUlfor3m);
            ulList.add(3, locfilteredProximityUlfor3_5m);
        }
        else{
            ulList.set(0, locfilteredProximityUlfor2m);
            ulList.set(1, locfilteredProximityUlfor2_5m);
            ulList.set(2, locfilteredProximityUlfor3m);
            ulList.set(3, locfilteredProximityUlfor3_5m);
        }

        return ulList;


    }


    private int checkProximity(VO originalVo, int n) {

        VO vo = originalVo;
        double errorValue = -999.9;
        double valueTemp;
        int keyTemp;

        double valueTemp2;
        int keyTemp2;

        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, vo.getRssi1());
        map.put(2, vo.getRssi2());
        map.put(3, vo.getRssi3());
        map.put(4, vo.getRssi4());
        map.put(5, vo.getRssi5());
        map.put(6, vo.getRssi6());
        map.put(7, vo.getRssi7());
        map.put(8, vo.getRssi8());

        for(int i=0; i<8; i++) {
            if( map.get(i+1) > 0 ) {
                map.put(i+1, errorValue);
            }
        }

        valueTemp = map.get(1);
        keyTemp=1;

        for(int i = 2; i<9; i++) {
            if(valueTemp < map.get(i) && 0>map.get(i)) {
                keyTemp = i;
                valueTemp = map.get(i);
            }
        }

        if(keyTemp==1) {
            keyTemp2 = 2;
            valueTemp2 = map.get(2);

            for(int i = 3; i<9; i++) {

                if(valueTemp < map.get(i) && 0>map.get(i)) {
                    keyTemp2 = i;
                    valueTemp2 = map.get(i);
                }
            }

        } else {
            keyTemp2=1;
            valueTemp2 = map.get(1);

            for(int i = 2; i<9; i++) {

                if(valueTemp < map.get(i) && 0>map.get(i) && keyTemp != i) {
                    keyTemp2 = i;
                    valueTemp2 = map.get(i);
                }
            }
        }

        // 1m = 23, n=4.68 일때 2m = -37.09
        // 1m = 23, n=4.68 일때 2.5m = -41.62
        // 1m = 23, n=4.68 일때 3m = -45.33
        // 1m = 23, n=4.68 일때 3.5m = -48.46
        // 1m = 23, n=4.68 일때 4m = -51.18
        // 1m = 23, n=4.68 일때 4.5m = -53.57
        // 1m = 23, n=4.68 일때 5m = -55.71
        if(n == 1) {
            //2m = -37.09
            if(valueTemp < 0 && valueTemp >= threshold2m && valueTemp2 < threshold2m) {
                return keyTemp;
            }
        }
        else if(n == 2) {
            //2.5m = -41.62
            if(valueTemp < 0 && valueTemp >= threshold2_5m && valueTemp2 < threshold2_5m) {
                return keyTemp;
            }
        }
        else if(n == 3) {
            //3m = -45.33
            if(valueTemp < 0 && valueTemp >= threshold3m && valueTemp2 < threshold3m) {
                return keyTemp;
            }
        }
        else if(n == 4) {
            //3.5m = -48.46
            if(valueTemp < 0 && valueTemp >= threshold3_5m && valueTemp2 < threshold3_5m) {
                return keyTemp;
            }
        }
        else if(n == 5) {
            //4m = -51.18
            if (valueTemp < 0 && valueTemp >= threshold4m && valueTemp2 < threshold4m) {
                return keyTemp;
            }
        }
        else if(n == 6) {
            //4.5m = -53.57
            if (valueTemp < 0 && valueTemp >= threshold4_5m && valueTemp2 < threshold4_5m) {
                return keyTemp;
            }
        }
        else if(n == 7) {
            //5m = -55.71
            if (valueTemp < 0 && valueTemp >= threshold5m && valueTemp2 < threshold5m) {
                return keyTemp;
            }
        }

        return 0;
    }

    private SelectedVO createSelectVO(String name, double rssi1, double rssi2, double rssi3) {
        return new SelectedVO(name,
                calcDistance(rssi1),
                rssi1,
                calcDistance(rssi2),
                rssi2,
                calcDistance(rssi3),
                rssi3
        );
    }


    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 4.68;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

    public int selectTriangle(VO originalVo) {
        int num;
        double valueTemp;
        int keyTemp;
        double errorValue = -999.9;

        VO vo = originalVo;

        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, vo.getRssi1());
        map.put(2, vo.getRssi2());
        map.put(3, vo.getRssi3());
        map.put(4, vo.getRssi4());
        map.put(5, vo.getRssi5());
        map.put(6, vo.getRssi6());
        map.put(7, vo.getRssi7());
        map.put(8, vo.getRssi8());


        for(int i=0; i<8; i++) {
            if( map.get(i+1) > 0 ) {
                map.put(i+1, errorValue);
            }
        }

        List<Integer> keyList = new ArrayList<Integer>();

        for(int j = 0; j<3; j++) {
            valueTemp = map.get(1);
            keyTemp=1;

            for(int i = 2; i<9; i++) {

                if(valueTemp < map.get(i) && 0>map.get(i)) {
                    keyTemp = i;
                    valueTemp = map.get(i);
                }
            }
            if(!map.get(keyTemp).equals(errorValue)) {
                keyList.add(keyTemp);
                map.put(keyTemp, errorValue);
            }
        }

        if(keyList.size() == 3) {
            Collections.sort(keyList);

            log.info("deviceName = {} key list = {}", vo.getDeviceName(), keyList.toString());


            int n1 = keyList.get(1) - keyList.get(0);
            int n2 = keyList.get(2) - keyList.get(1);

            if(n1 == 1 && n2 ==1) {
                return keyList.get(0);
            }
        }
        return 0;
    }

    //Threshold 실험 엑셀 파일 만들기
    public void createThresholdTestCsv(UserLocation originalUl,
                                       UserLocation updateLocFilteredUlfor2m,
                                       UserLocation updateLocFilteredUlfor2_5m,
                                       UserLocation updateLocFilteredUlfor3m,
                                       UserLocation updateLocFilteredUlfor3_5m,
                                       UserLocation updateLocFilteredUlfor4m,
                                       UserLocation updateLocFilteredUlfor4_5m,
                                       UserLocation updateLocFilteredUlfor5m) {
        try {
            poiHelper.writeExcelforLocationFilterTest(originalUl, updateLocFilteredUlfor2m, updateLocFilteredUlfor2_5m, updateLocFilteredUlfor3m, updateLocFilteredUlfor3_5m, updateLocFilteredUlfor4m, updateLocFilteredUlfor4_5m, updateLocFilteredUlfor5m);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VO createVO(String deviceName, double rssi1, double rssi2, double rssi3, double rssi4, double rssi5, double rssi6, double rssi7, double rssi8) {
        return new VO(deviceName,
                calcDistance(rssi1),
                rssi1,
                calcDistance(rssi2),
                rssi2,
                calcDistance(rssi3),
                rssi3,
                calcDistance(rssi4),
                rssi4,
                calcDistance(rssi5),
                rssi5,
                calcDistance(rssi6),
                rssi6,
                calcDistance(rssi7),
                rssi7,
                calcDistance(rssi8),
                rssi8
        );
    }

    public VO removeOutlier(VO vo) {

        double removedRssi1 = 1;
        double removedRssi2 = 1;
        double removedRssi3 = 1;
        double removedRssi4 = 1;
        double removedRssi5 = 1;
        double removedRssi6 = 1;
        double removedRssi7 = 1;
        double removedRssi8 = 1;


        if( rm.rssiOutlier(vo.getRssi1(), outlier15m) ) {
            removedRssi1 = vo.getRssi1();
        }
        if( rm.rssiOutlier(vo.getRssi2(), outlier15m) ) {
            removedRssi2 = vo.getRssi2();
        }
        if( rm.rssiOutlier(vo.getRssi3(), outlier15m) ) {
            removedRssi3 = vo.getRssi3();
        }
        if( rm.rssiOutlier(vo.getRssi4(), outlier15m) ) {
            removedRssi4 = vo.getRssi4();
        }
        if( rm.rssiOutlier(vo.getRssi5(), outlier15m) ) {
            removedRssi5 = vo.getRssi5();
        }
        if( rm.rssiOutlier(vo.getRssi6(), outlier15m) ) {
            removedRssi6 = vo.getRssi6();
        }
        if( rm.rssiOutlier(vo.getRssi7(), outlier15m) ) {
            removedRssi7 = vo.getRssi7();
        }
        if( rm.rssiOutlier(vo.getRssi8(), outlier15m) ) {
            removedRssi8 = vo.getRssi8();
        }

        return new VO(vo.getDeviceName(),
                calcDistance(removedRssi1),
                removedRssi1,
                calcDistance(removedRssi2),
                removedRssi2,
                calcDistance(removedRssi3),
                removedRssi3,
                calcDistance(removedRssi4),
                removedRssi4,
                calcDistance(removedRssi5),
                removedRssi5,
                calcDistance(removedRssi6),
                removedRssi6,
                calcDistance(removedRssi7),
                removedRssi7,
                calcDistance(removedRssi8),
                removedRssi8
        );
    }

    public VO rssiKalmanFilter(VO vo) {

        double filterdRssi1 = 1;
        double filterdRssi2 = 1;
        double filterdRssi3 = 1;
        double filterdRssi4 = 1;
        double filterdRssi5 = 1;
        double filterdRssi6 = 1;
        double filterdRssi7 = 1;
        double filterdRssi8 = 1;



        if(vo.getRssi1() != 1) {
            filterdRssi1 = kFilterForAp1.kalmanFiltering(vo.getRssi1());
        }
        if(vo.getRssi2() != 1) {
            filterdRssi2 = kFilterForAp2.kalmanFiltering(vo.getRssi2());
        }
        if(vo.getRssi3() != 1) {
            filterdRssi3 = kFilterForAp3.kalmanFiltering(vo.getRssi3());
        }
        if(vo.getRssi4() != 1) {
            filterdRssi4 = kFilterForAp4.kalmanFiltering(vo.getRssi4());
        }
        if(vo.getRssi5() != 1) {
            filterdRssi5 = kFilterForAp5.kalmanFiltering(vo.getRssi5());
        }
        if(vo.getRssi6() != 1) {
            filterdRssi6 = kFilterForAp6.kalmanFiltering(vo.getRssi6());
        }
        if(vo.getRssi7() != 1) {
            filterdRssi7 = kFilterForAp7.kalmanFiltering(vo.getRssi7());
        }
        if(vo.getRssi8() != 1) {
            filterdRssi8 = kFilterForAp8.kalmanFiltering(vo.getRssi8());
        }

        return new VO(vo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3,
                calcDistance(filterdRssi4),
                filterdRssi4,
                calcDistance(filterdRssi5),
                filterdRssi5,
                calcDistance(filterdRssi6),
                filterdRssi6,
                calcDistance(filterdRssi7),
                filterdRssi7,
                calcDistance(filterdRssi8),
                filterdRssi8
        );
    }


}

