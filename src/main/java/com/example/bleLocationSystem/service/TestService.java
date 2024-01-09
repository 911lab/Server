package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j //로깅 어노테이션
public class TestService {
    VO originalVo;
    SelectedVO filteredVo;

    VO originalVoforNotProximity;

    SelectedVO selectedVo;

    SelectedVO selectedVoforWeightAndKalman;
    SelectedVO selectedVoforNotProximity;

    SelectedVO weightVo;
    SelectedVO kalmanVo;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;
    KalmanFilter kFilterForAp9;


    StartFilter startFilter;
    StartFilter startFilter2;

    RssiFilter rssiFilter;

    LocKalmanFilter locKalmanFilter;
    LocKalmanFilter locKalmanFilter2;

    double[][] x;

    double[][] x2;

    double[][] tempArr;

    double[][] xforNotProximity;

    double[][] x2forNotProximity;


    Up UserPoint;
    Thread t;
    double tempAlpha;
    double lossNum;

    boolean initCheck;
    boolean numCheck;

    VO realNoFIlterVo;

    UserLocation filteredUl;

    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;

    MAFilter mafFilter4;
    MAFilter mafFilter5;
    MAFilter mafFilter6;

    LocMAFilter locMAFilter;
    LocMAFilter locMAFilter2;

    ExelPOIHelper poiHelper;
    ArrayList<UserLocation> ulList;
    RemoveOutlier rm;
    int i=0;
    int j=0;

    int weightFinishNum;
    int kalmanFinishNum;
    int proposedFinishNum;

    int totalNum;


    // 10m -> -78
//    double setting = 10.0;
//    double outlier = -78;
//    @Getter
//    double w = 10.0;
//    @Getter
//    double h = 5.0*Math.sqrt(3);   //8.66

    // 15m -> -83  -> -77
    double setting = 15.0;
    double outlier = -77.0;
    double minOutlier = -30.0;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99

    @Getter
    int triangleNum;
    @Getter
    int triangleNumforWeightAndKalman;
    @Getter
    int triangleNumforNotProximity;
    @Getter
    int proposedwithoutProximityFinishNum;

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap proximityAp;

    Ap filteredAp1;
    Ap filteredAp2;
    Ap filteredAp3;

    int finishedCount;

    int checkProximityNum;

    WeightFilter weightFilter1;
    WeightFilter weightFilter2;
    WeightFilter weightFilter3;

    private Ap weightAp1;
    private Ap weightAp2;
    private Ap weightAp3;
    private Ap kalmanAp1;
    private Ap kalmanAp2;
    private Ap kalmanAp3;

    UserLocation weightUl;
    UserLocation kalmanUl;

    UserLocation updateLocFilteredUl;
    Ap ap1forNotProximity;
    Ap ap2forNotProximity;
    Ap ap3forNotProximity;
    SelectedVO filteredVoforNotProximity;

    UserLocation filteredUlforNotProximity;
    UserLocation updateLocFilteredUlforNotProximity;

    public TestService() {


        poiHelper = new ExelPOIHelper();

        //RSSI 보정 프로세스
        startFilter = new StartFilter();
        startFilter2 = new StartFilter();

        weightFilter1 = new WeightFilter();
        weightFilter2 = new WeightFilter();
        weightFilter3 = new WeightFilter();

        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();

        mafFilter4 = new MAFilter();
        mafFilter5 = new MAFilter();
        mafFilter6 = new MAFilter();

        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();

        kFilterForAp4 = new KalmanFilter();
        kFilterForAp5 = new KalmanFilter();
        kFilterForAp6 = new KalmanFilter();

        kFilterForAp7 = new KalmanFilter();
        kFilterForAp8 = new KalmanFilter();
        kFilterForAp9 = new KalmanFilter();


        //위치 보정 프로세스
        locMAFilter = new LocMAFilter();
        locMAFilter2 = new LocMAFilter();

        locKalmanFilter = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        locKalmanFilter2 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);


        rm = new RemoveOutlier();
//        locKalmanFilter = new LocKalmanFilter(1, 1, 1, 1, 0.1, 0.1);

//        UserPoint = new Up();
//        t = new Thread(UserPoint);

//        initCheck = false;

        ulList = new ArrayList<UserLocation>();

        finishedCount = 0;

        weightFinishNum = 0;
        kalmanFinishNum = 0;
        proposedFinishNum = 0;
        proposedwithoutProximityFinishNum = 0;

        totalNum = 0;

    }



    public ArrayList<UserLocation> trilateration(VO vo) {
        originalVo = vo;
        originalVoforNotProximity = vo;

        totalNum++;

        //--------------------------------------------------------------Previous Research Method--------------------------------------------------------------
        triangleNumforWeightAndKalman = selectTriangle(originalVo);

        switch (triangleNumforWeightAndKalman) {
            case 0:
                selectedVoforWeightAndKalman = null;
                break;
            case 1:
                selectedVoforWeightAndKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                break;
            case 2:
                selectedVoforWeightAndKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                break;
            case 3:
                selectedVoforWeightAndKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
                break;
            case 4:
                selectedVoforWeightAndKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                break;
            case 5:
                selectedVoforWeightAndKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                break;
            case 6:
                selectedVoforWeightAndKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                break;
        }

        if(selectedVoforWeightAndKalman != null) {
            //------------------------------가중치 기법------------------------------
            weightVo = createWeightVo(selectedVoforWeightAndKalman);
            log.info("Weight Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", weightVo.getRssi1(), weightVo.getRssi2(), weightVo.getRssi3());

            if (triangleNumforWeightAndKalman % 2 == 0) {
                weightAp1 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman - 1), h, weightVo.getDistance1());
                weightAp2 = new Ap((w / 2.0) * triangleNumforWeightAndKalman, 0, weightVo.getDistance2());
                weightAp3 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman + 1), h, weightVo.getDistance3());
            } else {
                weightAp1 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman - 1), 0, weightVo.getDistance1());
                weightAp2 = new Ap((w / 2.0) * triangleNumforWeightAndKalman, h, weightVo.getDistance2());
                weightAp3 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman + 1), 0, weightVo.getDistance3());
            }
            Trilateration weightTr = new Trilateration(weightVo.getDeviceName(), weightAp1, weightAp2, weightAp3);
            weightUl = weightTr.calcUserLocation();
            weightFinishNum++;
            System.out.printf("Weight Finish Num : %d\n", weightFinishNum);


            //------------------------------칼만 필터 단일 기법------------------------------
            kalmanVo = createFilteredVo2(selectedVoforWeightAndKalman);
            log.info("Kalman Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());

            if (triangleNumforWeightAndKalman % 2 == 0) {
                kalmanAp1 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman - 1), h, kalmanVo.getDistance1());
                kalmanAp2 = new Ap((w / 2.0) * triangleNumforWeightAndKalman, 0, kalmanVo.getDistance2());
                kalmanAp3 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman + 1), h, kalmanVo.getDistance3());
            } else {
                kalmanAp1 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman - 1), 0, kalmanVo.getDistance1());
                kalmanAp2 = new Ap((w / 2.0) * triangleNumforWeightAndKalman, h, kalmanVo.getDistance2());
                kalmanAp3 = new Ap((w / 2.0) * (triangleNumforWeightAndKalman + 1), 0, kalmanVo.getDistance3());
            }
            Trilateration kalmanTr = new Trilateration(kalmanVo.getDeviceName(), kalmanAp1, kalmanAp2, kalmanAp3);
            kalmanUl = kalmanTr.calcUserLocation();
            kalmanFinishNum++;
            System.out.printf("Kalman Finish Num : %d\n", kalmanFinishNum);
        } else {
            weightUl = new UserLocation(999, 999, "ddd");
            kalmanUl = new UserLocation(999, 999, "ddd");
        }

        //--------------------------------------------------------------Proposed Method--------------------------------------------------------------

        //--------------------------------------------------------------Proposed Proximity Method--------------------------------------------------------------
        checkProximityNum = checkProximity(originalVo);

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
            triangleNum = selectTriangle(originalVo);
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
                    selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
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
                if (!rm.rmOutlier(selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3(), outlier)) {
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
                        if (triangleNum % 2 == 0) {
                            ap1 = new Ap((w / 2.0) * (triangleNum - 1), h, selectedVo.getDistance1());
                            ap2 = new Ap((w / 2.0) * triangleNum, 0, selectedVo.getDistance2());
                            ap3 = new Ap((w / 2.0) * (triangleNum + 1), h, selectedVo.getDistance3());
                        } else {
                            ap1 = new Ap((w / 2.0) * (triangleNum - 1), 0, selectedVo.getDistance1());
                            ap2 = new Ap((w / 2.0) * triangleNum, h, selectedVo.getDistance2());
                            ap3 = new Ap((w / 2.0) * (triangleNum + 1), 0, selectedVo.getDistance3());
                        }
                        //MAF
                        filteredVo = createMAFVo(selectedVo);
                        //KF
                        filteredVo = createFilteredVo(filteredVo);
                        //AP 좌표 설정
                        //w = 5, h = 10
                        if (triangleNum % 2 == 0) {
                            filteredAp1 = new Ap((w / 2.0) * (triangleNum - 1), h, filteredVo.getDistance1());
                            filteredAp2 = new Ap((w / 2.0) * triangleNum, 0, filteredVo.getDistance2());
                            filteredAp3 = new Ap((w / 2.0) * (triangleNum + 1), h, filteredVo.getDistance3());
                        } else {
                            filteredAp1 = new Ap((w / 2.0) * (triangleNum - 1), 0, filteredVo.getDistance1());
                            filteredAp2 = new Ap((w / 2.0) * triangleNum, h, filteredVo.getDistance2());
                            filteredAp3 = new Ap((w / 2.0) * (triangleNum + 1), 0, filteredVo.getDistance3());
                        }
                        Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);
                        Trilateration filteredTr = new Trilateration(filteredVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);
                        UserLocation ul = tr.calcUserLocation();
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

//            if (mAFilteredUl == null) {
//                System.out.println("LOC MAF CUT");
//            }

//            if(i==10){
//                ulList.add(0,mAFilteredUl);
//            }
//            else{
//                ulList.set(0,mAFilteredUl);
//            }

//            finishedCount++;
            //log.info("Finished Count = {}", finishedCount);

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

        //--------------------------------------------------------------Proposed Method without Proximity--------------------------------------------------------------

        triangleNumforNotProximity = selectTriangle(originalVoforNotProximity);

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
                selectedVoforNotProximity = createSelectVO(originalVoforNotProximity.getDeviceName(), originalVoforNotProximity.getRssi3(), originalVoforNotProximity.getRssi4(), originalVoforNotProximity.getRssi4());
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

        if(selectedVoforNotProximity != null) {
            log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVoforNotProximity.getRssi1(), selectedVoforNotProximity.getRssi2(), selectedVoforNotProximity.getRssi3());
            //RSSI 이상치 제거
            if (!rm.rmOutlier(selectedVoforNotProximity.getRssi1(), selectedVoforNotProximity.getRssi2(), selectedVoforNotProximity.getRssi3(), outlier)) {
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
                    //MAF
                    filteredVoforNotProximity = createMAFVo2(selectedVoforNotProximity);
                    //KF
                    filteredVoforNotProximity = createFilteredVo3(filteredVoforNotProximity);
                    //AP 좌표 설정
                    if (triangleNumforNotProximity % 2 == 0) {
                        ap1forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), h, filteredVoforNotProximity.getDistance1());
                        ap2forNotProximity = new Ap((w / 2.0) * triangleNumforNotProximity, 0, filteredVoforNotProximity.getDistance2());
                        ap3forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), h, filteredVoforNotProximity.getDistance3());
                    } else {
                        ap1forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity - 1), 0, filteredVoforNotProximity.getDistance1());
                        ap2forNotProximity = new Ap((w / 2.0) * triangleNumforNotProximity, h, filteredVoforNotProximity.getDistance2());
                        ap3forNotProximity = new Ap((w / 2.0) * (triangleNumforNotProximity + 1), 0, filteredVoforNotProximity.getDistance3());
                    }
                    Trilateration filteredTrforNotProximity = new Trilateration(originalVo.getDeviceName(), ap1forNotProximity, ap2forNotProximity, ap3forNotProximity);
                    filteredUlforNotProximity = filteredTrforNotProximity.calcUserLocation();
                }
                else {
                    filteredUlforNotProximity = new UserLocation(999, 999, "ddd");
                }
            }
            else {
                filteredUlforNotProximity = new UserLocation(999, 999, "ddd");
            }
        }
        else {
            filteredUlforNotProximity = new UserLocation(999, 999, "ddd");
        }

        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUlforNotProximity, w, h)) {
            filteredUlforNotProximity = null;
        }

        if(filteredUlforNotProximity != null) {
            //위치 MAF
            UserLocation mAFilteredUlforNotProximity = locMAFilter2.push(filteredUlforNotProximity);

            if(mAFilteredUlforNotProximity != null) {
                //2D 칼만 필터
                xforNotProximity = locKalmanFilter2.predict();
                tempArr = new double[][]{{mAFilteredUlforNotProximity.getX()}, {mAFilteredUlforNotProximity.getY()}};
                x2forNotProximity = locKalmanFilter2.update(tempArr);
                updateLocFilteredUlforNotProximity = new UserLocation(x2forNotProximity[0][0], x2forNotProximity[1][0], mAFilteredUlforNotProximity.getDeviceName());

                //제안 방법 성공 횟수
                proposedwithoutProximityFinishNum++;
                System.out.printf("Proposed without Proximity Finish Num : %d\n", proposedwithoutProximityFinishNum);
            }
            else
            {
                updateLocFilteredUlforNotProximity = new UserLocation(999, 999, "ddd");
            }
        }
        else
        {
            updateLocFilteredUlforNotProximity = new UserLocation(999, 999, "ddd");
        }


        //UserLocation moveFilteredUl = filteredTr.moveUserLocation(updateLocFilteredUl);

        //System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());
        //System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());
        //System.out.printf("LocFiltered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", locFilteredUl.getX(), locFilteredUl.getY(), locFilteredUl.getDistanceDev());
        //System.out.printf("MAF Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", mAFilteredUl.getX(), mAFilteredUl.getY(), mAFilteredUl.getDistanceDev());
        //System.out.printf("Proposed Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY(), updateLocFilteredUl.getDistanceDev());

        System.out.printf("Weight Location : (%.2f, %.2f)\n", weightUl.getX(), weightUl.getY());
        System.out.printf("Kalman Location : (%.2f, %.2f)\n", kalmanUl.getX(), kalmanUl.getY());
        System.out.printf("Proposed Location : (%.2f, %.2f)\n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY());
        System.out.printf("Proposed without Proximity Location : (%.2f, %.2f)\n", updateLocFilteredUlforNotProximity.getX(), updateLocFilteredUlforNotProximity.getY());

        //createCsvEx2(weightUl, kalmanUl, updateLocFilteredUl, updateLocFilteredUlforNotProximity);

        //4개찍을떄
        if(totalNum==1){
            ulList.add(0, weightUl);
            ulList.add(1, kalmanUl);
            ulList.add(2, updateLocFilteredUl);
            ulList.add(3, updateLocFilteredUlforNotProximity);
        }
        else{
            ulList.set(0, weightUl);
            ulList.set(1, kalmanUl);
            ulList.set(2, updateLocFilteredUl);
            ulList.set(3, updateLocFilteredUlforNotProximity);
        }

        log.info("total Num = {}", totalNum);

        return ulList;

//        } else if(selectedVo.getDeviceName().equals("BG")) {
//            if(j <= 10) {
//                log.info("j = {}", j);
//                if(selectedVo.getRssi1() < 0 && selectedVo.getRssi2() < 0 && selectedVo.getRssi3() < 0) {
//                    selectedVo = startFilter2.initFirstValue(selectedVo, j);
////                beforeFilteredVo = selectedVo;
//                } else {
//                    return null;
//                }
//            }
//
//            if(selectedVo != null) {
//
//                //w = 5, h = 10
//                if(triangleNum%2 == 0) {
//                    ap1 = new Ap((w/2.0)*(triangleNum-1), h, selectedVo.getDistance1());
//                    ap2 = new Ap((w/2.0)*triangleNum, 0, selectedVo.getDistance2());
//                    ap3 = new Ap((w/2.0)*(triangleNum+1), h, selectedVo.getDistance3());
//                }
//                else {
//                    ap1 = new Ap((w/2.0)*(triangleNum-1), 0, selectedVo.getDistance1());
//                    ap2 = new Ap((w/2.0)*triangleNum, h, selectedVo.getDistance2());
//                    ap3 = new Ap((w/2.0)*(triangleNum+1), 0, selectedVo.getDistance3());
//                }
////            log.info("selectedVo = {}", selectedVo.toString());
//                //MAF
//                filteredVo = createMAFVo2(selectedVo);
//                //log.info("filteredVo = {}", filteredVo.toString());
//
//                //KF
//                filteredVo = createFilteredVo2(filteredVo);
//
//                //Original
////            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, originalVo);
//                //Temp
////            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, filteredVo);
//
////            beforeFilteredVo = filteredVo;
////+-            beforeFilteredVo = originalVo;
//
//
//                //w = 5, h = 10
//                if(triangleNum%2 == 0) {
//                    filteredAp1 = new Ap((w/2.0)*(triangleNum-1), h, filteredVo.getDistance1());
//                    filteredAp2 = new Ap((w/2.0)*triangleNum, 0, filteredVo.getDistance2());
//                    filteredAp3 = new Ap((w/2.0)*(triangleNum+1), h, filteredVo.getDistance3());
//                }
//                else {
//                    filteredAp1 = new Ap((w/2.0)*(triangleNum-1), 0, filteredVo.getDistance1());
//                    filteredAp2 = new Ap((w/2.0)*triangleNum, h, filteredVo.getDistance2());
//                    filteredAp3 = new Ap((w/2.0)*(triangleNum+1), 0, filteredVo.getDistance3());
//                }
//
//                Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);
//
//                Trilateration filteredTr = new Trilateration(filteredVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);
//
//
////        if(!initCheck) {
////            t.start();
////            initCheck = true;
////        }
//
//                UserLocation ul = tr.calcUserLocation();
//                UserLocation filteredUl = filteredTr.calcUserLocation();
//
////            log.info("original dis1 = {}, dis2 = {}, dis3 = {}", originalVo.getDistance1(), originalVo.getDistance2(), originalVo.getDistance3());
////            log.info("filtered dis1 = {}, dis2 = {}, dis3 = {}", filteredVo.getDistance1(), filteredVo.getDistance2(), filteredVo.getDistance3());
//
////            System.out.printf("Basic Location : (%.2f, %.2f)\n", filteredUl.getX(), filteredUl.getY());
//
//                //좌표 이상치 제거
//                if(rm.rmXYOutlier(filteredUl, w, h))
//                    return null;
//
//
//
//                UserLocation mAFilteredUl = locMAFilter2.push(filteredUl);
//                if (mAFilteredUl == null) {
//                    System.out.println("LOC MAF CUT");
//                    return null;
//                }
//
////            if(i==10){
////                ulList.add(0,mAFilteredUl);
////            }
////            else{
////                ulList.set(0,mAFilteredUl);
////            }
//
//                finishedCount++;
//                //log.info("Finished Count = {}", finishedCount);
//
//                x = locKalmanFilter2.predict();
////            UserLocation locFilteredUl = new UserLocation(x[0][0], x[1][0]);
//
//                tempArr = new double[][] {{mAFilteredUl.getX()},
//                        {mAFilteredUl.getY()}};
//
//                x2 = locKalmanFilter2.update(tempArr);
//                UserLocation updateLocFilteredUl = new UserLocation(x2[0][0], x2[1][0], mAFilteredUl.getDeviceName());
//
////            UserLocation moveFilteredUl = filteredTr.moveUserLocation(updateLocFilteredUl);
//
//
//
////            System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());
////            System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());
//
////            System.out.printf("LocFiltered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", locFilteredUl.getX(), locFilteredUl.getY(), locFilteredUl.getDistanceDev());
//
//                //System.out.printf("MAF Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", mAFilteredUl.getX(), mAFilteredUl.getY(), mAFilteredUl.getDistanceDev());
//                System.out.printf("LocFiltered Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY(), updateLocFilteredUl.getDistanceDev());
//
////            System.out.printf("Moved Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", moveFilteredUl.getX(), moveFilteredUl.getY(), moveFilteredUl.getDistanceDev());
//
//                //2개찍을떄
////            if(i==10){
////                //ulList.add(0,filteredTr.moveUserLocation(filteredUl));
////                ulList.add(1,updateLocFilteredUl);
////            }
////            else{
////                //ulList.set(0,filteredTr.moveUserLocation(filteredUl));
////                ulList.set(1,updateLocFilteredUl);
////            }
//
//                j++;
////            createCsv(originalVo, ul, filteredVo, filteredUl);
////            return locFilteredUl;
////            return moveFilteredUl;
//
////                return mAFilteredUl;
////            return mAFilteredUl;
//            return updateLocFilteredUl;
//
//            }
//            j++;
//            return null;
//        }
//        //log.info("start");
//
//
//        //i++;
//        return null;
//        }
//        return null;
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
        if(valueTemp < 0 && valueTemp >= -37 && valueTemp2 < -37) {  //1.5m
//        if(valueTemp < 0 && valueTemp >= -30 && valueTemp2 < -30) {  //1m
            return keyTemp;
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

    private SelectedVO createWeightVo(SelectedVO originalVo) {
        double filterdRssi1 = weightFilter1.feedBack(originalVo.getRssi1());
        double filterdRssi2 = weightFilter2.feedBack(originalVo.getRssi2());
        double filterdRssi3 = weightFilter3.feedBack(originalVo.getRssi3());


        return new SelectedVO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
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
    public SelectedVO createFilteredVo(SelectedVO originalVo) {

        double filterdRssi1 = kFilterForAp1.kalmanFiltering(originalVo.getRssi1());
        double filterdRssi2 = kFilterForAp2.kalmanFiltering(originalVo.getRssi2());
        double filterdRssi3 = kFilterForAp3.kalmanFiltering(originalVo.getRssi3());

        return new SelectedVO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
        );
    }

    public SelectedVO createFilteredVo2(SelectedVO originalVo) {

        double filterdRssi1 = kFilterForAp4.kalmanFiltering(originalVo.getRssi1());
        double filterdRssi2 = kFilterForAp5.kalmanFiltering(originalVo.getRssi2());
        double filterdRssi3 = kFilterForAp6.kalmanFiltering(originalVo.getRssi3());

        return new SelectedVO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
        );
    }

    public SelectedVO createFilteredVo3(SelectedVO originalVo) {

        double filterdRssi1 = kFilterForAp7.kalmanFiltering(originalVo.getRssi1());
        double filterdRssi2 = kFilterForAp8.kalmanFiltering(originalVo.getRssi2());
        double filterdRssi3 = kFilterForAp9.kalmanFiltering(originalVo.getRssi3());

        return new SelectedVO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
        );
    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 3.81;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

    //엑셀 파일 만들기
    public void createCsv(VO originalVo, UserLocation ul, SelectedVO filteredVo, UserLocation filteredUl) {
        try {
            // 성능 테스트를 위한 엑셀 생성
            poiHelper.writeExcel(originalVo, ul, filteredVo, filteredUl, i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //실험 2 엑셀 파일 만들기
    public void createCsvEx2(UserLocation weightUl, UserLocation kalmanUl, UserLocation proposedUl, UserLocation proposedWithoutProximity) {
        try {
            poiHelper.writeExcelforEx2(weightUl, kalmanUl, proposedUl, proposedWithoutProximity, weightFinishNum, kalmanFinishNum, proposedFinishNum, proposedwithoutProximityFinishNum);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
