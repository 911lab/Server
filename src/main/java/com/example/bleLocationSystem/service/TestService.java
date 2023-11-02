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




    int i=0;
    double outlier = -83;

    ExelPOIHelper poiHelper;

    public TestService() {
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
        mafFilter7 = new MAFilter();
        mafFilter8 = new MAFilter();
        mafFilter9 = new MAFilter();
    }

    public void trilateration(VO vo) {

        originalVo = vo;

        if(originalVo.getRssi1() < 0) {
            i++;
            //MAF
            MafVo = createMAFVo1(originalVo);

            if (rmOutlier(originalVo)) {//이상치 제거

                //KF
//            roKalmanVo = createFilteredVo(originalVo);

                //RO + MAF
                roMafVo = createMAFVo2(originalVo);

                //RO + MAF + Kalman
                roMafKalamnVo = createFilteredVo1(roMafVo);

                //RO + Kalman + MAF
                roKalmanVo = createFilteredVo2(originalVo);
                roKalmanMafVo = createMAFVo3(roKalmanVo);

            } else {
                roMafVo = new VO(originalVo.getDeviceName(),
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
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

                roMafKalamnVo = new VO(originalVo.getDeviceName(),
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
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

                roKalmanMafVo = new VO(originalVo.getDeviceName(),
                        1,
                        1,
                        1,
                        1,
                        1,
                        1,
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


            createCsv(originalVo, MafVo, roMafVo, roMafKalamnVo, roKalmanMafVo);
        }
    }

    //8개 짜리 정지상태 엑셀 파일 만들기
    public void createCsv(VO originalVo, VO MafVo, VO roMafVo, VO roMafKalmanVo, VO roKalmanMafVo) {
        try {
            // 비콘 8개 각각 성능 테스트를 위한 엑셀 생성
//            poiHelper.writeTestExcel(originalVo, i);

            poiHelper.wrieteOneBeaconTestExcel(originalVo, MafVo, roMafVo, roMafKalmanVo, roKalmanMafVo,i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //RSSI 이상치 제거
    public boolean rmOutlier(VO vo){

        return !(vo.getRssi1() <= outlier) && !(vo.getRssi2() <= outlier) && !(vo.getRssi3() <= outlier) && !(vo.getRssi1() > 0);
    }

    //칼만 필터 VO 생성 함수 1
    public VO createFilteredVo1(VO originalVo) {

        double filterdRssi1 = kFilterForAp1.kalmanFiltering(originalVo.getRssi1());
        double filterdRssi2 = kFilterForAp2.kalmanFiltering(originalVo.getRssi2());
        double filterdRssi3 = kFilterForAp3.kalmanFiltering(originalVo.getRssi3());

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

    //칼만 필터 VO 생성 함수 2
    public VO createFilteredVo2(VO originalVo) {

        double filterdRssi1 = kFilterForAp4.kalmanFiltering(originalVo.getRssi1());
        double filterdRssi2 = kFilterForAp5.kalmanFiltering(originalVo.getRssi2());
        double filterdRssi3 = kFilterForAp6.kalmanFiltering(originalVo.getRssi3());

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


    // MAF VO 생성 함수 1
    private VO createMAFVo1(VO originalVo) {
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
    private VO createMAFVo2(VO originalVo) {
        double filterdRssi1 = mafFilter4.push(originalVo.getRssi1());
        double filterdRssi2 = mafFilter5.push(originalVo.getRssi2());
        double filterdRssi3 = mafFilter6.push(originalVo.getRssi3());

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

    // MAF VO 생성 함수 3
    private VO createMAFVo3(VO originalVo) {
        double filterdRssi1 = mafFilter7.push(originalVo.getRssi1());
        double filterdRssi2 = mafFilter8.push(originalVo.getRssi2());
        double filterdRssi3 = mafFilter9.push(originalVo.getRssi3());

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

}
