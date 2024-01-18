package com.example.bleLocationSystem.service;


import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ThresTestService {
    ExelPOIHelper poiHelper;

    private double tempAlpha;
    private double lossNum;

    int checkProximityNumfor1m;
    int checkProximityNumfor2m;
    int checkProximityNumfor3m;
    int checkProximityNumfor4m;

    VO originalVo;
    VO originalVoforTraiangle;
    SelectedVO selectedVo;

    SelectedVO selectedVofor1m;
    SelectedVO selectedVofor2m;
    SelectedVO selectedVofor3m;
    SelectedVO selectedVofor4m;

    SelectedVO filteredVofor1m;
    SelectedVO filteredVofor2m;
    SelectedVO filteredVofor3m;
    SelectedVO filteredVofor4m;


    UserLocation filteredUlfor1m;
    UserLocation filteredUlfor2m;
    UserLocation filteredUlfor3m;
    UserLocation filteredUlfor4m;

    UserLocation updateLocFilteredUlfor1m;
    UserLocation updateLocFilteredUlfor2m;
    UserLocation updateLocFilteredUlfor3m;
    UserLocation updateLocFilteredUlfor4m;

    UserLocation ul;

    @Getter
    int triangleNum;

    // 1m=-23, n=4.68 : 15m =  -78.0411
    double outlier15m = -78.0411;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0 * Math.sqrt(3) / 2;  //12.99

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap proximityApfor1m;
    Ap proximityApfor2m;
    Ap proximityApfor3m;
    Ap proximityApfor4m;


    Ap filteredAp1for1m;
    Ap filteredAp2for1m;
    Ap filteredAp3for1m;

    Ap filteredAp1for2m;
    Ap filteredAp2for2m;
    Ap filteredAp3for2m;

    Ap filteredAp1for3m;
    Ap filteredAp2for3m;
    Ap filteredAp3for3m;

    Ap filteredAp1for4m;
    Ap filteredAp2for4m;
    Ap filteredAp3for4m;

    RemoveOutlier rm;
    StartFilter startFilter0;
    StartFilter startFilter1;
    StartFilter startFilter2;
    StartFilter startFilter3;


    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;


    LocMAFilter locMAFilter0;
    LocMAFilter locMAFilter1;
    LocMAFilter locMAFilter2;
    LocMAFilter locMAFilter3;

    LocKalmanFilter locKalmanFilter0;
    LocKalmanFilter locKalmanFilter1;
    LocKalmanFilter locKalmanFilter2;
    LocKalmanFilter locKalmanFilter3;


    double[][] xfor1m;
    double[][] x2for1m;
    double[][] tempArrfor1m;

    double[][] xfor2m;
    double[][] x2for2m;
    double[][] tempArrfor2m;


    double[][] xfor3m;
    double[][] x2for3m;
    double[][] tempArrfor3m;


    double[][] xfor4m;
    double[][] x2for4m;
    double[][] tempArrfor4m;


    int ifor1m=0;
    int ifor2m=0;
    int ifor3m=0;
    int ifor4m=0;

    ArrayList<UserLocation> ulList;

    int totalNum;

    double threshold1m = -23;
    double threshold2m = -37.0882;
    double threshold3m = -45.3292;
    double threshold4m = -51.1764;

    public ThresTestService() {
        poiHelper = new ExelPOIHelper();

        //RSSI 보정 프로세스
        startFilter0 = new StartFilter();
        startFilter1 = new StartFilter();
        startFilter2 = new StartFilter();
        startFilter3 = new StartFilter();

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
        locMAFilter0 = new LocMAFilter();
        locMAFilter1 = new LocMAFilter();
        locMAFilter2 = new LocMAFilter();
        locMAFilter3 = new LocMAFilter();


        locKalmanFilter0 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        locKalmanFilter1 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        locKalmanFilter2 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        locKalmanFilter3 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);

        ulList = new ArrayList<UserLocation>();

        totalNum = 0;
    }

    public ArrayList<UserLocation> trilateration(VO vo) {
        originalVoforTraiangle = vo;
        originalVo = createKalmanVO(vo);

        totalNum++;

        triangleNum = selectTriangle(originalVoforTraiangle);

//        log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3());

        if (selectedVo != null) {

            //w = 5, h = 10
            if (triangleNum % 2 == 0) {
                ap1 = new Ap((w / 2.0) * (triangleNum - 1), h, selectedVo.getDistance1());
                ap2 = new Ap((w / 2.0) * triangleNum, 0, selectedVo.getDistance2());
                ap3 = new Ap((w / 2.0) * (triangleNum + 1), h, selectedVo.getDistance3());
            } else {
                ap1 = new Ap((w / 2.0) * (triangleNum - 1), 0, selectedVo.getDistance1());
                ap2 = new Ap((w / 2.0) * triangleNum, h, selectedVo.getDistance2());
                ap3 = new Ap((w / 2.0) * (triangleNum + 1), 0, selectedVo.getDistance3());
            }

            Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);
            ul = tr.calcUserLocation();
        }
        else {
            ul = new UserLocation(999, 999, "ddd");
        }
//--------------------------------------------------------------Proximity d = 1--------------------------------------------------------------
        checkProximityNumfor1m = checkProximity(originalVo, 1);

        if(checkProximityNumfor1m != 0) {
            switch (checkProximityNumfor1m) {
                case 0:
                    selectedVofor1m = null;
                    break;
                case 1: case 2:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }
            if (selectedVofor1m != null) {
                if (checkProximityNumfor1m == 1) {
                    proximityApfor1m = new Ap(0, 0, selectedVofor1m.getDistance1());
                } else if (checkProximityNumfor1m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                    proximityApfor1m = new Ap((w / 2.0) * (checkProximityNumfor1m - 1), h, selectedVofor1m.getDistance3());
                } else if (checkProximityNumfor1m % 2 == 0) {
                    proximityApfor1m = new Ap((w / 2.0) * (checkProximityNumfor1m - 1), h, selectedVofor1m.getDistance2());
                } else {
                    proximityApfor1m = new Ap((w / 2.0) * (checkProximityNumfor1m - 1), 0, selectedVofor1m.getDistance2());
                }
                Trilateration proximityTrfor1m = new Trilateration(originalVo.getDeviceName(), proximityApfor1m, checkProximityNumfor1m);
                filteredUlfor1m = proximityTrfor1m.calcProximityLocation();
            }
            else {
                filteredUlfor1m = null;
            }
        }
        else {
            //--------------------------------------------------------------Proposed RSSI Filter and Trilateration Method--------------------------------------------------------------
//            triangleNum = selectTriangle(originalVo);
            switch (triangleNum) {
                case 0:
                    selectedVofor1m = null;
                    break;
                case 1:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 2:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 3:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 5:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 6:
                    selectedVofor1m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if(selectedVofor1m != null) {
//                log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVofor2m.getRssi1(), selectedVofor2m.getRssi2(), selectedVofor2m.getRssi3());
                //RSSI 이상치 제거
                if (!rm.rmOutlier(selectedVofor1m.getRssi1(), selectedVofor1m.getRssi2(), selectedVofor1m.getRssi3(), outlier15m)) {
                    selectedVofor1m = null;
                }

                if(selectedVofor1m != null) {
                    if (ifor1m <= 10) {
//                        log.info("i = {}", ifor2m);
                        if (selectedVofor1m.getRssi1() < 0 && selectedVofor1m.getRssi2() < 0 && selectedVofor1m.getRssi3() < 0) {
                            selectedVofor1m = startFilter0.initFirstValue(selectedVofor1m, ifor1m);
                            ifor1m++;
                        } else {
                            selectedVofor1m = null;
                        }
                    }
                    if(selectedVofor1m != null) {
                        //KF
//                        filteredVofor1m = createFilteredVo0(selectedVofor1m);
                        filteredVofor1m = selectedVofor1m;
                        //AP 좌표 설정
                        if (triangleNum % 2 == 0) {
                            filteredAp1for1m = new Ap((w / 2.0) * (triangleNum - 1), h, filteredVofor1m.getDistance1());
                            filteredAp2for1m = new Ap((w / 2.0) * triangleNum, 0, filteredVofor1m.getDistance2());
                            filteredAp3for1m = new Ap((w / 2.0) * (triangleNum + 1), h, filteredVofor1m.getDistance3());
                        } else {
                            filteredAp1for1m = new Ap((w / 2.0) * (triangleNum - 1), 0, filteredVofor1m.getDistance1());
                            filteredAp2for1m = new Ap((w / 2.0) * triangleNum, h, filteredVofor1m.getDistance2());
                            filteredAp3for1m = new Ap((w / 2.0) * (triangleNum + 1), 0, filteredVofor1m.getDistance3());
                        }
                        Trilateration filteredTrfor1m = new Trilateration(filteredVofor1m.getDeviceName(), filteredAp1for1m, filteredAp2for1m, filteredAp3for1m);
                        filteredUlfor1m = filteredTrfor1m.calcUserLocation();
                    }
                    else {
                        filteredUlfor1m = new UserLocation(999, 999, "ddd");
                    }
                }
                else {
                    filteredUlfor1m = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUlfor1m = new UserLocation(999, 999, "ddd");
            }
        }

        //--------------------------------------------------------------Location Filter Method--------------------------------------------------------------
        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUlfor1m, w, h)) {
            filteredUlfor1m = null;
        }

        if(filteredUlfor1m != null) {
            //위치 MAF
            UserLocation mAFilteredUlfor1m = locMAFilter0.push(filteredUlfor1m);

            if(mAFilteredUlfor1m != null) {
                //2D 칼만 필터
                xfor1m = locKalmanFilter0.predict();
                tempArrfor1m = new double[][]{{mAFilteredUlfor1m.getX()}, {mAFilteredUlfor1m.getY()}};
                x2for1m = locKalmanFilter0.update(tempArrfor1m);
                updateLocFilteredUlfor1m = new UserLocation(x2for1m[0][0], x2for1m[1][0], mAFilteredUlfor1m.getDeviceName());

            }
            else
            {
                updateLocFilteredUlfor1m = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            updateLocFilteredUlfor1m = new UserLocation(999, 999, "ddd");
        }


//--------------------------------------------------------------Proximity d = 2--------------------------------------------------------------
        checkProximityNumfor2m = checkProximity(originalVo, 2);

        if(checkProximityNumfor2m != 0) {
            switch (checkProximityNumfor2m) {
                case 0:
                    selectedVofor2m = null;
                    break;
                case 1: case 2:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }
            if (selectedVofor2m != null) {
                if (checkProximityNumfor2m == 1) {
                    proximityApfor2m = new Ap(0, 0, selectedVofor2m.getDistance1());
                } else if (checkProximityNumfor2m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                    proximityApfor2m = new Ap((w / 2.0) * (checkProximityNumfor2m - 1), h, selectedVofor2m.getDistance3());
                } else if (checkProximityNumfor2m % 2 == 0) {
                    proximityApfor2m = new Ap((w / 2.0) * (checkProximityNumfor2m - 1), h, selectedVofor2m.getDistance2());
                } else {
                    proximityApfor2m = new Ap((w / 2.0) * (checkProximityNumfor2m - 1), 0, selectedVofor2m.getDistance2());
                }
                Trilateration proximityTrfor2m = new Trilateration(originalVo.getDeviceName(), proximityApfor2m, checkProximityNumfor2m);
                filteredUlfor2m = proximityTrfor2m.calcProximityLocation();
            }
            else {
                filteredUlfor2m = null;
            }
        }
        else {
        //--------------------------------------------------------------Proposed RSSI Filter and Trilateration Method--------------------------------------------------------------
//            triangleNum = selectTriangle(originalVo);
            switch (triangleNum) {
                case 0:
                    selectedVofor2m = null;
                    break;
                case 1:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 2:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 3:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 5:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 6:
                    selectedVofor2m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if(selectedVofor2m != null) {
//                log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVofor2m.getRssi1(), selectedVofor2m.getRssi2(), selectedVofor2m.getRssi3());
                //RSSI 이상치 제거
                if (!rm.rmOutlier(selectedVofor2m.getRssi1(), selectedVofor2m.getRssi2(), selectedVofor2m.getRssi3(), outlier15m)) {
                    selectedVofor2m = null;
                }

                if(selectedVofor2m != null) {
                    if (ifor2m <= 10) {
//                        log.info("i = {}", ifor2m);
                        if (selectedVofor2m.getRssi1() < 0 && selectedVofor2m.getRssi2() < 0 && selectedVofor2m.getRssi3() < 0) {
                            selectedVofor2m = startFilter1.initFirstValue(selectedVofor2m, ifor2m);
                            ifor2m++;
                        } else {
                            selectedVofor2m = null;
                        }
                    }
                    if(selectedVofor2m != null) {
                        //KF 삭제
//                        filteredVofor2m = createFilteredVo1(selectedVofor2m);
                        filteredVofor2m = selectedVofor2m;
                        //AP 좌표 설정
                        if (triangleNum % 2 == 0) {
                            filteredAp1for2m = new Ap((w / 2.0) * (triangleNum - 1), h, filteredVofor2m.getDistance1());
                            filteredAp2for2m = new Ap((w / 2.0) * triangleNum, 0, filteredVofor2m.getDistance2());
                            filteredAp3for2m = new Ap((w / 2.0) * (triangleNum + 1), h, filteredVofor2m.getDistance3());
                        } else {
                            filteredAp1for2m = new Ap((w / 2.0) * (triangleNum - 1), 0, filteredVofor2m.getDistance1());
                            filteredAp2for2m = new Ap((w / 2.0) * triangleNum, h, filteredVofor2m.getDistance2());
                            filteredAp3for2m = new Ap((w / 2.0) * (triangleNum + 1), 0, filteredVofor2m.getDistance3());
                        }
                        Trilateration filteredTrfor2m = new Trilateration(filteredVofor2m.getDeviceName(), filteredAp1for2m, filteredAp2for2m, filteredAp3for2m);
                        filteredUlfor2m = filteredTrfor2m.calcUserLocation();
                    }
                    else {
                        filteredUlfor2m = new UserLocation(999, 999, "ddd");
                    }
                }
                else {
                    filteredUlfor2m = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUlfor2m = new UserLocation(999, 999, "ddd");
            }
        }

        //--------------------------------------------------------------Location Filter Method--------------------------------------------------------------
        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUlfor2m, w, h)) {
            filteredUlfor2m = null;
        }

        if(filteredUlfor2m != null) {
            //위치 MAF
            UserLocation mAFilteredUlfor2m = locMAFilter1.push(filteredUlfor2m);

            if(mAFilteredUlfor2m != null) {
                //2D 칼만 필터
                xfor2m = locKalmanFilter1.predict();
                tempArrfor2m = new double[][]{{mAFilteredUlfor2m.getX()}, {mAFilteredUlfor2m.getY()}};
                x2for2m = locKalmanFilter1.update(tempArrfor2m);
                updateLocFilteredUlfor2m = new UserLocation(x2for2m[0][0], x2for2m[1][0], mAFilteredUlfor2m.getDeviceName());

            }
            else
            {
                updateLocFilteredUlfor2m = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            updateLocFilteredUlfor2m = new UserLocation(999, 999, "ddd");
        }


//--------------------------------------------------------------Proximity d = 3--------------------------------------------------------------
        checkProximityNumfor3m = checkProximity(originalVo, 3);

        if(checkProximityNumfor3m != 0) {
            switch (checkProximityNumfor3m) {
                case 0:
                    selectedVofor3m = null;
                    break;
                case 1: case 2:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }
            if (selectedVofor3m != null) {
                if (checkProximityNumfor3m == 1) {
                    proximityApfor3m = new Ap(0, 0, selectedVofor3m.getDistance1());
                } else if (checkProximityNumfor3m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                    proximityApfor3m = new Ap((w / 2.0) * (checkProximityNumfor3m - 1), h, selectedVofor3m.getDistance3());
                } else if (checkProximityNumfor3m % 2 == 0) {
                    proximityApfor3m = new Ap((w / 2.0) * (checkProximityNumfor3m - 1), h, selectedVofor3m.getDistance2());
                } else {
                    proximityApfor3m = new Ap((w / 2.0) * (checkProximityNumfor3m - 1), 0, selectedVofor3m.getDistance2());
                }
                Trilateration proximityTrfor3m = new Trilateration(originalVo.getDeviceName(), proximityApfor3m, checkProximityNumfor3m);
                filteredUlfor3m = proximityTrfor3m.calcProximityLocation();
            }
            else {
                filteredUlfor3m = null;
            }
        }
        else {
            //--------------------------------------------------------------Proposed RSSI Filter and Trilateration Method--------------------------------------------------------------
//            triangleNum = selectTriangle(originalVo);
            switch (triangleNum) {
                case 0:
                    selectedVofor3m = null;
                    break;
                case 1:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 2:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 3:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 5:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 6:
                    selectedVofor3m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if(selectedVofor3m != null) {
//                log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVofor2m.getRssi1(), selectedVofor2m.getRssi2(), selectedVofor2m.getRssi3());
                //RSSI 이상치 제거
                if (!rm.rmOutlier(selectedVofor3m.getRssi1(), selectedVofor3m.getRssi2(), selectedVofor3m.getRssi3(), outlier15m)) {
                    selectedVofor3m = null;
                }

                if(selectedVofor3m != null) {
                    if (ifor3m <= 10) {
//                        log.info("i = {}", ifor2m);
                        if (selectedVofor3m.getRssi1() < 0 && selectedVofor3m.getRssi2() < 0 && selectedVofor3m.getRssi3() < 0) {
                            selectedVofor3m = startFilter2.initFirstValue(selectedVofor3m, ifor3m);
                            ifor3m++;
                        } else {
                            selectedVofor3m = null;
                        }
                    }
                    if(selectedVofor3m != null) {
                        //KF
//                        filteredVofor3m = createFilteredVo2(selectedVofor3m);
                        filteredVofor3m = selectedVofor3m;
                        //AP 좌표 설정
                        if (triangleNum % 2 == 0) {
                            filteredAp1for3m = new Ap((w / 2.0) * (triangleNum - 1), h, filteredVofor3m.getDistance1());
                            filteredAp2for3m = new Ap((w / 2.0) * triangleNum, 0, filteredVofor3m.getDistance2());
                            filteredAp3for3m = new Ap((w / 2.0) * (triangleNum + 1), h, filteredVofor3m.getDistance3());
                        } else {
                            filteredAp1for3m = new Ap((w / 2.0) * (triangleNum - 1), 0, filteredVofor3m.getDistance1());
                            filteredAp2for3m = new Ap((w / 2.0) * triangleNum, h, filteredVofor3m.getDistance2());
                            filteredAp3for3m = new Ap((w / 2.0) * (triangleNum + 1), 0, filteredVofor3m.getDistance3());
                        }
                        Trilateration filteredTrfor3m = new Trilateration(filteredVofor3m.getDeviceName(), filteredAp1for3m, filteredAp2for3m, filteredAp3for3m);
                        filteredUlfor3m = filteredTrfor3m.calcUserLocation();
                    }
                    else {
                        filteredUlfor3m = new UserLocation(999, 999, "ddd");
                    }
                }
                else {
                    filteredUlfor3m = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUlfor3m = new UserLocation(999, 999, "ddd");
            }
        }

        //--------------------------------------------------------------Location Filter Method--------------------------------------------------------------
        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUlfor3m, w, h)) {
            filteredUlfor3m = null;
        }

        if(filteredUlfor3m != null) {
            //위치 MAF
            UserLocation mAFilteredUlfor3m = locMAFilter2.push(filteredUlfor3m);

            if(mAFilteredUlfor3m != null) {
                //2D 칼만 필터
                xfor3m = locKalmanFilter2.predict();
                tempArrfor3m = new double[][]{{mAFilteredUlfor3m.getX()}, {mAFilteredUlfor3m.getY()}};
                x2for3m = locKalmanFilter2.update(tempArrfor3m);
                updateLocFilteredUlfor3m = new UserLocation(x2for3m[0][0], x2for3m[1][0], mAFilteredUlfor3m.getDeviceName());

            }
            else
            {
                updateLocFilteredUlfor3m = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            updateLocFilteredUlfor3m = new UserLocation(999, 999, "ddd");
        }

//--------------------------------------------------------------Proximity d = 4--------------------------------------------------------------
        checkProximityNumfor4m = checkProximity(originalVo, 4);

        if(checkProximityNumfor4m != 0) {
            switch (checkProximityNumfor4m) {
                case 0:
                    selectedVofor4m = null;
                    break;
                case 1: case 2:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }
            if (selectedVofor4m != null) {
                if (checkProximityNumfor4m == 1) {
                    proximityApfor4m = new Ap(0, 0, selectedVofor4m.getDistance1());
                } else if (checkProximityNumfor4m == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                    proximityApfor4m = new Ap((w / 2.0) * (checkProximityNumfor4m - 1), h, selectedVofor4m.getDistance3());
                } else if (checkProximityNumfor4m % 2 == 0) {
                    proximityApfor4m = new Ap((w / 2.0) * (checkProximityNumfor4m - 1), h, selectedVofor4m.getDistance2());
                } else {
                    proximityApfor4m = new Ap((w / 2.0) * (checkProximityNumfor4m - 1), 0, selectedVofor4m.getDistance2());
                }
                Trilateration proximityTrfor4m = new Trilateration(originalVo.getDeviceName(), proximityApfor4m, checkProximityNumfor4m);
                filteredUlfor4m = proximityTrfor4m.calcProximityLocation();
            }
            else {
                filteredUlfor4m = null;
            }
        }
        else {
            //--------------------------------------------------------------Proposed RSSI Filter and Trilateration Method--------------------------------------------------------------
//            triangleNum = selectTriangle(originalVo);
            switch (triangleNum) {
                case 0:
                    selectedVofor4m = null;
                    break;
                case 1:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 2:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 3:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 5:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 6:
                    selectedVofor4m = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if(selectedVofor4m != null) {
//                log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVofor2m.getRssi1(), selectedVofor2m.getRssi2(), selectedVofor2m.getRssi3());
                //RSSI 이상치 제거
                if (!rm.rmOutlier(selectedVofor4m.getRssi1(), selectedVofor4m.getRssi2(), selectedVofor4m.getRssi3(), outlier15m)) {
                    selectedVofor4m = null;
                }

                if(selectedVofor4m != null) {
                    if (ifor4m <= 10) {
//                        log.info("i = {}", ifor2m);
                        if (selectedVofor4m.getRssi1() < 0 && selectedVofor4m.getRssi2() < 0 && selectedVofor4m.getRssi3() < 0) {
                            selectedVofor4m = startFilter3.initFirstValue(selectedVofor4m, ifor4m);
                            ifor4m++;
                        } else {
                            selectedVofor4m = null;
                        }
                    }
                    if(selectedVofor4m != null) {
                        //KF
//                        filteredVofor4m = createFilteredVo3(selectedVofor4m);
                        filteredVofor4m = selectedVofor4m;
                        //AP 좌표 설정
                        if (triangleNum % 2 == 0) {
                            filteredAp1for4m = new Ap((w / 2.0) * (triangleNum - 1), h, filteredVofor4m.getDistance1());
                            filteredAp2for4m = new Ap((w / 2.0) * triangleNum, 0, filteredVofor4m.getDistance2());
                            filteredAp3for4m = new Ap((w / 2.0) * (triangleNum + 1), h, filteredVofor4m.getDistance3());
                        } else {
                            filteredAp1for4m = new Ap((w / 2.0) * (triangleNum - 1), 0, filteredVofor4m.getDistance1());
                            filteredAp2for4m = new Ap((w / 2.0) * triangleNum, h, filteredVofor4m.getDistance2());
                            filteredAp3for4m = new Ap((w / 2.0) * (triangleNum + 1), 0, filteredVofor4m.getDistance3());
                        }
                        Trilateration filteredTrfor4m = new Trilateration(filteredVofor4m.getDeviceName(), filteredAp1for4m, filteredAp2for4m, filteredAp3for4m);
                        filteredUlfor4m = filteredTrfor4m.calcUserLocation();
                    }
                    else {
                        filteredUlfor4m = new UserLocation(999, 999, "ddd");
                    }
                }
                else {
                    filteredUlfor4m = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUlfor4m = new UserLocation(999, 999, "ddd");
            }
        }

        //--------------------------------------------------------------Location Filter Method--------------------------------------------------------------
        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUlfor4m, w, h)) {
            filteredUlfor4m = null;
        }

        if(filteredUlfor4m != null) {
            //위치 MAF
            UserLocation mAFilteredUlfor4m = locMAFilter3.push(filteredUlfor4m);

            if(mAFilteredUlfor4m != null) {
                //2D 칼만 필터
                xfor4m = locKalmanFilter3.predict();
                tempArrfor4m = new double[][]{{mAFilteredUlfor4m.getX()}, {mAFilteredUlfor4m.getY()}};
                x2for4m = locKalmanFilter3.update(tempArrfor4m);
                updateLocFilteredUlfor4m = new UserLocation(x2for4m[0][0], x2for4m[1][0], mAFilteredUlfor4m.getDeviceName());

            }
            else
            {
                updateLocFilteredUlfor4m = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            updateLocFilteredUlfor4m = new UserLocation(999, 999, "ddd");
        }

        createCsvEx2(ul, updateLocFilteredUlfor1m, updateLocFilteredUlfor2m, updateLocFilteredUlfor3m, updateLocFilteredUlfor4m);

        if(totalNum==1){
            ulList.add(0, updateLocFilteredUlfor1m);
            ulList.add(1, updateLocFilteredUlfor2m);
            ulList.add(2, updateLocFilteredUlfor3m);
            ulList.add(3, updateLocFilteredUlfor4m);
        }
        else{
            ulList.set(0, updateLocFilteredUlfor1m);
            ulList.set(1, updateLocFilteredUlfor2m);
            ulList.set(2, updateLocFilteredUlfor3m);
            ulList.set(3, updateLocFilteredUlfor4m);
        }

        log.info("total Num = {}", totalNum);

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

        if(n == 1) {
            if(valueTemp < 0 && valueTemp >= -23 && valueTemp2 < -23) { // 1m = 23, n=4.68 일때 1m = -23
                return keyTemp;
            }
        }
        else if(n == 2) {
            if(valueTemp < 0 && valueTemp >= -37.0882 && valueTemp2 < -37.0882) { // 1m = 23, n=4.68 일때 2m = -37.0882
                return keyTemp;
            }
        }
        else if(n == 3) {
                if(valueTemp < 0 && valueTemp >= -45.3292 && valueTemp2 < -45.3292) { // 1m = 23, n=4.68 일때 3m = -45.3292
                    return keyTemp;
                }
        }
        else if(n == 4) {
                if (valueTemp < 0 && valueTemp >= -51.1764 && valueTemp2 < -51.1764) { // 1m = 23, n=4.68 일때 3m = -45.3292
                    return keyTemp;
                }
        }

//        if(valueTemp < 0 && valueTemp >= -23.0 && valueTemp2 < -23.0) { // 1m = 23, n=4.68 일때 1m = -23
//        if(valueTemp < 0 && valueTemp >= -37.0882 && valueTemp2 < -37.0882) { // 1m = 23, n=4.68 일때 2m = -37.0882
//        if(valueTemp < 0 && valueTemp >= -45.3292 && valueTemp2 < -45.3292) { // 1m = 23, n=4.68 일때 3m = -45.3292
//        if(valueTemp < 0 && valueTemp >= -51.1764 && valueTemp2 < -51.1764) { // 1m = 23, n=4.68 일때 4m = -51.1764

        return 0;
    }

    private VO createKalmanVO(VO vo) {

        double filterdRssi1 = 1;
        double filterdRssi2 = 1;
        double filterdRssi3 = 1;
        double filterdRssi4 = 1;
        double filterdRssi5 = 1;
        double filterdRssi6 = 1;
        double filterdRssi7 = 1;
        double filterdRssi8 = 1;



        if(vo.getRssi1() == 1) {
            filterdRssi1 = kFilterForAp1.kalmanFiltering(vo.getRssi1());
        }
        if(vo.getRssi2() == 1) {
            filterdRssi2 = kFilterForAp2.kalmanFiltering(vo.getRssi2());
        }
        if(vo.getRssi3() == 1) {
            filterdRssi3 = kFilterForAp3.kalmanFiltering(vo.getRssi3());
        }
        if(vo.getRssi4() == 1) {
            filterdRssi4 = kFilterForAp4.kalmanFiltering(vo.getRssi4());
        }
        if(vo.getRssi5() == 1) {
            filterdRssi5 = kFilterForAp5.kalmanFiltering(vo.getRssi5());
        }
        if(vo.getRssi6() == 1) {
            filterdRssi6 = kFilterForAp6.kalmanFiltering(vo.getRssi6());
        }
        if(vo.getRssi7() == 1) {
            filterdRssi7 = kFilterForAp7.kalmanFiltering(vo.getRssi7());
        }
        if(vo.getRssi8() == 1) {
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

    //칼만 필터 VO 생성 함수
//    public SelectedVO createFilteredVo0(SelectedVO originalVo) {
//
//        double filterdRssi1 = kFilterForAp10.kalmanFiltering(originalVo.getRssi1());
//        double filterdRssi2 = kFilterForAp11.kalmanFiltering(originalVo.getRssi2());
//        double filterdRssi3 = kFilterForAp12.kalmanFiltering(originalVo.getRssi3());
//
//        return new SelectedVO(originalVo.getDeviceName(),
//                calcDistance(filterdRssi1),
//                filterdRssi1,
//                calcDistance(filterdRssi2),
//                filterdRssi2,
//                calcDistance(filterdRssi3),
//                filterdRssi3
//        );
//    }
//
//    public SelectedVO createFilteredVo1(SelectedVO originalVo) {
//
//        double filterdRssi1 = kFilterForAp1.kalmanFiltering(originalVo.getRssi1());
//        double filterdRssi2 = kFilterForAp2.kalmanFiltering(originalVo.getRssi2());
//        double filterdRssi3 = kFilterForAp3.kalmanFiltering(originalVo.getRssi3());
//
//        return new SelectedVO(originalVo.getDeviceName(),
//                calcDistance(filterdRssi1),
//                filterdRssi1,
//                calcDistance(filterdRssi2),
//                filterdRssi2,
//                calcDistance(filterdRssi3),
//                filterdRssi3
//        );
//    }
//
//    public SelectedVO createFilteredVo2(SelectedVO originalVo) {
//
//        double filterdRssi1 = kFilterForAp4.kalmanFiltering(originalVo.getRssi1());
//        double filterdRssi2 = kFilterForAp5.kalmanFiltering(originalVo.getRssi2());
//        double filterdRssi3 = kFilterForAp6.kalmanFiltering(originalVo.getRssi3());
//
//        return new SelectedVO(originalVo.getDeviceName(),
//                calcDistance(filterdRssi1),
//                filterdRssi1,
//                calcDistance(filterdRssi2),
//                filterdRssi2,
//                calcDistance(filterdRssi3),
//                filterdRssi3
//        );
//    }
//
//    public SelectedVO createFilteredVo3(SelectedVO originalVo) {
//
//        double filterdRssi1 = kFilterForAp7.kalmanFiltering(originalVo.getRssi1());
//        double filterdRssi2 = kFilterForAp8.kalmanFiltering(originalVo.getRssi2());
//        double filterdRssi3 = kFilterForAp9.kalmanFiltering(originalVo.getRssi3());
//
//        return new SelectedVO(originalVo.getDeviceName(),
//                calcDistance(filterdRssi1),
//                filterdRssi1,
//                calcDistance(filterdRssi2),
//                filterdRssi2,
//                calcDistance(filterdRssi3),
//                filterdRssi3
//        );
//    }

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
    public void createCsvEx2(UserLocation originalUl, UserLocation updateLocFilteredUlfor1m, UserLocation updateLocFilteredUlfor2m, UserLocation updateLocFilteredUlfor3m, UserLocation updateLocFilteredUlfor4m) {
        try {
            poiHelper.writeExcelforLocFilter(originalUl, updateLocFilteredUlfor1m, updateLocFilteredUlfor2m, updateLocFilteredUlfor3m, updateLocFilteredUlfor4m);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
