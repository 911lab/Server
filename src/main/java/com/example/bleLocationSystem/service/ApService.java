package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j //로깅 어노테이션
public class ApService {

    VO originalVo;
    VO filteredVo;
    VO beforeFilteredVo;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    Up UserPoint;

    private float tempAlpha;
    private int lossNum;

    ExelPOIHelper poiHelper;

    int i=1;

    public ApService() {
        poiHelper = new ExelPOIHelper();
        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();

        UserPoint = new Up();
        Thread t = new Thread(UserPoint);
        t.start();

    }

    public UserLocation trilateration(VO vo) {

        originalVo = vo;

        filteredVo = createFilteredVo(originalVo);

        Ap ap1 = new Ap(0,0, originalVo.getDistance1());
        Ap ap2 = new Ap(10,0, originalVo.getDistance2());
        Ap ap3 = new Ap(5,10, originalVo.getDistance3());

        Ap filteredAp1 = new Ap(0,0, filteredVo.getDistance1());
        Ap filteredAp2 = new Ap(10,0, filteredVo.getDistance2());
        Ap filteredAp3 = new Ap(5,10, filteredVo.getDistance3());

        Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);

        Trilateration filteredTr = new Trilateration(filteredVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);

        UserLocation ul = tr.calcUserLocation(UserPoint);
        UserLocation filteredUl = filteredTr.calcUserLocation(UserPoint);


        log.info("originalVo = {}", originalVo.toString());
        log.info("filteredVo = {}", filteredVo.toString());

        System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());

        System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());


        createCsv(originalVo ,ul, filteredVo, filteredUl);

        return ul;
    }

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
                        filterdRssi3
                        );
    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -59;
        lossNum = 2;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

    //엑셀 파일 만들기
    public void createCsv(VO originalVo, UserLocation ul, VO filteredVo, UserLocation filteredUl) {
        try {
            // 성능 테스트를 위한 엑셀 생성
            i = poiHelper.writeExcel(originalVo, ul, filteredVo, filteredUl, i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
