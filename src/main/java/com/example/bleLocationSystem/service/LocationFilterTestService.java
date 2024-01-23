package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class LocationFilterTestService {

    ExelPOIHelper poiHelper;

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

    VO originalVo;

    VO removedVo;

    VO kalmanVo;

    SelectedVO originalSelectedVo;
    SelectedVO removedSelectedVo;
    SelectedVO kalmanSelectedVo;

    SelectedVO originalProximitySelectedVo;
    SelectedVO removedProximitySelectedVo;
    SelectedVO kalmanProximitySelectedVo;

    Ap ap1forOriginal;
    Ap ap2forOriginal;
    Ap ap3forOriginal;

    Ap ap1forRemoved;
    Ap ap2forRemoved;
    Ap ap3forRemoved;

    Ap ap1forKalman;
    Ap ap2forKalman;
    Ap ap3forKalman;

    Ap originalProximityAp;
    Ap removedProximityAp;
    Ap kalmanProximityAp;


    UserLocation originalUl;
    UserLocation removedUl;
    UserLocation kalmanUl;
    UserLocation locfilteredUl;

    UserLocation originalProximityUl;
    UserLocation removedProximityUl;
    UserLocation kalmanProximityUl;
    UserLocation locfilteredProximityUl;



    // 1m=-23, n=4.68 : 15m =  -78.0411
    double outlier15m = -78.0411;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99

    private double tempAlpha;
    private double lossNum;

    int checkProximityNum;

    int checkOriginalProximityNum;
    int checkRemovedProximityNum;
    int checkKalmanProximityNum;


    @Getter
    int originalTriangleNum;
    @Getter
    int removedTriangleNum;
    @Getter
    int kalmanTriangleNum;

    int i=0;
    int totalNum=0;

    ArrayList<UserLocation> ulList;

    public LocationFilterTestService() {
        poiHelper = new ExelPOIHelper();
        ulList = new ArrayList<UserLocation>();

        rm = new RemoveOutlier();

        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();
        kFilterForAp4 = new KalmanFilter();
        kFilterForAp5 = new KalmanFilter();
        kFilterForAp6 = new KalmanFilter();
        kFilterForAp7 = new KalmanFilter();
        kFilterForAp8 = new KalmanFilter();

        startFilter1 = new StartFilter();
        startFilter2 = new StartFilter();
        startFilter3 = new StartFilter();
        startFilter4 = new StartFilter();
        startFilter5 = new StartFilter();
        startFilter6 = new StartFilter();
        startFilter7 = new StartFilter();
        startFilter8 = new StartFilter();

        locMAFilter1 = new LocMAFilter(3);
        locMAFilter2 = new LocMAFilter(8);

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
                ap1forOriginal = new Ap((w / 2.0) * (originalTriangleNum - 1), h, originalSelectedVo.getDistance1());
                ap2forOriginal = new Ap((w / 2.0) * originalTriangleNum, 0, originalSelectedVo.getDistance2());
                ap3forOriginal = new Ap((w / 2.0) * (originalTriangleNum + 1), h, originalSelectedVo.getDistance3());
            } else {
                ap1forOriginal = new Ap((w / 2.0) * (originalTriangleNum - 1), 0, originalSelectedVo.getDistance1());
                ap2forOriginal = new Ap((w / 2.0) * originalTriangleNum, h, originalSelectedVo.getDistance2());
                ap3forOriginal = new Ap((w / 2.0) * (originalTriangleNum + 1), 0, originalSelectedVo.getDistance3());
            }
            Trilateration originalTr = new Trilateration(originalSelectedVo.getDeviceName(), ap1forOriginal, ap2forOriginal, ap3forOriginal);
            originalUl = originalTr.calcUserLocation();
        }
        else {
            originalUl = new UserLocation(999, 999, "ddd");
        }


        //============================================================ Removed ============================================================
        removedTriangleNum = selectTriangle(removedVo);
        switch (removedTriangleNum) {
            case 0:
                removedSelectedVo = null;
                break;
            case 1:
                removedSelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi1(), removedVo.getRssi2(), removedVo.getRssi3());
                break;
            case 2:
                removedSelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi2(), removedVo.getRssi3(), removedVo.getRssi4());
                break;
            case 3:
                removedSelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi3(), removedVo.getRssi4(), removedVo.getRssi5());
                break;
            case 4:
                removedSelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi4(), removedVo.getRssi5(), removedVo.getRssi6());
                break;
            case 5:
                removedSelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi5(), removedVo.getRssi6(), removedVo.getRssi7());
                break;
            case 6:
                removedSelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi6(), removedVo.getRssi7(), removedVo.getRssi8());
                break;
        }
        if(removedTriangleNum != 0) {
            if (removedTriangleNum % 2 == 0) {
                ap1forRemoved = new Ap((w / 2.0) * (removedTriangleNum - 1), h, removedSelectedVo.getDistance1());
                ap2forRemoved = new Ap((w / 2.0) * removedTriangleNum, 0, removedSelectedVo.getDistance2());
                ap3forRemoved = new Ap((w / 2.0) * (removedTriangleNum + 1), h, removedSelectedVo.getDistance3());
            } else {
                ap1forRemoved = new Ap((w / 2.0) * (removedTriangleNum - 1), 0, removedSelectedVo.getDistance1());
                ap2forRemoved = new Ap((w / 2.0) * removedTriangleNum, h, removedSelectedVo.getDistance2());
                ap3forRemoved = new Ap((w / 2.0) * (removedTriangleNum + 1), 0, removedSelectedVo.getDistance3());
            }
            Trilateration removedTr = new Trilateration(removedSelectedVo.getDeviceName(), ap1forRemoved, ap2forRemoved, ap3forRemoved);
            removedUl = removedTr.calcUserLocation();
        }
        else {
            removedUl = new UserLocation(999, 999, "ddd");
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
        //좌표 이상치 제거
        if (rm.rmXYOutlier(kalmanUl, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredUl = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredUl = locMAFilter1.push(kalmanUl);

            if(locfilteredUl == null) {
                locfilteredUl = new UserLocation(999, 999, "ddd");
            }
        }

//==============================================================================================================================================================================
//====================================================================== Original Proximity ======================================================================
//==============================================================================================================================================================================

        checkOriginalProximityNum = checkProximity(originalVo);

        if(checkOriginalProximityNum != 0) {
            switch (checkOriginalProximityNum) {
                case 0:
                    originalProximitySelectedVo = null;
                    break;
                case 1: case 2:
                    originalProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    originalProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    originalProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    originalProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    originalProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    originalProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if (checkOriginalProximityNum == 1) {
                originalProximityAp = new Ap(0, 0, originalProximitySelectedVo.getDistance1());
            } else if (checkOriginalProximityNum == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                originalProximityAp = new Ap((w / 2.0) * (checkOriginalProximityNum - 1), h, originalProximitySelectedVo.getDistance3());
            } else if (checkOriginalProximityNum % 2 == 0) {
                originalProximityAp = new Ap((w / 2.0) * (checkOriginalProximityNum - 1), h, originalProximitySelectedVo.getDistance2());
            } else {
                originalProximityAp = new Ap((w / 2.0) * (checkOriginalProximityNum - 1), 0, originalProximitySelectedVo.getDistance2());
            }

            Trilateration originalProximityTr = new Trilateration(originalProximitySelectedVo.getDeviceName(), originalProximityAp, checkOriginalProximityNum);
            originalProximityUl = originalProximityTr.calcProximityLocation();
        }
        else {
            originalProximityUl = new UserLocation(originalUl.getX(), originalUl.getY(), originalUl.getDeviceName());
        }

//==============================================================================================================================================================================
//====================================================================== Removed Proximity ======================================================================
//==============================================================================================================================================================================
        checkRemovedProximityNum = checkProximity(removedVo);

        if(checkRemovedProximityNum != 0) {
            switch (checkRemovedProximityNum) {
                case 0:
                    removedProximitySelectedVo = null;
                    break;
                case 1: case 2:
                    removedProximitySelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi1(), removedVo.getRssi2(), removedVo.getRssi3());
                    break;
                case 3:
                    removedProximitySelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi2(), removedVo.getRssi3(), removedVo.getRssi4());
                    break;
                case 4:
                    removedProximitySelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi3(), removedVo.getRssi4(), removedVo.getRssi5());
                    break;
                case 5:
                    removedProximitySelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi4(), removedVo.getRssi5(), removedVo.getRssi6());
                    break;
                case 6:
                    removedProximitySelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi5(), removedVo.getRssi6(), removedVo.getRssi7());
                    break;
                case 7: case 8:
                    removedProximitySelectedVo = createSelectVO(removedVo.getDeviceName(), removedVo.getRssi6(), removedVo.getRssi7(), removedVo.getRssi8());
                    break;
            }

            if (checkRemovedProximityNum == 1) {
                removedProximityAp = new Ap(0, 0, removedProximitySelectedVo.getDistance1());
            } else if (checkRemovedProximityNum == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                removedProximityAp = new Ap((w / 2.0) * (checkRemovedProximityNum - 1), h, removedProximitySelectedVo.getDistance3());
            } else if (checkRemovedProximityNum % 2 == 0) {
                removedProximityAp = new Ap((w / 2.0) * (checkRemovedProximityNum - 1), h, removedProximitySelectedVo.getDistance2());
            } else {
                removedProximityAp = new Ap((w / 2.0) * (checkRemovedProximityNum - 1), 0, removedProximitySelectedVo.getDistance2());
            }

            Trilateration removedProximityTr = new Trilateration(removedProximitySelectedVo.getDeviceName(), removedProximityAp, checkRemovedProximityNum);
            removedProximityUl = removedProximityTr.calcProximityLocation();
        }
        else {
            removedProximityUl = new UserLocation(removedUl.getX(), removedUl.getY(), removedUl.getDeviceName());
        }

//==============================================================================================================================================================================
//====================================================================== Kalman && Loc Filtered Proximity ======================================================================
//==============================================================================================================================================================================

//        checkKalmanProximityNum = checkProximity(kalmanVo);
        checkKalmanProximityNum = checkProximity(originalVo);
        if(checkKalmanProximityNum != 0) {
            switch (checkKalmanProximityNum) {
                case 0:
                    kalmanProximitySelectedVo = null;
                    break;
                case 1: case 2:
                    kalmanProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    kalmanProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    kalmanProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    kalmanProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    kalmanProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    kalmanProximitySelectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if (checkKalmanProximityNum == 1) {
                kalmanProximityAp = new Ap(0, 0, kalmanProximitySelectedVo.getDistance1());
            } else if (checkKalmanProximityNum == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                kalmanProximityAp = new Ap((w / 2.0) * (checkKalmanProximityNum - 1), h, kalmanProximitySelectedVo.getDistance3());
            } else if (checkKalmanProximityNum % 2 == 0) {
                kalmanProximityAp = new Ap((w / 2.0) * (checkKalmanProximityNum - 1), h, kalmanProximitySelectedVo.getDistance2());
            } else {
                kalmanProximityAp = new Ap((w / 2.0) * (checkKalmanProximityNum - 1), 0, kalmanProximitySelectedVo.getDistance2());
            }

            Trilateration kalmanProximityTr = new Trilateration(kalmanProximitySelectedVo.getDeviceName(), kalmanProximityAp, checkKalmanProximityNum);
            kalmanProximityUl = kalmanProximityTr.calcProximityLocation();
        }
        else {
            kalmanProximityUl = new UserLocation(kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDeviceName());
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(kalmanProximityUl, w, h)) {
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            locfilteredProximityUl = new UserLocation(999, 999, "ddd");
        }
        else {
            locfilteredProximityUl = locMAFilter2.push(kalmanProximityUl);

            if(locfilteredProximityUl == null) {
                locfilteredProximityUl = new UserLocation(999, 999, "ddd");
            }
        }

        // End
        //==============================================================================================================================================================================
        //==============================================================================================================================================================================
        //==============================================================================================================================================================================

        System.out.printf("Original Location : (%.2f, %.2f)  Dev : %.2fm\n", originalUl.getX(), originalUl.getY(), originalUl.getDistanceDev());
        System.out.printf("Removed Location : (%.2f, %.2f)  Dev : %.2fm\n", removedUl.getX(), removedUl.getY(), removedUl.getDistanceDev());
        System.out.printf("Kalman Location : (%.2f, %.2f)  Dev : %.2fm\n", kalmanUl.getX(), kalmanUl.getY(), kalmanUl.getDistanceDev());
        System.out.printf("Loc Filtered Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredUl.getX(), locfilteredUl.getY(), locfilteredUl.getDistanceDev());

        System.out.printf("Proximity Original Location : (%.2f, %.2f)  Dev : %.2fm\n", originalProximityUl.getX(), originalProximityUl.getY(), originalProximityUl.getDistanceDev());
        System.out.printf("Proximity Removed Location : (%.2f, %.2f)  Dev : %.2fm\n", removedProximityUl.getX(), removedProximityUl.getY(), removedProximityUl.getDistanceDev());
        System.out.printf("Proximity Kalman Location : (%.2f, %.2f)  Dev : %.2fm\n", kalmanProximityUl.getX(), kalmanProximityUl.getY(), kalmanProximityUl.getDistanceDev());
        System.out.printf("Proximity Loc Filtered Location : (%.2f, %.2f)  Dev : %.2fm\n", locfilteredProximityUl.getX(), locfilteredProximityUl.getY(), locfilteredProximityUl.getDistanceDev());


//        createLocationFilterTestCsv(originalUl, removedUl, kalmanUl, locfilteredUl, originalProximityUl, removedProximityUl, kalmanProximityUl, locfilteredProximityUl);

        totalNum++;

        if(totalNum==1){
            ulList.add(0, locfilteredUl);
            ulList.add(1, locfilteredProximityUl);
            ulList.add(2, locfilteredUl);
            ulList.add(3, locfilteredProximityUl);
        }
        else{
            ulList.set(0, locfilteredUl);
            ulList.set(1, locfilteredProximityUl);
            ulList.set(2, locfilteredUl);
            ulList.set(3, locfilteredProximityUl);
        }

        return ulList;
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

    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 4.68;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

    private int checkProximity(VO originalVo) {

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



        //if(valueTemp < 0 && valueTemp >= -49 && valueTemp2 < -49) {  //3m
        //if(valueTemp < 0 && valueTemp >= -42 && valueTemp2 < -42) {  //2m
//        if(valueTemp < 0 && valueTemp >= -37 && valueTemp2 < -37) {  //1.5m
//        if(valueTemp < 0 && valueTemp >= -30 && valueTemp2 < -30) {  //1m

        if(valueTemp < 0 && valueTemp >= -37.0882 && valueTemp2 < -37.0882) { // 1m = 23, n=4.68 일때 2m =-37.0882
//        if(valueTemp < 0 && valueTemp >= -45.3292 && valueTemp2 < -45.3292) { // 1m = 23, n=4.68 일때 3m =-45.3292
            return keyTemp;
        }

        return 0;
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

    public void createLocationFilterTestCsv(UserLocation originalUl, UserLocation removedUl, UserLocation kalmanUl, UserLocation locfilteredUl,
                                            UserLocation originalProximityUl, UserLocation removedProximityUl, UserLocation kalmanProximityUl, UserLocation locfilteredProximityUl) {
        try {
            poiHelper.writeExcelforLocationFilterTest(originalUl, removedUl, kalmanUl, locfilteredUl, originalProximityUl, removedProximityUl, kalmanProximityUl, locfilteredProximityUl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
