package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class LocFiterTestService {

    ExelPOIHelper poiHelper;

    @Getter
    int triangleNum;
    @Getter
    int triangleNumforKalman;
    @Getter
    int triangleNumforNotProximity;

    int checkProximityNum;

    private double tempAlpha;
    private double lossNum;


    VO originalVo;
    VO realOriginalVo;
    VO originalVoforNotProximity;
    VO realOriginalVoforNotProximity;



    SelectedVO originalSelectVo;
    SelectedVO selectedVoforKalman;
    SelectedVO kalmanVo;

    SelectedVO selectedVo;
    SelectedVO filteredVo;

    SelectedVO selectedVoforOriginal;
    SelectedVO selectedVoforNotProximity;
    SelectedVO filteredVoforNotProximity;

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap ap1forOriginal;
    Ap ap2forOriginal;
    Ap ap3forOriginal;

    Ap kalmanAp1;
    Ap kalmanAp2;
    Ap kalmanAp3;

    Ap proximityAp;

    Ap filteredAp1;
    Ap filteredAp2;
    Ap filteredAp3;

    Ap ap1forNotProximity;
    Ap ap2forNotProximity;
    Ap ap3forNotProximity;

    Ap ap1forNoKalman;
    Ap ap2forNoKalman;
    Ap ap3forNoKalman;


    StartFilter startFilter;
    StartFilter startFilter2;
    RemoveOutlier rm;

    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;

    MAFilter mafFilter4;
    MAFilter mafFilter5;
    MAFilter mafFilter6;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;
    KalmanFilter kFilterForAp9;

    LocMAFilter locMAFilter;
    LocMAFilter locMAFilter2;
    LocMAFilter locMAFilter3;

    LocKalmanFilter locKalmanFilter;
    LocKalmanFilter locKalmanFilter2;

    double[][] x;

    double[][] x2;

    double[][] tempArr;

    double[][] tempArrforNotProximity;

    double[][] xforNotProximity;

    double[][] x2forNotProximity;



    UserLocation originalUl;
    UserLocation rmKalmanUl;

    UserLocation locMAFUl;
    UserLocation mAFilteredUlforNotProximity;
    UserLocation filteredUlforNotProximity;
//    UserLocation filteredUlforNoKalman;
    UserLocation updateLocFilteredUlforNotProximity;

    UserLocation filteredUl;
    UserLocation updateLocFilteredUl;

    ArrayList<UserLocation> ulList;

    // 1m=-30, n=4 : 15m = -77
    // 1m=-23, n=3.81 : 15m =  -67.8091
//    double outlier15m = -67.8091;
//    double outlier20m = -72.5692;

    // 1m=-23, n=4.68 : 15m =  -78.0411
    double outlier15m = -78.0411;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99


    int kalmanFinishNum;
    int proposedFinishNum;
//    @Getter
    int proposedwithoutProximityFinishNum;
    int totalNum;
    int i=0;
    int j=0;


    public LocFiterTestService() {
        poiHelper = new ExelPOIHelper();

        //RSSI 보정 프로세스
        startFilter = new StartFilter();
        startFilter2 = new StartFilter();

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
        locMAFilter = new LocMAFilter();
        locMAFilter2 = new LocMAFilter();
        locMAFilter3 = new LocMAFilter();

        locKalmanFilter = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        locKalmanFilter2 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);

        ulList = new ArrayList<UserLocation>();

        kalmanFinishNum = 0;
        proposedFinishNum = 0;
        proposedwithoutProximityFinishNum = 0;

        totalNum = 0;
    }

    public ArrayList<UserLocation> trilateration(VO vo) {
        realOriginalVo = vo;
        realOriginalVoforNotProximity = vo;

//        originalVo = createKalmanVO(vo);
//        originalVoforNotProximity = createKalmanVO(vo);



        totalNum++;

//--------------------------------------------------------------Proposed Method--------------------------------------------------------------

        //--------------------------------------------------------------Proposed Proximity Method--------------------------------------------------------------
        checkProximityNum = checkProximity(realOriginalVo);

        if(checkProximityNum != 0) {
            switch (checkProximityNum) {
                case 0:
                    selectedVo = null;
                    break;
                case 1: case 2:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 3:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 4:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 5:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 6:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 7: case 8:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }
            if (selectedVo != null) {
                if (checkProximityNum == 1) {
                    proximityAp = new Ap(0, 0, selectedVo.getDistance1());
                } else if (checkProximityNum == 8) {      //beacon의 갯수 -> 가장 마지막 번호
                    proximityAp = new Ap((w / 2.0) * (checkProximityNum - 1), h, selectedVo.getDistance3());
                } else if (checkProximityNum % 2 == 0) {
                    proximityAp = new Ap((w / 2.0) * (checkProximityNum - 1), h, selectedVo.getDistance2());
                } else {
                    proximityAp = new Ap((w / 2.0) * (checkProximityNum - 1), 0, selectedVo.getDistance2());
                }
                Trilateration proximityTr = new Trilateration(originalVo.getDeviceName(), proximityAp, checkProximityNum);
                filteredUl = proximityTr.calcProximityLocation();
            }
            else {
                filteredUl = null;
            }
        }
        else {
            //--------------------------------------------------------------Proposed RSSI Filter and Trilateration Method--------------------------------------------------------------
            triangleNum = selectTriangle(realOriginalVo);
            switch (triangleNum) {
                case 0:
                    selectedVo = null;
                    break;
                case 1:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                    break;
                case 2:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                    break;
                case 3:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi5());
                    break;
                case 4:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                    break;
                case 5:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                    break;
                case 6:
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                    break;
            }

            if(selectedVo != null) {
                log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3());
                //RSSI 이상치 제거
                if (!rm.rmOutlier(selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3(), outlier15m)) {
                    selectedVo = null;
                }
//        if(selectedVo.getDeviceName().equals("HJ")) {
                if(selectedVo != null) {
                    if (i <= 10) {
                        log.info("i = {}", i);
                        if (selectedVo.getRssi1() < 0 && selectedVo.getRssi2() < 0 && selectedVo.getRssi3() < 0) {
                            selectedVo = startFilter.initFirstValue(selectedVo, i);
                            i++;
                        } else {
                            selectedVo = null;
                        }
                    }
                    if(selectedVo != null) {
                        //KF
//                        filteredVo = createFilteredVo(selectedVo);
                        filteredVo = selectedVo;
                        //AP 좌표 설정
                        if (triangleNum % 2 == 0) {
                            filteredAp1 = new Ap((w / 2.0) * (triangleNum - 1), h, filteredVo.getDistance1());
                            filteredAp2 = new Ap((w / 2.0) * triangleNum, 0, filteredVo.getDistance2());
                            filteredAp3 = new Ap((w / 2.0) * (triangleNum + 1), h, filteredVo.getDistance3());
                        } else {
                            filteredAp1 = new Ap((w / 2.0) * (triangleNum - 1), 0, filteredVo.getDistance1());
                            filteredAp2 = new Ap((w / 2.0) * triangleNum, h, filteredVo.getDistance2());
                            filteredAp3 = new Ap((w / 2.0) * (triangleNum + 1), 0, filteredVo.getDistance3());
                        }
//                        Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);
                        Trilateration filteredTr = new Trilateration(filteredVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);
//                        UserLocation ul = tr.calcUserLocation();
                        filteredUl = filteredTr.calcUserLocation();
                    }
                    else {
                        filteredUl = new UserLocation(999, 999, "ddd");
                    }
                }
                else {
                    filteredUl = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUl = new UserLocation(999, 999, "ddd");
            }
        }

        //--------------------------------------------------------------Location Filter Method--------------------------------------------------------------
        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUl, w, h)) {
            filteredUl = null;
        }

        if(filteredUl != null) {
            //위치 MAF
            UserLocation mAFilteredUl = locMAFilter.push(filteredUl);

            if(mAFilteredUl != null) {
                //2D 칼만 필터
                x = locKalmanFilter.predict();
                tempArr = new double[][]{{mAFilteredUl.getX()}, {mAFilteredUl.getY()}};
                x2 = locKalmanFilter.update(tempArr);
                updateLocFilteredUl = new UserLocation(x2[0][0], x2[1][0], mAFilteredUl.getDeviceName());

                //제안 방법 성공 횟수
                proposedFinishNum++;
                System.out.printf("Proposed Finish Num : %d\n", proposedFinishNum);
            }
            else
            {
                updateLocFilteredUl = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            updateLocFilteredUl = new UserLocation(999, 999, "ddd");
        }
//--------------------------------------------------------------END--------------------------------------------------------------

//--------------------------------------------------------------Proposed Method without Proximity--------------------------------------------------------------

        triangleNumforNotProximity = selectTriangle(realOriginalVoforNotProximity);

        switch (triangleNumforNotProximity) {
            case 0:
                selectedVoforOriginal = null;
                break;
            case 1:
                selectedVoforOriginal = createSelectVO(realOriginalVoforNotProximity.getDeviceName(), realOriginalVoforNotProximity.getRssi1(), realOriginalVoforNotProximity.getRssi2(), realOriginalVoforNotProximity.getRssi3());
                break;
            case 2:
                selectedVoforOriginal = createSelectVO(realOriginalVoforNotProximity.getDeviceName(), realOriginalVoforNotProximity.getRssi2(), realOriginalVoforNotProximity.getRssi3(), realOriginalVoforNotProximity.getRssi4());
                break;
            case 3:
                selectedVoforOriginal = createSelectVO(realOriginalVoforNotProximity.getDeviceName(), realOriginalVoforNotProximity.getRssi3(), realOriginalVoforNotProximity.getRssi4(), realOriginalVoforNotProximity.getRssi5());
                break;
            case 4:
                selectedVoforOriginal = createSelectVO(realOriginalVoforNotProximity.getDeviceName(), realOriginalVoforNotProximity.getRssi4(), realOriginalVoforNotProximity.getRssi5(), realOriginalVoforNotProximity.getRssi6());
                break;
            case 5:
                selectedVoforOriginal = createSelectVO(realOriginalVoforNotProximity.getDeviceName(), realOriginalVoforNotProximity.getRssi5(), realOriginalVoforNotProximity.getRssi6(), realOriginalVoforNotProximity.getRssi7());
                break;
            case 6:
                selectedVoforOriginal = createSelectVO(realOriginalVoforNotProximity.getDeviceName(), realOriginalVoforNotProximity.getRssi6(), realOriginalVoforNotProximity.getRssi7(), realOriginalVoforNotProximity.getRssi8());
                break;
        }

        if(selectedVoforOriginal != null) {

            //Original
            //++++
            //----------------------------------------------------------------------------------
            originalSelectVo = createSelectVO(selectedVoforOriginal.getDeviceName(), selectedVoforOriginal.getRssi1(), selectedVoforOriginal.getRssi2(), selectedVoforOriginal.getRssi3());

            if (triangleNumforNotProximity % 2 == 0) {
                ap1forOriginal = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), h, originalSelectVo.getDistance1());
                ap2forOriginal = new Ap((w / 2.0) * triangleNumforNotProximity, 0, originalSelectVo.getDistance2());
                ap3forOriginal = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), h, originalSelectVo.getDistance3());
            } else {
                ap1forOriginal = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), 0, originalSelectVo.getDistance1());
                ap2forOriginal = new Ap((w / 2.0) * triangleNumforNotProximity, h, originalSelectVo.getDistance2());
                ap3forOriginal = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), 0, originalSelectVo.getDistance3());
            }

            Trilateration originalTr = new Trilateration(originalSelectVo.getDeviceName(), ap1forOriginal, ap2forOriginal, ap3forOriginal);
            originalUl = originalTr.calcUserLocation();

            //----------------------------------------------------------------------------------

            switch (triangleNumforNotProximity) {
                case 0:
                    selectedVoforNotProximity = null;
                    break;
                case 1:
                    selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi1(), originalVoforNotProximity.getRssi2(), originalVoforNotProximity.getRssi3());
                    break;
                case 2:
                    selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi2(), originalVoforNotProximity.getRssi3(), originalVoforNotProximity.getRssi4());
                    break;
                case 3:
                    selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi3(), originalVoforNotProximity.getRssi4(), originalVoforNotProximity.getRssi5());
                    break;
                case 4:
                    selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi4(), originalVoforNotProximity.getRssi5(), originalVoforNotProximity.getRssi6());
                    break;
                case 5:
                    selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi5(), originalVoforNotProximity.getRssi6(), originalVoforNotProximity.getRssi7());
                    break;
                case 6:
                    selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi6(), originalVoforNotProximity.getRssi7(), originalVoforNotProximity.getRssi8());
                    break;
            }



            log.info("Original Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", originalSelectVo.getRssi1(), originalSelectVo.getRssi2(), originalSelectVo.getRssi3());
//            log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVoforNotProximity.getRssi1(), selectedVoforNotProximity.getRssi2(), selectedVoforNotProximity.getRssi3());
            //RSSI 이상치 제거
            if (!rm.rmOutlier(selectedVoforNotProximity.getRssi1(), selectedVoforNotProximity.getRssi2(), selectedVoforNotProximity.getRssi3(), outlier15m)) {
                selectedVoforNotProximity = null;
            }

//        if(selectedVo.getDeviceName().equals("HJ")) {
            if(selectedVoforNotProximity != null) {
                if (j <= 10) {
                    log.info("j = {}", j);
                    if (selectedVoforNotProximity.getRssi1() < 0 && selectedVoforNotProximity.getRssi2() < 0 && selectedVoforNotProximity.getRssi3() < 0) {
                        selectedVoforNotProximity = startFilter2.initFirstValue(selectedVoforNotProximity, j);
                        j++;
                    } else {
                        selectedVoforNotProximity = null;
                    }
                }
                if(selectedVoforNotProximity != null) {
                    filteredVoforNotProximity = selectedVoforNotProximity;
//                    filteredVoforNotProximity = createFilteredVo3(selectedVoforNotProximity);
                    log.info("Filtered Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", filteredVoforNotProximity.getRssi1(), filteredVoforNotProximity.getRssi2(), filteredVoforNotProximity.getRssi3());
                    //AP 좌표 설정
                    if (triangleNumforNotProximity % 2 == 0) {
                        //rm+kf한거
                        ap1forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), h, filteredVoforNotProximity.getDistance1());
                        ap2forNotProximity = new Ap((w / 2.0) * triangleNumforNotProximity, 0, filteredVoforNotProximity.getDistance2());
                        ap3forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), h, filteredVoforNotProximity.getDistance3());
                        //rm만한거
//                        ap1forNoKalman = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), h, selectedVoforNotProximity.getDistance1());
//                        ap2forNoKalman = new Ap((w / 2.0) * triangleNumforNotProximity, 0, selectedVoforNotProximity.getDistance2());
//                        ap3forNoKalman = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), h, selectedVoforNotProximity.getDistance3());
                    } else {
                        //rm+kf한거
                        ap1forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), 0, filteredVoforNotProximity.getDistance1());
                        ap2forNotProximity = new Ap((w / 2.0) * triangleNumforNotProximity, h, filteredVoforNotProximity.getDistance2());
                        ap3forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), 0, filteredVoforNotProximity.getDistance3());
                        //rm만한거
//                        ap1forNoKalman = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), 0, selectedVoforNotProximity.getDistance1());
//                        ap2forNoKalman = new Ap((w / 2.0) * triangleNumforNotProximity, h, selectedVoforNotProximity.getDistance2());
//                        ap3forNoKalman = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), 0, selectedVoforNotProximity.getDistance3());
                    }
                    //rm+kf한거
                    Trilateration filteredTrforNotProximity = new Trilateration(originalVo.getDeviceName(), ap1forNotProximity, ap2forNotProximity, ap3forNotProximity);
                    //rm만한거
//                    Trilateration filteredTrforNoKalman = new Trilateration(originalVo.getDeviceName(), ap1forNoKalman, ap2forNoKalman, ap3forNoKalman);
                    //RM + Kalman
                    filteredUlforNotProximity = filteredTrforNotProximity.calcUserLocation();
                    //RM
//                    filteredUlforNoKalman = filteredTrforNoKalman.calcUserLocation();

                }
                else {
                    filteredUlforNotProximity = new UserLocation(999, 999, "ddd");
//                    filteredUlforNoKalman = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUlforNotProximity = new UserLocation(999, 999, "ddd");
//                filteredUlforNoKalman = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            originalUl = new UserLocation(999, 999, "ddd");
            filteredUlforNotProximity = new UserLocation(999, 999, "ddd");
//            filteredUlforNoKalman = new UserLocation(999, 999, "ddd");
        }


        //좌표 이상치 제거
//        if (rm.rmXYOutlier(filteredUlforNotProximity, w, h)) { //rm+kalman
        if (rm.rmXYOutlier(filteredUlforNotProximity, w, h)) { //rssi이상치제거만된 xy
            //이후꺼 다 new UserLocation(999, 999, "ddd");
            mAFilteredUlforNotProximity = new UserLocation(999, 999, "ddd");
            updateLocFilteredUlforNotProximity = new UserLocation(999, 999, "ddd");

        } else {
            //RM + Kalman + Loc RM + 2d MAF
//            mAFilteredUlforNotProximity = locMAFilter3.push(filteredUlforNotProximity);
            //RM + Loc RM + 2d MAF
            mAFilteredUlforNotProximity = locMAFilter3.push(filteredUlforNotProximity);

            if(mAFilteredUlforNotProximity != null) {
                //2D 칼만 필터
                xforNotProximity = locKalmanFilter2.predict();
                tempArrforNotProximity = new double[][]{{mAFilteredUlforNotProximity.getX()}, {mAFilteredUlforNotProximity.getY()}};
                x2forNotProximity = locKalmanFilter2.update(tempArrforNotProximity);
                //RM + Loc RM+ 2d MAF + 2d Kalman
                updateLocFilteredUlforNotProximity = new UserLocation(x2forNotProximity[0][0], x2forNotProximity[1][0], mAFilteredUlforNotProximity.getDeviceName());

                //제안 방법 성공 횟수
                proposedwithoutProximityFinishNum++;
                System.out.printf("Proposed without Proximity Finish Num : %d\n", proposedwithoutProximityFinishNum);
            }
            else
            {
                mAFilteredUlforNotProximity = new UserLocation(999, 999, "ddd");
                updateLocFilteredUlforNotProximity = new UserLocation(999, 999, "ddd");
            }
        }


        //UserLocation moveFilteredUl = filteredTr.moveUserLocation(updateLocFilteredUl);

        //System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());
        //System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());
        //System.out.printf("LocFiltered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", locFilteredUl.getX(), locFilteredUl.getY(), locFilteredUl.getDistanceDev());
        //System.out.printf("MAF Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", mAFilteredUl.getX(), mAFilteredUl.getY(), mAFilteredUl.getDistanceDev());
        //System.out.printf("Proposed Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY(), updateLocFilteredUl.getDistanceDev());

        System.out.printf("Original Location : (%.2f, %.2f)\n", originalUl.getX(), originalUl.getY());
        System.out.printf("Kalman Location : (%.2f, %.2f)\n", filteredUlforNotProximity.getX(), filteredUlforNotProximity.getY());
        System.out.printf("Proposed without Loc Filter, Proximity Location : (%.2f, %.2f)\n", mAFilteredUlforNotProximity.getX(), mAFilteredUlforNotProximity.getY());
        System.out.printf("Proposed without Proximity Location : (%.2f, %.2f)\n", updateLocFilteredUlforNotProximity.getX(), updateLocFilteredUlforNotProximity.getY());
        System.out.printf("Proposed Location : (%.2f, %.2f)\n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY());

        //original, kalman, ourNoLocNoProximity,ourNoProximity, our
        createCsvEx2(originalUl, filteredUlforNotProximity, mAFilteredUlforNotProximity,updateLocFilteredUlforNotProximity, updateLocFilteredUl);

//        4개찍을떄
        if(totalNum==1){
            ulList.add(0, originalUl);
            ulList.add(1, filteredUlforNotProximity);
            ulList.add(2, mAFilteredUlforNotProximity);
            ulList.add(3, updateLocFilteredUlforNotProximity);
        }
        else{
            ulList.set(0, originalUl);
            ulList.set(1, filteredUlforNotProximity);
            ulList.set(2, mAFilteredUlforNotProximity);
            ulList.set(3, updateLocFilteredUlforNotProximity);
        }

        log.info("total Num = {}", totalNum);

        return ulList;
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

//        if(valueTemp < 0 && valueTemp >= -37.0882 && valueTemp2 < -37.0882) { // 1m = 23, n=4.68 일때 2m =-37.0882
        if(valueTemp < 0 && valueTemp >= -45.3292 && valueTemp2 < -45.3292) { // 1m = 23, n=4.68 일때 3m =-45.3292
            return keyTemp;
        }

        return 0;
    }



    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 4.68;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
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

    private SelectedVO createMAFVo(SelectedVO originalVo) {
        double filterdRssi1 = mafFilter1.push(originalVo.getRssi1());
        double filterdRssi2 = mafFilter2.push(originalVo.getRssi2());
        double filterdRssi3 = mafFilter3.push(originalVo.getRssi3());

        return new SelectedVO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
        );
    }

    private SelectedVO createMAFVo2(SelectedVO originalVo) {
        double filterdRssi1 = mafFilter4.push(originalVo.getRssi1());
        double filterdRssi2 = mafFilter5.push(originalVo.getRssi2());
        double filterdRssi3 = mafFilter6.push(originalVo.getRssi3());

        return new SelectedVO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
        );
    }

    //칼만 필터 VO 생성 함수
//    public SelectedVO createFilteredVo(SelectedVO originalVo) {
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

    //실험 2 엑셀 파일 만들기
    public void createCsvEx2(UserLocation originalUl, UserLocation kalmanUl, UserLocation mAFilteredUlforNotProximity, UserLocation proposedWithoutProximity ,UserLocation proposedUl) {
        try {
            poiHelper.writeExcelforLocFilter(originalUl, kalmanUl, mAFilteredUlforNotProximity, proposedWithoutProximity, proposedUl);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}