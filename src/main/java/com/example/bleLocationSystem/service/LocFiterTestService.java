package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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

    private double tempAlpha;
    private double lossNum;


    VO originalVo;
    VO originalVoforNotProximity;

    SelectedVO selectedVoforKalman;
    SelectedVO kalmanVo;


    private Ap kalmanAp1;
    private Ap kalmanAp2;
    private Ap kalmanAp3;


    RemoveOutlier rm;
    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;


    UserLocation kalmanUl;
    ArrayList<UserLocation> ulList;

    // 1m=-30, n=4 : 15m = -77
    // 1m=-23, n=3.81 : 15m =  -67.8091
    double outlier = -68.8091;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99


    int kalmanFinishNum;
    int proposedFinishNum;
    int totalNum;

    public LocFiterTestService() {
        poiHelper = new ExelPOIHelper();

        rm = new RemoveOutlier();
        kFilterForAp1 = new KalmanFilter();
        kFilterForAp2 = new KalmanFilter();
        kFilterForAp3 = new KalmanFilter();

        kFilterForAp4 = new KalmanFilter();
        kFilterForAp5 = new KalmanFilter();
        kFilterForAp6 = new KalmanFilter();

        ulList = new ArrayList<UserLocation>();

        totalNum = 0;
    }

    public ArrayList<UserLocation> trilateration(VO vo) {
        originalVo = vo;
        originalVoforNotProximity = vo;

        totalNum++;

        //--------------------------------------------------------------Kalman Method--------------------------------------------------------------
        triangleNumforKalman = selectTriangle(originalVo);

        switch (triangleNumforKalman) {
            case 0:
                selectedVoforKalman = null;
                break;
            case 1:
                selectedVoforKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi2(), originalVo.getRssi3());
                break;
            case 2:
                selectedVoforKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi2(), originalVo.getRssi3(), originalVo.getRssi4());
                break;
            case 3:
                selectedVoforKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi3(), originalVo.getRssi4(), originalVo.getRssi4());
                break;
            case 4:
                selectedVoforKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi4(), originalVo.getRssi5(), originalVo.getRssi6());
                break;
            case 5:
                selectedVoforKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi5(), originalVo.getRssi6(), originalVo.getRssi7());
                break;
            case 6:
                selectedVoforKalman = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi6(), originalVo.getRssi7(), originalVo.getRssi8());
                break;
        }

        if(selectedVoforKalman != null) {
            kalmanVo = createFilteredVo2(selectedVoforKalman);
            log.info("Kalman Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", kalmanVo.getRssi1(), kalmanVo.getRssi2(), kalmanVo.getRssi3());

            if (triangleNumforKalman % 2 == 0) {
                kalmanAp1 = new Ap((w / 2.0) * (triangleNumforKalman - 1), h, kalmanVo.getDistance1());
                kalmanAp2 = new Ap((w / 2.0) * triangleNumforKalman, 0, kalmanVo.getDistance2());
                kalmanAp3 = new Ap((w / 2.0) * (triangleNumforKalman + 1), h, kalmanVo.getDistance3());
            } else {
                kalmanAp1 = new Ap((w / 2.0) * (triangleNumforKalman - 1), 0, kalmanVo.getDistance1());
                kalmanAp2 = new Ap((w / 2.0) * triangleNumforKalman, h, kalmanVo.getDistance2());
                kalmanAp3 = new Ap((w / 2.0) * (triangleNumforKalman + 1), 0, kalmanVo.getDistance3());
            }
            Trilateration kalmanTr = new Trilateration(kalmanVo.getDeviceName(), kalmanAp1, kalmanAp2, kalmanAp3);
            kalmanUl = kalmanTr.calcUserLocation();
            kalmanFinishNum++;
            System.out.printf("Kalman Finish Num : %d\n", kalmanFinishNum);
        } else {
            kalmanUl = new UserLocation(999, 999, "ddd");
        }

        return ulList;
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

    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 3.81;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
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