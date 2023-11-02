package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;

import java.io.IOException;
import java.util.ArrayList;

public class TestService {
    VO originalVo;
    VO roKalmanVo;

    VO MafVo;

    VO roMafVo;

    VO roMafKalamnVo;

    VO roKalmanMafVo;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;


    MAFilter mafFilter4;
    MAFilter mafFilter5;
    MAFilter mafFilter6;


    MAFilter mafFilter7;
    MAFilter mafFilter8;
    MAFilter mafFilter9;

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap filteredAp1;
    Ap filteredAp2;
    Ap filteredAp3;

    private float tempAlpha;
    private int lossNum;

    UserLocation ul;

    UserLocation filteredUl;




    int i=0;
    // 15m -> -83
    // 10m -> -78
    double outlier = -78;

    ExelPOIHelper poiHelper;

    public TestService() {
        poiHelper = new ExelPOIHelper();
        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();

        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();
//        mafFilter4 = new MAFilter();
//        mafFilter5 = new MAFilter();
//        mafFilter6 = new MAFilter();
//        mafFilter7 = new MAFilter();
//        mafFilter8 = new MAFilter();
//        mafFilter9 = new MAFilter();
    }

    public void trilateration(VO vo) {

        originalVo = vo;

        if(originalVo.getRssi1() < 0 && originalVo.getRssi2() < 0 && originalVo.getRssi3() < 0) {
            i++;
            //MAF
//            MafVo = createMAFVo1(originalVo);

            if (rmOutlier(originalVo)) {//이상치 제거

                //KF
//            roKalmanVo = createFilteredVo(originalVo);

                //RO + MAF
//                roMafVo = createMAFVo2(originalVo);

                //RO + MAF + Kalman
                roMafVo = createMAFVo(originalVo);
                roMafKalamnVo = createFilteredVo(roMafVo);

                //RO + Kalman + MAF
//                roKalmanVo = createFilteredVo2(originalVo);
//                roKalmanMafVo = createMAFVo3(roKalmanVo);


                filteredAp1 = new Ap(0, 0, roMafKalamnVo.getDistance1());
                filteredAp2 = new Ap(10, 0, roMafKalamnVo.getDistance2());
                filteredAp3 = new Ap(5, 10, roMafKalamnVo.getDistance3());

                Trilateration filteredTr = new Trilateration(roMafKalamnVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);

                filteredUl = filteredTr.calcUserLocation();
            }
            else {
                filteredUl = new UserLocation(-999,-999);
            }

            ap1 = new Ap(0, 0, originalVo.getDistance1());
            ap2 = new Ap(10, 0, originalVo.getDistance2());
            ap3 = new Ap(5, 10, originalVo.getDistance3());

            Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);

            ul = tr.calcUserLocation();


            createCsv(ul, filteredUl);
        }
    }

    //8개 짜리 정지상태 엑셀 파일 만들기
    public void createCsv(UserLocation ul, UserLocation filterdUl) {
        try {
            // 비콘 8개 각각 성능 테스트를 위한 엑셀 생성
//            poiHelper.writeTestExcel(originalVo, i);

            poiHelper.wrieteOneBeaconTestExcel(ul, filterdUl,i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //RSSI 이상치 제거
    public boolean rmOutlier(VO vo){

        return !(vo.getRssi1() <= outlier) && !(vo.getRssi2() <= outlier) && !(vo.getRssi3() <= outlier) && !(vo.getRssi1() > 0) && !(vo.getRssi2() > 0) && !(vo.getRssi3() > 0);
    }

    //칼만 필터 VO 생성 함수 1
    public VO createFilteredVo(VO originalVo) {

        double filterdRssi1 = kFilterForAp1.kalmanFiltering(originalVo.getRssi1());
        double filterdRssi2 = kFilterForAp2.kalmanFiltering(originalVo.getRssi2());
        double filterdRssi3 = kFilterForAp3.kalmanFiltering(originalVo.getRssi3());

        return new VO(originalVo.getDeviceName(),
                        calcDistance(filterdRssi1),
                        filterdRssi1,
                        calcDistance(filterdRssi2),
                        filterdRssi2,
                        calcDistance(filterdRssi3),
                        filterdRssi3,
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
                        1
                        );
    }

    //칼만 필터 VO 생성 함수 2
//    public VO createFilteredVo2(VO originalVo) {
//
//        double filterdRssi1 = kFilterForAp4.kalmanFiltering(originalVo.getRssi1());
//        double filterdRssi2 = kFilterForAp5.kalmanFiltering(originalVo.getRssi2());
//        double filterdRssi3 = kFilterForAp6.kalmanFiltering(originalVo.getRssi3());
//
//        return new VO(originalVo.getDeviceName(),
//                1,
//                filterdRssi1,
//                1,
//                filterdRssi2,
//                1,
//                filterdRssi3,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1
//        );
//    }


    // MAF VO 생성 함수 1
    private VO createMAFVo(VO originalVo) {
        double filterdRssi1 = mafFilter1.push(originalVo.getRssi1());
        double filterdRssi2 = mafFilter2.push(originalVo.getRssi2());
        double filterdRssi3 = mafFilter3.push(originalVo.getRssi3());

        return new VO(originalVo.getDeviceName(),
                1,
                filterdRssi1,
                1,
                filterdRssi2,
                1,
                filterdRssi3,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1
        );
    }

    // MAF VO 생성 함수 2
//    private VO createMAFVo2(VO originalVo) {
//        double filterdRssi1 = mafFilter4.push(originalVo.getRssi1());
//        double filterdRssi2 = mafFilter5.push(originalVo.getRssi2());
//        double filterdRssi3 = mafFilter6.push(originalVo.getRssi3());
//
//        return new VO(originalVo.getDeviceName(),
//                1,
//                filterdRssi1,
//                1,
//                filterdRssi2,
//                1,
//                filterdRssi3,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1
//        );
//    }

    // MAF VO 생성 함수 3
//    private VO createMAFVo3(VO originalVo) {
//        double filterdRssi1 = mafFilter7.push(originalVo.getRssi1());
//        double filterdRssi2 = mafFilter8.push(originalVo.getRssi2());
//        double filterdRssi3 = mafFilter9.push(originalVo.getRssi3());
//
//        return new VO(originalVo.getDeviceName(),
//                1,
//                filterdRssi1,
//                1,
//                filterdRssi2,
//                1,
//                filterdRssi3,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1,
//                1
//        );
//    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -30;
        lossNum = 4;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

}
