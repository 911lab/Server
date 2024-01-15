package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RSSIFilterOursTestService {
    ExelPOIHelper poiHelper;

    double tempAlpha;
    double lossNum;

    RemoveOutlier rm;
    WeightFilter weightFilter1;
    WeightFilter weightFilter2;
    WeightFilter weightFilter3;
    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;
    KalmanFilter kalmanFilter1;
    KalmanFilter kalmanFilter2;
    KalmanFilter kalmanFilter3;
    KalmanFilter kalmanFilter4;
    KalmanFilter kalmanFilter5;


    ArrayList<Double> array;
    ArrayList<Double> arrayR;
    ArrayList<Double> arrayF;
    ArrayList<Double> arrayM;
    ArrayList<Double> arrayK;
    ArrayList<Double> arrayKF;
    ArrayList<Double> arrayKM;
    ArrayList<Double> arrayFK;
    ArrayList<Double> arrayMK;


    // 1m=-30, n=4 : 15m = -77
    // 1m=-23, n=3.81 : 15m =  -67.8091  outlier20m = -72.5692
    // 1m=-23, n=4.68 : 15m =  -78.0411
    double outlier15m = -78.0411;
    double outlier20m = -72.5692;

    double value;
//    int weightFinishNum;
//    int kalmanFinishNum;
//    int proposedFinishNum;

//    int i=0;
    int totalNum;


    public RSSIFilterOursTestService(){

        poiHelper = new ExelPOIHelper();

        rm = new RemoveOutlier();

        weightFilter1 = new WeightFilter();
        weightFilter2 = new WeightFilter();
        weightFilter3 = new WeightFilter();

        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();

        kalmanFilter1 = new KalmanFilter();
        kalmanFilter2 = new KalmanFilter();
        kalmanFilter3 = new KalmanFilter();
        kalmanFilter4 = new KalmanFilter();
        kalmanFilter5 = new KalmanFilter();

        value = 1.0;

        totalNum = 0;

        //Original
        array = new ArrayList<Double>();
        //Only RM
        arrayR = new ArrayList<Double>();
        //Feedback
        arrayF = new ArrayList<Double>();
        //MAF
        arrayM = new ArrayList<Double>();
        //Kalman
        arrayK = new ArrayList<Double>();
        //Kalman + Feedback
        arrayKF = new ArrayList<Double>();
        //Kalman + MAF
        arrayKM = new ArrayList<Double>();
        //Feedback + Kalman
        arrayFK = new ArrayList<Double>();
        //MAF + Kalman
        arrayMK = new ArrayList<Double>();

        //1.값 읽어오기 -> 2.필터링 -> 3.값 쓰기(new 엑셀파일에)
        //1
        array = readExel();
        //2
        startFilter(array);
    }
    public void startFilter(ArrayList<Double> array) {

        double RSSI;

        for(int j=0; j<1000; j++) {

            //이상치 제거
            if(array.get(j) < outlier15m || array.get(j) > 0){
                RSSI=1;
            }
            else{
                RSSI=array.get(j);
            }

            if(RSSI != 1) {
                arrayR.add(RSSI);
                arrayF.add(createWeight1(RSSI));
                arrayM.add(createMAF1(RSSI));
                arrayK.add(createKalman1(RSSI));
                arrayKF.add(createWeight2(createKalman2(RSSI)));
                arrayKM.add(createMAF2(createKalman3(RSSI)));
                arrayFK.add(createKalman4(createWeight3(RSSI)));
                arrayMK.add(createKalman5(createMAF3(RSSI)));
            } else {
                arrayR.add(1.0);
                arrayF.add(1.0);
                arrayM.add(1.0);
                arrayK.add(1.0);
                arrayKF.add(1.0);
                arrayKM.add(1.0);
                arrayFK.add(1.0);
                arrayMK.add(1.0);
            }
            createCsvRSSIFilterEx(j, array.get(j), arrayR.get(j), arrayF.get(j), arrayM.get(j), arrayK.get(j), arrayKF.get(j), arrayKM.get(j), arrayFK.get(j), arrayMK.get(j));
        }
        try {
            poiHelper.createFileAndRewrite();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Double> readExel() {
        try {
            FileInputStream file = new FileInputStream("C:\\Users\\heejin\\Desktop\\BLE_Test\\RSSI_Filter_1000\\RawData\\15m_NLOS_Ap5.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);

//            int rowindex=0;
            int columnindex=0;

            value=1.0;

            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet=workbook.getSheetAt(0);
            //행의 수
            int rows=sheet.getPhysicalNumberOfRows();

//            System.out.printf("Total Row = {}\n", rows);

            for(int rowindex=0; rowindex<rows; rowindex++){

//                System.out.printf("Now Row = {}\n", rowindex);
                XSSFRow row = sheet.getRow(rowindex);

                if(row !=null) {

                    //셀의 수
//                    int cells = row.getPhysicalNumberOfCells();
//                    System.out.printf("cells = {}", cells);

                    value = 1.0;

                    XSSFCell cell = row.getCell(0);

                    if (cell == null) {
                        array.add(1.0);
                    } else {
                        value = cell.getNumericCellValue();

//                        System.out.printf("value = {}\n", value);

                        array.add(value);
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    private double createWeight1(double rssi) {
        double filterdRssi = weightFilter1.feedBack(rssi);

        return filterdRssi;
    }

    private double createWeight2(double rssi) {
        double filterdRssi = weightFilter2.feedBack(rssi);

        return filterdRssi;
    }
    private double createWeight3(double rssi) {
        double filterdRssi = weightFilter3.feedBack(rssi);

        return filterdRssi;
    }

    private double createMAF1(double rssi) {
        double filterdRssi = mafFilter1.push(rssi);

        return filterdRssi;
    }

    private double createMAF2(double rssi) {
        double filterdRssi = mafFilter2.push(rssi);

        return filterdRssi;
    }

    private double createMAF3(double rssi) {
        double filterdRssi = mafFilter3.push(rssi);

        return filterdRssi;
    }

    private double createKalman1(double rssi) {
        double filterdRssi = kalmanFilter1.kalmanFiltering(rssi);

        return filterdRssi;
    }

    private double createKalman2(double rssi) {
        double filterdRssi = kalmanFilter2.kalmanFiltering(rssi);

        return filterdRssi;
    }

    private double createKalman3(double rssi) {
        double filterdRssi = kalmanFilter3.kalmanFiltering(rssi);

        return filterdRssi;
    }

    private double createKalman4(double rssi) {
        double filterdRssi = kalmanFilter4.kalmanFiltering(rssi);

        return filterdRssi;
    }

    private double createKalman5(double rssi) {
        double filterdRssi = kalmanFilter5.kalmanFiltering(rssi);

        return filterdRssi;
    }

    public void createCsvRSSIFilterEx(int i,
                                      double array,
                                      double arrayR,
                                      double arrayF,
                                      double arrayM,
                                      double arrayK,
                                      double arrayKF,
                                      double arrayKM,
                                      double arrayFK,
                                      double arrayMK) {
        try {
            poiHelper.writeExcelforRSSIFilter(i, array, arrayR, arrayF, arrayM, arrayK, arrayKF, arrayKM, arrayFK, arrayMK);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double calcDistance(double tempRssi) {

        tempAlpha = -23;
        lossNum = 4.68;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }


}
