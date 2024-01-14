package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Slf4j
public class OriginalTestService {

    ExelPOIHelper poiHelper;

    private double tempAlpha;
    private double lossNum;

    VO originalVo;
    SelectedVO selectedVo;

    UserLocation ul;

    @Getter
    int triangleNum;

    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99

    Ap ap1;
    Ap ap2;
    Ap ap3;




    public OriginalTestService() {
        poiHelper = new ExelPOIHelper();
    }


    public UserLocation trilateration(VO vo) {
        originalVo = vo;

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

        log.info("Selected Rssi1 = {}, Rssi2 = {}, Rssi3 = {}", selectedVo.getRssi1(), selectedVo.getRssi2(), selectedVo.getRssi3());

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

            Trilateration tr = new Trilateration(originalVo.getDeviceName(), ap1, ap2, ap3);
            ul = tr.calcUserLocation();

            System.out.printf("Original Location : (%.2f, %.2f) dev : %.2f m \n", ul.getX(), ul.getY(), ul.getDistanceDev());



        } else {
            ul = new UserLocation(999, 999, "ddd");
        }

        createCsvforOrigianl(ul);

        return ul;
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
        lossNum = 4.68;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
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

    public void createCsvforOrigianl(UserLocation ul) {
        try {
            poiHelper.writeExcelforOriginalTest(ul);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
