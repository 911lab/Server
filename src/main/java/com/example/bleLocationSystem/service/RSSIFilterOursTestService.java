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
    ArrayList<Double> array;
    KalmanFilter kFilterForOur1;
    KalmanFilter kFilterForOur2;
    KalmanFilter kFilterForOur3;
    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;
    RemoveOutlier rm;
    double outlier = -83.0;

    double value;
    int weightFinishNum;
    int kalmanFinishNum;
    int proposedFinishNum;

    int totalNum;

    int finishedCount;

    ExelPOIHelper poiHelper;
    public RSSIFilterOursTestService(){

        poiHelper = new ExelPOIHelper();
        kFilterForOur1 = new KalmanFilter(0.005, 2.5);
        kFilterForOur2 = new KalmanFilter(0.005, 2.5);
        kFilterForOur3 = new KalmanFilter(0.005, 2.5);
        mafFilter1 = new MAFilter();
        mafFilter2 = new MAFilter();
        mafFilter3 = new MAFilter();
        value = 1.0;
        rm = new RemoveOutlier();
        finishedCount = 0;

        rm = new RemoveOutlier();

        totalNum = 0;

        array = new ArrayList<Double>();

        //1.값 읽어오기 -> 2.필터링 -> 3.값 쓰기(new 엑셀파일에)
        //1
        array = readExel();
        //2
        startFilter(array);
    }
    public void startFilter(ArrayList<Double> arrays) {

//        for(int i=0; i<15; i++) {
        double RSSI;
        for(int j=0; j<100; j++) {
            if(arrays.get(j) < outlier || arrays.get(j) > 0){
                RSSI=1;
            }
            else{
                RSSI=arrays.get(j)
            }
            if(RSSI != 1) {
                    RnMArrays[i].add(createOurKalmanVo_1m(arrays.get(j)));
                    RnKArrays[i].add(fusionSelect(arrays, i, j));
                    RnMnKArrays[i].add(performanceSelect(arrays, i, j));
                    RnKnMArrays[i].add(performanceSelect(arrays, i, j));
                } else {
                    RnMArrays[i].add(1.0);
                    RnKArrays[i].add(1.0);
                    RnMnKArrays[i].add(1.0);
                    RnKnMArrays[i].add(1.0);
                }
            }
            createCsvRSSIFilterOursEx(i, RnMArrays, RnKArrays, RnMnKArrays, RnKnMArrays);
//        }
        try {
            poiHelper.createFileAndRewrite();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void createCsvRSSIFilterOursEx(int i, ArrayList<Double>[] RnMArrays, ArrayList<Double>[] RnKArrays, ArrayList<Double>[] RnMnKArrays, ArrayList<Double>[] RnKnMArrays) {
        try {
            poiHelper.writeExcelforRSSIFilterOursEx(i, RnMArrays, RnKArrays, RnMnKArrays, RnKnMArrays);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public double createRnM(double rssi) {

        double filterdRssi1 = kFilterForOur1.kalmanFiltering(rssi);


        return filterdRssi1;
    }

    public double createOurKalmanVo_2m(double rssi) {

        double filterdRssi1 = kFilterForOur2.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createOurKalmanVo_3m(double rssi) {

        double filterdRssi1 = kFilterForOur3.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public ArrayList<Double> readExel() {
        try {
            FileInputStream file = new FileInputStream("C:\\Users\\JaeHyuk\\Desktop\\3_5_10_15_org.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);

//            int rowindex=0;
            int columnindex=0;

            value=1.0;

            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet=workbook.getSheetAt(0);
            //행의 수
            int rows=sheet.getPhysicalNumberOfRows();

            System.out.printf("Total Row = {}", rows);

            for(int rowindex=0; rowindex<rows; rowindex++){

                System.out.printf("Now Row = {}", rowindex);
                XSSFRow row = sheet.getRow(rowindex);

                if(row !=null) {

                    //셀의 수
                    int cells = row.getPhysicalNumberOfCells();
//                    System.out.printf("cells = {}", cells);

                    value = 1.0;

                    //3m -> 0
                    //5m -> 1
                    //10m -> 2
                    //15m -> 3
                    //10w -> 4
                    //15w -> 5
                    XSSFCell cell = row.getCell(5);

                    if (cell == null) {
                        array.add(1.0);
                    } else {
                        value = cell.getNumericCellValue();

                        System.out.printf("value = {}", value);

                        array.add(value);
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return array;
    }
}
