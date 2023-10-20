package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j //로깅 어노테이션
public class ApService extends JFrame {

    VO originalVo;
    VO filteredVo;
    VO beforeFilteredVo;

    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    StartFilter startFilter;
    RssiFilter rssiFilter;

    LocKalmanFilter locKalmanFilter;

    double[][] x;

    double[][] x2;

    double[][] tempArr;


    Up UserPoint;
    Thread t;
    private float tempAlpha;
    private int lossNum;

    boolean initCheck;
    boolean numCheck;

    VO realNoFIlterVo;

    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;

    ExelPOIHelper poiHelper;
    ArrayList<UserLocation> ulList;
    RemoveOutlier rm;
    int i=0;
    double outlier = -86;
    @Getter
    double w= 20;
    @Getter
    double h= 15;

    public ApService() {


        poiHelper = new ExelPOIHelper();

//        kFilterForAp1 = new KalmanFilter();
//        kFilterForAp2 = new KalmanFilter();
//        kFilterForAp3 = new KalmanFilter();

        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();

        startFilter = new StartFilter();
        rssiFilter = new RssiFilter();

        locKalmanFilter = new LocKalmanFilter(0.1, 1, 1, 1, 0.1, 0.1);
        ulList = new ArrayList<UserLocation>();
        rm = new RemoveOutlier();
//        locKalmanFilter = new LocKalmanFilter(1, 1, 1, 1, 0.1, 0.1);

//        UserPoint = new Up();
//        t = new Thread(UserPoint);

//        initCheck = false;

    }

    //public UserLocation trilateration(VO vo) {

    public ArrayList<UserLocation> trilateration(VO vo) {
        originalVo = vo;
        realNoFIlterVo = new VO(originalVo.getDeviceName(), originalVo.getDistance1(), originalVo.getRssi1(), originalVo.getDistance2(), originalVo.getRssi2(), originalVo.getDistance3(), originalVo.getRssi3());

        if(!rm.rmOutlier(originalVo.getRssi1(),originalVo.getRssi2(),originalVo.getRssi3()))//이상치 제거
            return null;

        if(i <= 10) {
            log.info("i = {}", i);
            if(originalVo.getRssi1() < 0 && originalVo.getRssi2() < 0 && originalVo.getRssi3() < 0) {
                originalVo = startFilter.initFirstValue(originalVo, i);
                beforeFilteredVo = originalVo;
            } else {
                i--;
            }
        }

        if(originalVo != null) {

            Ap ap1 = new Ap(0, 0, originalVo.getDistance1());
            Ap ap2 = new Ap(w, 0, originalVo.getDistance2());
            Ap ap3 = new Ap(w/2.0, h, originalVo.getDistance3());

            //MAF
            filteredVo = createMAFVo(originalVo);

            //KF
//            filteredVo = createFilteredVo(originalVo);

            //Original
//            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, originalVo);
            //Temp
//            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, filteredVo);

            beforeFilteredVo = filteredVo;
//+-            beforeFilteredVo = originalVo;


            Ap filteredAp1 = new Ap(0, 0, filteredVo.getDistance1());
            Ap filteredAp2 = new Ap(w, 0, filteredVo.getDistance2());
            Ap filteredAp3 = new Ap(w/2.0, h, filteredVo.getDistance3());

            Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);

            Trilateration filteredTr = new Trilateration(filteredVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);



//        if(!initCheck) {
//            t.start();
//            initCheck = true;
//        }

            UserLocation ul = tr.calcUserLocation();
            UserLocation filteredUl = filteredTr.calcUserLocation();
            if(i==10){
                ulList.add(0,filteredUl);
            }
            else{
                ulList.set(0,filteredUl);
            }
            x = locKalmanFilter.predict();
            UserLocation locFilteredUl = new UserLocation(x[0][0], x[1][0]);


            tempArr = new double[][] {{filteredUl.getX()},
                                       {filteredUl.getY()}};

            x2 = locKalmanFilter.update(tempArr);
            UserLocation updateLocFilteredUl = new UserLocation(x2[0][0], x2[1][0]);

//            UserLocation moveFilteredUl = filteredTr.moveUserLocation(updateLocFilteredUl);

            log.info("originalVo = {}", originalVo.toString());
            log.info("filteredVo = {}", filteredVo.toString());

            System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());
            System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());

            System.out.printf("LocFiltered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", locFilteredUl.getX(), locFilteredUl.getY(), locFilteredUl.getDistanceDev());
            System.out.printf("LocFiltered Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", updateLocFilteredUl.getX(), updateLocFilteredUl.getY(), updateLocFilteredUl.getDistanceDev());
//            System.out.printf("Moved Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", moveFilteredUl.getX(), moveFilteredUl.getY(), moveFilteredUl.getDistanceDev());

            if(i==10){
                //ulList.add(0,filteredTr.moveUserLocation(filteredUl));
                ulList.add(1,updateLocFilteredUl);
            }
            else{
                //ulList.set(0,filteredTr.moveUserLocation(filteredUl));
                ulList.set(1,updateLocFilteredUl);
            }
            i++;
            createCsv(realNoFIlterVo, ul, filteredVo, filteredUl);
//            return locFilteredUl;
//            return moveFilteredUl;

            return ulList;

        }
        i++;
        return null;
    }

    private VO createMAFVo(VO originalVo) {
        double filterdRssi1 = mafFilter1.push(originalVo.getRssi1());
        double filterdRssi2 = mafFilter2.push(originalVo.getRssi2());
        double filterdRssi3 = mafFilter3.push(originalVo.getRssi3());

        return new VO(originalVo.getDeviceName(),
                calcDistance(filterdRssi1),
                filterdRssi1,
                calcDistance(filterdRssi2),
                filterdRssi2,
                calcDistance(filterdRssi3),
                filterdRssi3
        );
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

        tempAlpha = -30;
        lossNum = 4;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }

    //엑셀 파일 만들기
    public void createCsv(VO originalVo, UserLocation ul, VO filteredVo, UserLocation filteredUl) {
        try {
            // 성능 테스트를 위한 엑셀 생성
            poiHelper.writeExcel(originalVo, ul, filteredVo, filteredUl, i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
