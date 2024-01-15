package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.ExelPOIHelper;
import com.example.bleLocationSystem.model.KalmanFilter;
import com.example.bleLocationSystem.model.SelectedVO;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class KalmanParameterTestService {

    KalmanFilter kFilterForOur1;
    KalmanFilter kFilterForOur2;
    KalmanFilter kFilterForOur3;
    KalmanFilter kFilterForOur4;
    KalmanFilter kFilterForOur5;
    KalmanFilter kFilterForOur6;
    KalmanFilter kFilterForOur7;
    KalmanFilter kFilterForOur8;
    KalmanFilter kFilterForOur9;
    KalmanFilter kFilterForOur10;
    KalmanFilter kFilterForOur11;
    KalmanFilter kFilterForOur12;
    KalmanFilter kFilterForOur13;
    KalmanFilter kFilterForOur14;
    KalmanFilter kFilterForOur15;

    KalmanFilter kFilterForFusion1;
    KalmanFilter kFilterForFusion2;
    KalmanFilter kFilterForFusion3;
    KalmanFilter kFilterForFusion4;
    KalmanFilter kFilterForFusion5;
    KalmanFilter kFilterForFusion6;
    KalmanFilter kFilterForFusion7;
    KalmanFilter kFilterForFusion8;
    KalmanFilter kFilterForFusion9;
    KalmanFilter kFilterForFusion10;
    KalmanFilter kFilterForFusion11;
    KalmanFilter kFilterForFusion12;
    KalmanFilter kFilterForFusion13;
    KalmanFilter kFilterForFusion14;
    KalmanFilter kFilterForFusion15;

    KalmanFilter kFilterForPerformance1;
    KalmanFilter kFilterForPerformance2;
    KalmanFilter kFilterForPerformance3;
    KalmanFilter kFilterForPerformance4;
    KalmanFilter kFilterForPerformance5;
    KalmanFilter kFilterForPerformance6;
    KalmanFilter kFilterForPerformance7;
    KalmanFilter kFilterForPerformance8;
    KalmanFilter kFilterForPerformance9;
    KalmanFilter kFilterForPerformance10;
    KalmanFilter kFilterForPerformance11;
    KalmanFilter kFilterForPerformance12;
    KalmanFilter kFilterForPerformance13;
    KalmanFilter kFilterForPerformance14;
    KalmanFilter kFilterForPerformance15;

    ArrayList<Double>[] originArrays;

    ArrayList<Double>[] filteredArrays;


    ArrayList<Double>[] ourKalmanArrays;
    ArrayList<Double>[] fusionKalmanArrays;
    ArrayList<Double>[] performanceKalmanArrays;

    double value;



    ExelPOIHelper poiHelper;
    public KalmanParameterTestService() {
        poiHelper = new ExelPOIHelper();
//
//        kFilterForOur1 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur2 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur3 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur4 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur5 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur6 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur7 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur8 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur9 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur10 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur11 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur12 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur13 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur14 = new KalmanFilter(0.005, 20.0);
//        kFilterForOur15 = new KalmanFilter(0.005, 20.0);
//
//        kFilterForFusion1 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion2 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion3 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion4 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion5 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion6 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion7 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion8 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion9 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion10 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion11 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion12 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion13 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion14 = new KalmanFilter(0.005, 1.25);
//        kFilterForFusion15 = new KalmanFilter(0.005, 1.25);
//
//        kFilterForPerformance1 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance2 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance3 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance4 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance5 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance6 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance7 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance8 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance9 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance10 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance11 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance12 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance13 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance14 = new KalmanFilter(0.005, 2.5);
//        kFilterForPerformance15 = new KalmanFilter(0.005, 2.5);

        value = 1.0;

        //originArrays = new ArrayList[15];
//        filteredArrays = new ArrayList[15];

        ourKalmanArrays = new ArrayList[15];
        fusionKalmanArrays = new ArrayList[15];
        performanceKalmanArrays = new ArrayList[15];

        for(int i=0; i<15; i++) {
            ourKalmanArrays[i] = new ArrayList<Double>();
        }

        for(int i=0; i<15; i++) {
            fusionKalmanArrays[i] = new ArrayList<Double>();
        }

        for(int i=0; i<15; i++) {
            performanceKalmanArrays[i] = new ArrayList<Double>();
        }

        //1.값 읽어오기 -> 2.필터링 -> 3.값 쓰기(new 엑셀파일에)
        //1
        originArrays = readExel();
        //2
        startFilter(originArrays);

    }

    public ArrayList<Double>[] readExel() {

        ArrayList<Double>[] arrays = new ArrayList[15];

        for(int i=0; i<15; i++) {
            arrays[i] = new ArrayList<Double>();
        }
        try {
            FileInputStream file = new FileInputStream("C:\\Users\\JaeHyuk\\Desktop\\NLOS_1_15.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);

//            int rowindex=0;
//            int columnindex=0;

            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet=workbook.getSheetAt(0);
            //행의 수
            int rows=sheet.getPhysicalNumberOfRows();

            System.out.printf("Total Row = %d\n", rows);

            for(int rowindex=0; rowindex<rows; rowindex++){

                System.out.printf("Now Row = %d\n", rowindex);
                XSSFRow row = sheet.getRow(rowindex);

                if(row !=null) {

                    //셀의 수
                    int cells = row.getPhysicalNumberOfCells();
//                    System.out.printf("cells = {}", cells);

                    //1m -> 0
                    //15m -> 14

                    for(int cellindex=0; cellindex < cells; cellindex++) {
                        value = 1.0;

                        XSSFCell cell = row.getCell(cellindex);

                        if (cell == null) {
                            arrays[rowindex].add(1.0);
                        } else {
                            value = cell.getNumericCellValue();

                            System.out.printf("value = %f", value);

                            arrays[rowindex].add(value);
                        }
                    }
                }

            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return arrays;
    }

    public void startFilter(ArrayList<Double>[] arrays) {

        for(int i=0; i<15; i++) {
            for(int j=0; j<100; j++) {
                if(arrays[i].get(j) != 1) {
                    ourKalmanArrays[i].add(ourSelect(arrays, i, j));
                    fusionKalmanArrays[i].add(fusionSelect(arrays, i, j));
                    performanceKalmanArrays[i].add(performanceSelect(arrays, i, j));
                } else {
                    ourKalmanArrays[i].add(1.0);
                    fusionKalmanArrays[i].add(1.0);
                    performanceKalmanArrays[i].add(1.0);
                }
            }
            createCsvKalmanParameterEx(i, ourKalmanArrays, fusionKalmanArrays, performanceKalmanArrays);
        }
        try {
            poiHelper.createFileAndRewrite();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createCsvKalmanParameterEx(int i, ArrayList<Double>[] ourKalmanArrays, ArrayList<Double>[] fusionKalmanArrays, ArrayList<Double>[] performanceKalmanArrays) {
        try {
            poiHelper.writeExcelforKalmanParameterEx(i, ourKalmanArrays, fusionKalmanArrays, performanceKalmanArrays);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double ourSelect(ArrayList<Double>[] arrays, int i, int j) {
        double temp = 1.0;

        switch (i) {
            case 0:
                temp = createOurKalmanVo_1m(arrays[i].get(j));
                break;
            case 1:
                temp = createOurKalmanVo_2m(arrays[i].get(j));
                break;
            case 2:
                temp = createOurKalmanVo_3m(arrays[i].get(j));
                break;
            case 3:
                temp = createOurKalmanVo_4m(arrays[i].get(j));
                break;
            case 4:
                temp = createOurKalmanVo_5m(arrays[i].get(j));
                break;
            case 5:
                temp = createOurKalmanVo_6m(arrays[i].get(j));
                break;
            case 6:
                temp = createOurKalmanVo_7m(arrays[i].get(j));
                break;
            case 7:
                temp = createOurKalmanVo_8m(arrays[i].get(j));
                break;
            case 8:
                temp = createOurKalmanVo_9m(arrays[i].get(j));
                break;
            case 9:
                temp = createOurKalmanVo_10m(arrays[i].get(j));
                break;
            case 10:
                temp = createOurKalmanVo_11m(arrays[i].get(j));
                break;
            case 11:
                temp = createOurKalmanVo_12m(arrays[i].get(j));
                break;
            case 12:
                temp = createOurKalmanVo_13m(arrays[i].get(j));
                break;
            case 13:
                temp = createOurKalmanVo_14m(arrays[i].get(j));
                break;
            case 14:
                temp = createOurKalmanVo_15m(arrays[i].get(j));
                break;
        }
        return temp;
    }

    public double fusionSelect(ArrayList<Double>[] arrays, int i, int j) {
        double temp = 1.0;

        switch (i) {
            case 0:
                temp = createFusionKalmanVo_1m(arrays[i].get(j));
                break;
            case 1:
                temp = createFusionKalmanVo_2m(arrays[i].get(j));
                break;
            case 2:
                temp = createFusionKalmanVo_3m(arrays[i].get(j));
                break;
            case 3:
                temp = createFusionKalmanVo_4m(arrays[i].get(j));
                break;
            case 4:
                temp = createFusionKalmanVo_5m(arrays[i].get(j));
                break;
            case 5:
                temp = createFusionKalmanVo_6m(arrays[i].get(j));
                break;
            case 6:
                temp = createFusionKalmanVo_7m(arrays[i].get(j));
                break;
            case 7:
                temp = createFusionKalmanVo_8m(arrays[i].get(j));
                break;
            case 8:
                temp = createFusionKalmanVo_9m(arrays[i].get(j));
                break;
            case 9:
                temp = createFusionKalmanVo_10m(arrays[i].get(j));
                break;
            case 10:
                temp = createFusionKalmanVo_11m(arrays[i].get(j));
                break;
            case 11:
                temp = createFusionKalmanVo_12m(arrays[i].get(j));
                break;
            case 12:
                temp = createFusionKalmanVo_13m(arrays[i].get(j));
                break;
            case 13:
                temp = createFusionKalmanVo_14m(arrays[i].get(j));
                break;
            case 14:
                temp = createFusionKalmanVo_15m(arrays[i].get(j));
                break;
        }
        return temp;
    }

    public double performanceSelect(ArrayList<Double>[] arrays, int i, int j) {
        double temp = 1.0;

        switch (i) {
            case 0:
                temp = createPerformanceKalmanVo_1m(arrays[i].get(j));
                break;
            case 1:
                temp = createPerformanceKalmanVo_2m(arrays[i].get(j));
                break;
            case 2:
                temp = createPerformanceKalmanVo_3m(arrays[i].get(j));
                break;
            case 3:
                temp = createPerformanceKalmanVo_4m(arrays[i].get(j));
                break;
            case 4:
                temp = createPerformanceKalmanVo_5m(arrays[i].get(j));
                break;
            case 5:
                temp = createPerformanceKalmanVo_6m(arrays[i].get(j));
                break;
            case 6:
                temp = createPerformanceKalmanVo_7m(arrays[i].get(j));
                break;
            case 7:
                temp = createPerformanceKalmanVo_8m(arrays[i].get(j));
                break;
            case 8:
                temp = createPerformanceKalmanVo_9m(arrays[i].get(j));
                break;
            case 9:
                temp = createPerformanceKalmanVo_10m(arrays[i].get(j));
                break;
            case 10:
                temp = createPerformanceKalmanVo_11m(arrays[i].get(j));
                break;
            case 11:
                temp = createPerformanceKalmanVo_12m(arrays[i].get(j));
                break;
            case 12:
                temp = createPerformanceKalmanVo_13m(arrays[i].get(j));
                break;
            case 13:
                temp = createPerformanceKalmanVo_14m(arrays[i].get(j));
                break;
            case 14:
                temp = createPerformanceKalmanVo_15m(arrays[i].get(j));
                break;
        }
        return temp;
    }

    public double createOurKalmanVo_1m(double rssi) {

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

    public double createOurKalmanVo_4m(double rssi) {

        double filterdRssi1 = kFilterForOur4.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createOurKalmanVo_5m(double rssi) {

        double filterdRssi1 = kFilterForOur5.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createOurKalmanVo_6m(double rssi) {

        double filterdRssi1 = kFilterForOur6.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createOurKalmanVo_7m(double rssi) {

        double filterdRssi1 = kFilterForOur7.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createOurKalmanVo_8m(double rssi) {

        double filterdRssi1 = kFilterForOur8.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createOurKalmanVo_9m(double rssi) {

        double filterdRssi1 = kFilterForOur9.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createOurKalmanVo_10m(double rssi) {

        double filterdRssi1 = kFilterForOur10.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createOurKalmanVo_11m(double rssi) {

        double filterdRssi1 = kFilterForOur11.kalmanFiltering(rssi);

        return filterdRssi1;
    }



    public double createOurKalmanVo_12m(double rssi) {

        double filterdRssi1 = kFilterForOur12.kalmanFiltering(rssi);

        return filterdRssi1;
    }



    public double createOurKalmanVo_13m(double rssi) {

        double filterdRssi1 = kFilterForOur13.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createOurKalmanVo_14m(double rssi) {

        double filterdRssi1 = kFilterForOur14.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createOurKalmanVo_15m(double rssi) {

        double filterdRssi1 = kFilterForOur15.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createFusionKalmanVo_1m(double rssi) {

        double filterdRssi1 = kFilterForFusion1.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_2m(double rssi) {

        double filterdRssi1 = kFilterForFusion2.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_3m(double rssi) {

        double filterdRssi1 = kFilterForFusion3.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_4m(double rssi) {

        double filterdRssi1 = kFilterForFusion4.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_5m(double rssi) {

        double filterdRssi1 = kFilterForFusion5.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_6m(double rssi) {

        double filterdRssi1 = kFilterForFusion6.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_7m(double rssi) {

        double filterdRssi1 = kFilterForFusion7.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_8m(double rssi) {

        double filterdRssi1 = kFilterForFusion8.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_9m(double rssi) {

        double filterdRssi1 = kFilterForFusion9.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_10m(double rssi) {

        double filterdRssi1 = kFilterForFusion10.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_11m(double rssi) {

        double filterdRssi1 = kFilterForFusion11.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_12m(double rssi) {

        double filterdRssi1 = kFilterForFusion12.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_13m(double rssi) {

        double filterdRssi1 = kFilterForFusion13.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_14m(double rssi) {

        double filterdRssi1 = kFilterForFusion14.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createFusionKalmanVo_15m(double rssi) {

        double filterdRssi1 = kFilterForFusion15.kalmanFiltering(rssi);

        return filterdRssi1;
    }


    public double createPerformanceKalmanVo_1m(double rssi) {

        double filterdRssi1 = kFilterForPerformance1.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_2m(double rssi) {

        double filterdRssi1 = kFilterForPerformance2.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_3m(double rssi) {

        double filterdRssi1 = kFilterForPerformance3.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_4m(double rssi) {

        double filterdRssi1 = kFilterForPerformance4.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_5m(double rssi) {

        double filterdRssi1 = kFilterForPerformance5.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_6m(double rssi) {

        double filterdRssi1 = kFilterForPerformance6.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_7m(double rssi) {

        double filterdRssi1 = kFilterForPerformance7.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_8m(double rssi) {

        double filterdRssi1 = kFilterForPerformance8.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_9m(double rssi) {

        double filterdRssi1 = kFilterForPerformance9.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_10m(double rssi) {

        double filterdRssi1 = kFilterForPerformance10.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_11m(double rssi) {

        double filterdRssi1 = kFilterForPerformance11.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_12m(double rssi) {

        double filterdRssi1 = kFilterForPerformance12.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_13m(double rssi) {

        double filterdRssi1 = kFilterForPerformance13.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_14m(double rssi) {

        double filterdRssi1 = kFilterForPerformance14.kalmanFiltering(rssi);

        return filterdRssi1;
    }

    public double createPerformanceKalmanVo_15m(double rssi) {

        double filterdRssi1 = kFilterForPerformance15.kalmanFiltering(rssi);

        return filterdRssi1;
    }






}
