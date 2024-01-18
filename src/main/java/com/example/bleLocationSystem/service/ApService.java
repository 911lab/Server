package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j //로깅 어노테이션
public class ApService extends JFrame {

    VO originalVo;
    SelectedVO filteredVo;
//    SelectedVO beforeFilteredVo;

    SelectedVO selectedVo;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    StartFilter startFilter;
    StartFilter startFilter2;

    RssiFilter rssiFilter;

    LocKalmanFilter locKalmanFilter;
    LocKalmanFilter locKalmanFilter2;

    double[][] x;

    double[][] x2;

    double[][] tempArr;


    Up UserPoint;
    Thread t;
    private double tempAlpha;
    private double lossNum;

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

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap proximityAp;

    Ap filteredAp1;
    Ap filteredAp2;
    Ap filteredAp3;

    int finishedCount;

    int checkProximityNum;

    public ApService() {


        poiHelper = new ExelPOIHelper();

        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();

        kFilterForAp4 = new KalmanFilter();
        kFilterForAp5 = new KalmanFilter();
        kFilterForAp6 = new KalmanFilter();

        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();

        mafFilter4 = new MAFilter();
        mafFilter5 = new MAFilter();
        mafFilter6 = new MAFilter();

        locMAFilter = new LocMAFilter();
        locMAFilter2 = new LocMAFilter();

        startFilter = new StartFilter();
        startFilter2 = new StartFilter();
//        rssiFilter = new RssiFilter();

        locKalmanFilter = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        locKalmanFilter2 = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);

        //ulList = new ArrayList<UserLocation>();
        rm = new RemoveOutlier();
//        locKalmanFilter = new LocKalmanFilter(1, 1, 1, 1, 0.1, 0.1);

//        UserPoint = new Up();
//        t = new Thread(UserPoint);

//        initCheck = false;

        finishedCount = 0;

    }

//    public ArrayList<UserLocation> trilateration(VO vo) {

    public UserLocation trilateration(VO vo) {
        originalVo = vo;

        checkProximityNum = checkProximity(originalVo);

        if(checkProximityNum != 0) {

            switch (checkProximityNum) {
                case 0:
                    return null;
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

                if (checkProximityNum == 1 ) {
                    proximityAp = new Ap(0, 0, selectedVo.getDistance1());
                }
                else if (checkProximityNum == 8) {  //beacon의 갯수 -> 가장 마지막 번호
                    proximityAp = new Ap((w / 2.0) * (checkProximityNum - 1), h, selectedVo.getDistance3());
                }
                else if (checkProximityNum % 2 == 0) {
                    proximityAp = new Ap((w / 2.0) * (checkProximityNum - 1), h, selectedVo.getDistance2());
                }
                else {
                    proximityAp = new Ap((w / 2.0) * (checkProximityNum - 1), 0, selectedVo.getDistance2());
                }
            }

            Trilateration proximityTr = new Trilateration(originalVo.getDeviceName(), proximityAp, checkProximityNum);

            filteredUl = proximityTr.calcProximityLocation();

        }else {

            triangleNum = selectTriangle(originalVo);

            switch (triangleNum) {
                case 0:
                    return null;
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
//        realNoFIlterVo = new VO(originalVo.getDeviceName(), originalVo.getDistance1(), originalVo.getRssi1(), originalVo.getDistance2(), originalVo.getRssi2(), originalVo.getDistance3(), originalVo.getRssi3());

            log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3());

            //RSSI 이상치 제거
            if (!rm.rmOutlier(selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3(), outlier))
            {
                return null;
            }
//            return null;


//        if(selectedVo.getDeviceName().equals("HJ")) {

            if (i <= 10) {
                log.info("i = {}", i);
                if (selectedVo.getRssi1() < 0 && selectedVo.getRssi2() < 0 && selectedVo.getRssi3() < 0) {
                    selectedVo = startFilter.initFirstValue(selectedVo, i);
                    i++;
//                beforeFilteredVo = selectedVo;
                } else {
                    return null;
                }
            }

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
//            log.info("selectedVo = {}", selectedVo.toString());
                //MAF
                filteredVo = createMAFVo(selectedVo);
                //log.info("filteredVo = {}", filteredVo.toString());

                //KF
                filteredVo = createFilteredVo(filteredVo);

                //Original
//            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, originalVo);
                //Temp
//            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, filteredVo);

//            beforeFilteredVo = filteredVo;
//+-            beforeFilteredVo = originalVo;


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


//        if(!initCheck) {
//            t.start();
//            initCheck = true;
//        }

                UserLocation ul = tr.calcUserLocation();
                filteredUl = filteredTr.calcUserLocation();

            }
            else {
                return null;
            }
        }

//            log.info("original dis1 = {}, dis2 = {}, dis3 = {}", originalVo.getDistance1(), originalVo.getDistance2(), originalVo.getDistance3());
//            log.info("filtered dis1 = {}, dis2 = {}, dis3 = {}", filteredVo.getDistance1(), filteredVo.getDistance2(), filteredVo.getDistance3());

//            System.out.printf("Basic Location : (%.2f, %.2f)\n", filteredUl.getX(), filteredUl.getY());


        //좌표 이상치 제거
        if (rm.rmXYOutlier(filteredUl, w, h))
            return null;

        //위치 MAF
        UserLocation mAFilteredUl = locMAFilter.push(filteredUl);

        if (mAFilteredUl == null) {
            System.out.println("LOC MAF CUT");
            return null;
        }

//            if(i==10){
//                ulList.add(0,mAFilteredUl);
//            }
//            else{
//                ulList.set(0,mAFilteredUl);
//            }

        finishedCount++;
            //log.info("Finished Count = {}", finishedCount);

        //2D 칼만 필터
        x = locKalmanFilter.predict();
//            UserLocation locFilteredUl = new UserLocation(x[0][0], x[1][0]);

        tempArr = new double[][]{{mAFilteredUl.getX()}, {mAFilteredUl.getY()}};

        x2 = locKalmanFilter.update(tempArr);

        UserLocation updateLocFilteredUl = new UserLocation(x2[0][0], x2[1][0], mAFilteredUl.getDeviceName());

//            UserLocation moveFilteredUl = filteredTr.moveUserLocation(updateLocFilteredUl);


//            System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());
//            System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());

//            System.out.printf("LocFiltered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", locFilteredUl.getX(), locFilteredUl.getY(), locFilteredUl.getDistanceDev());

            //System.out.printf("MAF Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", mAFilteredUl.getX(), mAFilteredUl.getY(), mAFilteredUl.getDistanceDev());
            System.out.printf("LocFiltered Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY(), updateLocFilteredUl.getDistanceDev());

//            System.out.printf("Moved Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", moveFilteredUl.getX(), moveFilteredUl.getY(), moveFilteredUl.getDistanceDev());

            //2개찍을떄
//            if(i==10){
//                //ulList.add(0,filteredTr.moveUserLocation(filteredUl));
//                ulList.add(1,updateLocFilteredUl);
//            }
//            else{
//                //ulList.set(0,filteredTr.moveUserLocation(filteredUl));
//                ulList.set(1,updateLocFilteredUl);
//            }

//            i++;
//            createCsv(originalVo, ul, filteredVo, filteredUl);
//            return locFilteredUl;
//            return moveFilteredUl;

        return updateLocFilteredUl;
//            return mAFilteredUl;
//            return updateLocFilteredUl;


//            i++;
//
//            return null;

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


        //2m -> -34.4692
        //3m -> -41.1783
        if(valueTemp >= -41.1783 && valueTemp2 < -41.1783) {
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

    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 4.68;

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
