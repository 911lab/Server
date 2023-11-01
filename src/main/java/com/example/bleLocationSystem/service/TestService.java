package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.ExelPOIHelper;
import com.example.bleLocationSystem.model.KalmanFilter;
import com.example.bleLocationSystem.model.UserLocation;
import com.example.bleLocationSystem.model.VO;

import java.io.IOException;
import java.util.ArrayList;

public class TestService {
    VO originalVo;
    VO roKalmanVo;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;




    int i=0;
    double outlier = -77;

    ExelPOIHelper poiHelper;

    public TestService() {
        poiHelper = new ExelPOIHelper();
        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();
    }

    public void trilateration(VO vo) {
        originalVo = vo;

        if(rmOutlier(originalVo))//이상치 제거
            i++;
            //KF
            roKalmanVo = createFilteredVo(originalVo);
            createCsv(originalVo, roKalmanVo);



    }

    //8개 짜리 정지상태 엑셀 파일 만들기
    public void createCsv(VO originalVo, VO roKalmanVo) {
        try {
            // 비콘 8개 각각 성능 테스트를 위한 엑셀 생성
//            poiHelper.writeTestExcel(originalVo, i);

            poiHelper.wrieteOneBeaconTestExcel(originalVo, roKalmanVo,i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //RSSI 이상치 제거
    public boolean rmOutlier(VO vo){

        return !(vo.getRssi1() <= outlier && vo.getRssi1() >= 0) && !(vo.getRssi2() <= outlier) && !(vo.getRssi3() <= outlier);
    }

    //칼만 필터 VO 생성 함수
    public VO createFilteredVo(VO originalVo) {

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
}
