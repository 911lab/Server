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
    SelectedVO beforeFilteredVo;

    SelectedVO selectedVo;

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

    LocMAFilter locMAFilter;

    ExelPOIHelper poiHelper;
    ArrayList<UserLocation> ulList;
    RemoveOutlier rm;
    int i=0;
    @Getter
    double w= 5;
    @Getter
    double h= 10;

    int triangleNum;

    Ap ap1;
    Ap ap2;
    Ap ap3;

    Ap filteredAp1;
    Ap filteredAp2;
    Ap filteredAp3;

    public ApService() {


        poiHelper = new ExelPOIHelper();

//        kFilterForAp1 = new KalmanFilter();
//        kFilterForAp2 = new KalmanFilter();
//        kFilterForAp3 = new KalmanFilter();

        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();

        locMAFilter = new LocMAFilter();

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
        log.info("start");
        triangleNum = selectTriangle(originalVo);


        switch (triangleNum) {
            case 0:
                return null;
            case 1:
                selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(),originalVo.getRssi2(),originalVo.getRssi3());
                break;
            case 2:
                selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(),originalVo.getRssi3(),originalVo.getRssi4());
                break;
            case 3:
                selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(),originalVo.getRssi4(),originalVo.getRssi4());
                break;
            case 4:
                selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(),originalVo.getRssi5(),originalVo.getRssi6());
                break;
            case 5:
                selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(),originalVo.getRssi6(),originalVo.getRssi7());
                break;
            case 6:
                selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(),originalVo.getRssi7(),originalVo.getRssi8());
                break;
        }
//        realNoFIlterVo = new VO(originalVo.getDeviceName(), originalVo.getDistance1(), originalVo.getRssi1(), originalVo.getDistance2(), originalVo.getRssi2(), originalVo.getDistance3(), originalVo.getRssi3());

        if(!rm.rmOutlier(selectedVo.getRssi1(),selectedVo.getRssi2(),selectedVo.getRssi3()))//이상치 제거
            return null;

        if(i <= 10) {
            log.info("i = {}", i);
            if(selectedVo.getRssi1() < 0 && selectedVo.getRssi2() < 0 && selectedVo.getRssi3() < 0) {
                selectedVo = startFilter.initFirstValue(selectedVo, i);
                beforeFilteredVo = selectedVo;
            } else {
                i--;
            }
        }

        if(selectedVo != null) {

            //w = 5, h = 10
            if(triangleNum%2 == 0) {
                ap1 = new Ap(w*(triangleNum-1), h, selectedVo.getDistance1());
                ap2 = new Ap(w*triangleNum, 0, selectedVo.getDistance2());
                ap3 = new Ap(w*(triangleNum+1), h, selectedVo.getDistance3());
            }
            else {
                ap1 = new Ap(w*(triangleNum-1), 0, selectedVo.getDistance1());
                ap2 = new Ap(w*triangleNum, h, selectedVo.getDistance2());
                ap3 = new Ap(w*(triangleNum+1), 0, selectedVo.getDistance3());
            }
            //MAF
            filteredVo = createMAFVo(selectedVo);

            //KF
//            filteredVo = createFilteredVo(originalVo);

            //Original
//            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, originalVo);
            //Temp
//            rssiFilter.setRssiVo(ap1, ap2, ap3,beforeFilteredVo, filteredVo);

            beforeFilteredVo = filteredVo;
//+-            beforeFilteredVo = originalVo;


            //w = 5, h = 10
            if(triangleNum%2 == 0) {
                filteredAp1 = new Ap(w*(triangleNum-1), h, filteredVo.getDistance1());
                filteredAp2 = new Ap(w*triangleNum, 0, filteredVo.getDistance2());
                filteredAp3 = new Ap(w*(triangleNum+1), h, filteredVo.getDistance3());
            }
            else {
                filteredAp1 = new Ap(w*(triangleNum-1), 0, filteredVo.getDistance1());
                filteredAp2 = new Ap(w*triangleNum, h, filteredVo.getDistance2());
                filteredAp3 = new Ap(w*(triangleNum+1), 0, filteredVo.getDistance3());
            }

            Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);

            Trilateration filteredTr = new Trilateration(filteredVo.getDeviceName(), filteredAp1, filteredAp2, filteredAp3);


//        if(!initCheck) {
//            t.start();
//            initCheck = true;
//        }

            UserLocation ul = tr.calcUserLocation();
            UserLocation filteredUl = filteredTr.calcUserLocation();

            //좌표 이상치 제거
            if(rm.rmXYOutlier(filteredUl))
                return null;

            if(i==10){
                ulList.add(0,filteredUl);
            }
            else{
                ulList.set(0,filteredUl);
            }

            UserLocation mAFilteredUl = locMAFilter.push(filteredUl);
            if (mAFilteredUl == null) {
                return null;
            }

            x = locKalmanFilter.predict();
//            UserLocation locFilteredUl = new UserLocation(x[0][0], x[1][0]);

            tempArr = new double[][] {{mAFilteredUl.getX()},
                                       {mAFilteredUl.getY()}};

            x2 = locKalmanFilter.update(tempArr);
            UserLocation updateLocFilteredUl = new UserLocation(x2[0][0], x2[1][0]);

//            UserLocation moveFilteredUl = filteredTr.moveUserLocation(updateLocFilteredUl);

            log.info("originalVo = {}", originalVo.toString());
            log.info("filteredVo = {}", filteredVo.toString());

            System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());
            System.out.printf("Filtered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", filteredUl.getX(), filteredUl.getY(), filteredUl.getDistanceDev());

//            System.out.printf("LocFiltered Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", locFilteredUl.getX(), locFilteredUl.getY(), locFilteredUl.getDistanceDev());

            System.out.printf("MAF Location (Update) : (%.2f, %.2f)  Distance Deviation : %.2fm%n", mAFilteredUl.getX(), mAFilteredUl.getY(), mAFilteredUl.getDistanceDev());
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
//            createCsv(originalVo, ul, filteredVo, filteredUl);
//            return locFilteredUl;
//            return moveFilteredUl;

            return ulList;

        }
        i++;
        return null;
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

    //칼만 필터 VO 생성 함수
//    public VO createFilteredVo(VO originalVo) {
//
//        double filterdRssi1 = kFilterForAp1.kalmanFiltering(originalVo.getRssi1());
//        double filterdRssi2 = kFilterForAp2.kalmanFiltering(originalVo.getRssi2());
//        double filterdRssi3 = kFilterForAp3.kalmanFiltering(originalVo.getRssi3());
//
//        return new VO(originalVo.getDeviceName(),
//                        calcDistance(filterdRssi1),
//                        filterdRssi1,
//                        calcDistance(filterdRssi2),
//                        filterdRssi2,
//                        calcDistance(filterdRssi3),
//                        filterdRssi3
//                        );
//    }

    public double calcDistance(double tempRssi) {

        tempAlpha = -30;
        lossNum = 4;

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

        log.info(map.toString());

        for(int i=0; i<8; i++) {
            if( map.get(i+1) > 0 ) {
                map.put(i+1, errorValue);
            }
        }

        log.info(map.toString());

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

        log.info("key list before sort = {}", keyList.toString());

        if(keyList.size() == 3) {
            Collections.sort(keyList);

            log.info("key list = {}", keyList.toString());


            int n1 = keyList.get(1) - keyList.get(0);
            int n2 = keyList.get(2) - keyList.get(1);

            log.info("n1 = {},   n2 = {}", n1, n2);

            if(n1 == 1 && n2 ==1) {
                return keyList.get(0);
            }
        }
        return 0;
    }


}
