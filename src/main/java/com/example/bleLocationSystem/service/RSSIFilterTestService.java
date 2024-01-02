package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

@Slf4j
public class RSSIFilterTestService {

    VO originalVo;
    SelectedVO filteredVo;

    VO originalVoforNotProximity;

    SelectedVO selectedVo;

    SelectedVO selectedVoforWeightAndKalman;
    SelectedVO selectedVoforNotProximity;

    SelectedVO weightVo;
    SelectedVO kalmanVo;

    SelectedVO kfilteredVo;

    SelectedVO realOriginalVo;
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
    private float tempAlpha;
    private int lossNum;

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
    double outlier = -83.0;
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

    public  RSSIFilterTestService() {
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

        rm = new RemoveOutlier();

        ulList = new ArrayList<UserLocation>();

        finishedCount = 0;

        weightFinishNum = 0;
        kalmanFinishNum = 0;
        proposedFinishNum = 0;
        proposedwithoutProximityFinishNum = 0;

        totalNum = 0;
    }

    public VO trilateration(VO vo) {
        originalVo = vo;
        realOriginalVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi1(), originalVo.getRssi1());

        totalNum++;
        log.info("--------------- total Num = {} ---------------", totalNum);

        //--------------------------------------------------------------Previous Research Method--------------------------------------------------------------
        //------------------------------가중치 기법------------------------------
        weightVo = createWeightVo(originalVo);



        //------------------------------칼만 필터 단일 기법------------------------------
        kalmanVo = createFilteredVo2(originalVo);


        //--------------------------------------------------------------Proposed RSSI Filtering Method--------------------------------------------------------------

        // RSSI 이상치 제거
        if( originalVo.getRssi1() < outlier && originalVo.getRssi1() > 0 ) {
            selectedVo = createSelectVO(originalVo.getDeviceName(), 1, 1, 1);
        }
        else {
            selectedVo = createSelectVO(originalVo.getDeviceName(), originalVo.getRssi1(), originalVo.getRssi1(), originalVo.getRssi1());
        }

        //MAF
        filteredVo = createMAFVo(selectedVo);

        //Kalman
        kfilteredVo = createFilteredVo(filteredVo);


        log.info("Original Rssi1 = {}  dis1 = {}", realOriginalVo.getRssi1(), realOriginalVo.getDistance1());
        log.info("Weight Rssi1 = {}  dis2 = {}", weightVo.getRssi1(), weightVo.getDistance1());
        log.info("Kalman Rssi = {}  dis3 = {}", kalmanVo.getRssi1(), kalmanVo.getDistance1());
        log.info("Proposed Rssi = {}  dis4 = {}", kfilteredVo.getRssi1(), kfilteredVo.getDistance1());

        createCsvEx1(realOriginalVo, weightVo, kalmanVo, kfilteredVo, totalNum);

        log.info("----------------------------------------------");
        return originalVo;
    }

    private SelectedVO createWeightVo(VO originalVo) {
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

    public SelectedVO createFilteredVo2(VO originalVo) {

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

        tempAlpha = -30;
        lossNum = 4;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }


    //실험 1 엑셀 파일 만들기
    public void createCsvEx1(SelectedVO realOriginalVo, SelectedVO weightVo, SelectedVO kalmanVo, SelectedVO proposedVo, int totalNum) {
        try {
            poiHelper.writeExcelforEx1(realOriginalVo, weightVo, kalmanVo, proposedVo, totalNum);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
