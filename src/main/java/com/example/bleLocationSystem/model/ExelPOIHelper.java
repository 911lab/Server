package com.example.bleLocationSystem.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Slf4j
public class ExelPOIHelper {

    //엑셀 파일
    Workbook workbook;

    //엑셀 시트
    Sheet sheet;
    Sheet testSheet;
    Sheet oneBeaconSheet;
    Sheet Ex2Sheet;

    //헤더 로우
    Row header;
    Row testHeader;
    Row oneBeaconHeader;
    Row Ex2Header;

    //헤더 스타일
    CellStyle headerStyle;
    XSSFFont font;

    //셀 스타일
    CellStyle style;

    String strOrgloc;
    String strFilterdloc;

    int num;
    int nowNum;

    ArrayList<Row> rowArray= new ArrayList<Row>();




    public ExelPOIHelper() {
        workbook = new XSSFWorkbook();
        styleSetting();

        //실제 동작 시
//        ResultSetting();

        //테스트 시
//        testSetting();

        //한개 비콘 필터링 테스트시
//        oneBeaconSetting();

        //비콘 필터링 테스트 (7개 필터링 동시에)
//        rssiFilterSetting();

        //세개 비콘 정지상태 필터링 테스트시
//        threeBeaconStopSetting();

        //실험 2 셋팅
//        forEx2Setting();
//        nowNum = 0;

        //실험 1 셋팅
//        forEx1Setting();

        //칼만 파라미터 실험 셋팅
//        forKalmanParameterExSetting();
//        rowArray = new ArrayList<Row>();
//        //data row 생성
//        for(int j=0; j<100; j++) {
//            rowArray.add(Ex2Sheet.createRow(j));
//        }

        //칼만 파라미터2 실험 셋팅
//        forKalmanParameter2ExSetting();
//        rowArray =
//        //data row 생성
//        for(int j=0; j<1001; j++) {
//            rowArray.add(Ex2Sheet.createRow(j));
//        }

        //RSSI Filter Test 셋팅
//        forRSSIFilterSetting();

        //Loc Filter Test 셋팅
//        forLocFilterSetting();
//        nowNum = 0;

        //Location Filter Test 셋팅
        forLocationFilterTestSetting();
        nowNum = 0;


        //Original Test 셋팅
//        forOriginalTestSetting();
//        nowNum=0;
    }



    public void styleSetting() {
        //칼럼 셀 스타일(헤더 : 칼럼)
        headerStyle = workbook.createCellStyle();

        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        font = ((XSSFWorkbook) workbook).createFont();

        //셀 스타일(데이터 : 로우)
        // Next, let's write the content of the table with a different style:
        style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);            // 가로 중앙 정렬
        style.setWrapText(true);
    }

    public void ResultSetting() {
        sheet = workbook.createSheet("Beacon");

        sheet.setColumnWidth(0, 10000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 10000);
        sheet.setColumnWidth(4, 10000);
        sheet.setColumnWidth(5, 10000);
        sheet.setColumnWidth(6, 10000);
        sheet.setColumnWidth(7, 10000);
        sheet.setColumnWidth(8, 10000);
        sheet.setColumnWidth(9, 10000);


        header = sheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Before Filtering Ap1 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("After Filtering Ap1 Rssi");
        headerCell.setCellStyle(headerStyle);



        headerCell = header.createCell(2);
        headerCell.setCellValue("Before Filtering Ap2 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("After Filtering Ap2 Rssi");
        headerCell.setCellStyle(headerStyle);



        headerCell = header.createCell(4);
        headerCell.setCellValue("Before Filtering Ap3 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("After Filtering Ap3 Rssi");
        headerCell.setCellStyle(headerStyle);


        headerCell = header.createCell(6);
        headerCell.setCellValue("before filtered location");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue("After filtered location");
        headerCell.setCellStyle(headerStyle);



        headerCell = header.createCell(8);
        headerCell.setCellValue("Before Filtering distance Deviation");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(9);
        headerCell.setCellValue("After Filtering distance Deviation");
        headerCell.setCellStyle(headerStyle);
    }

    public void writeExcel(VO originalVo, UserLocation originalUl, SelectedVO filteredVo, UserLocation filteredUl, int i) throws IOException  {

        num = i;

        log.info("row i : {}", num);

        //data row 생성
        Row row = sheet.createRow(num);


        //셀 추가
        //0 ap1
        Cell cell = row.createCell(0);
        cell.setCellValue(originalVo.getRssi1());
        cell.setCellStyle(style);

        //1 ap1 filter
        cell = row.createCell(1);
        cell.setCellValue(filteredVo.getRssi1());
        cell.setCellStyle(style);

        //2 ap2
        cell = row.createCell(2);
        cell.setCellValue(originalVo.getRssi2());
        cell.setCellStyle(style);

        //3 ap2 filter
        cell = row.createCell(3);
        cell.setCellValue(filteredVo.getRssi2());
        cell.setCellStyle(style);

        //4 ap3
        cell = row.createCell(4);
        cell.setCellValue(originalVo.getRssi3());
        cell.setCellStyle(style);

        //5 ap3 filter
        cell = row.createCell(5);
        cell.setCellValue(filteredVo.getRssi3());
        cell.setCellStyle(style);

        strOrgloc = String.format("( %.2f, %.2f )", originalUl.getX(), originalUl.getY());
        strFilterdloc = String.format("( %.2f, %.2f )", filteredUl.getX(), filteredUl.getY());

        //6 before filtered location
        cell = row.createCell(6);
        cell.setCellValue(strOrgloc);
        cell.setCellStyle(style);

        //7 after filtered location
        cell = row.createCell(7);
        cell.setCellValue(strFilterdloc);
        cell.setCellStyle(style);

        //8 distance dev
        cell = row.createCell(8);
        cell.setCellValue(originalUl.getDistanceDev());
        cell.setCellStyle(style);

        //9 distance dev filter
        cell = row.createCell(9);
        cell.setCellValue(filteredUl.getDistanceDev());
        cell.setCellStyle(style);

        if(num%300 == 0)  {
            createFileAndRewrite();
        }
    }




    //8개 짜리 정지상태 엑셀 파일 만들기
    public void testSetting() {
        testSheet = workbook.createSheet("BeaconTest");

        testSheet.setColumnWidth(0, 10000);
        testSheet.setColumnWidth(1, 10000);
        testSheet.setColumnWidth(2, 10000);
        testSheet.setColumnWidth(3, 10000);
        testSheet.setColumnWidth(4, 10000);
        testSheet.setColumnWidth(5, 10000);
        testSheet.setColumnWidth(6, 10000);
        testSheet.setColumnWidth(7, 10000);

        testHeader = testSheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = testHeader.createCell(0);
        headerCell.setCellValue("Ap1 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(1);
        headerCell.setCellValue("Ap2 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(2);
        headerCell.setCellValue("Ap3 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(3);
        headerCell.setCellValue("Ap4 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(4);
        headerCell.setCellValue("Ap5 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(5);
        headerCell.setCellValue("Ap6 Rssi");
        headerCell.setCellStyle(headerStyle);


        headerCell = testHeader.createCell(6);
        headerCell.setCellValue("Ap7 Rssi");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(7);
        headerCell.setCellValue("Ap8 Rssi");
        headerCell.setCellStyle(headerStyle);
    }







    //8개 짜리 정지상태 엑셀 파일 만들기
    public void writeTestExcel(VO originalVo, int i) throws IOException  {

        num = i;

        log.info("row i : {}", num);

        //data row 생성
        Row row = testSheet.createRow(num);


        //셀 추가
        //ap1
        Cell cell = row.createCell(0);
        cell.setCellValue(originalVo.getRssi1());
        cell.setCellStyle(style);

        //ap2
        cell = row.createCell(1);
        cell.setCellValue(originalVo.getRssi2());
        cell.setCellStyle(style);

        //ap3
        cell = row.createCell(2);
        cell.setCellValue(originalVo.getRssi3());
        cell.setCellStyle(style);

        //ap4
        cell = row.createCell(3);
        cell.setCellValue(originalVo.getRssi4());
        cell.setCellStyle(style);

        //ap5
        cell = row.createCell(4);
        cell.setCellValue(originalVo.getRssi5());
        cell.setCellStyle(style);

        //ap6
        cell = row.createCell(5);
        cell.setCellValue(originalVo.getRssi6());
        cell.setCellStyle(style);

        //ap7
        cell = row.createCell(6);
        cell.setCellValue(originalVo.getRssi7());
        cell.setCellStyle(style);

        //ap8
        cell = row.createCell(7);
        cell.setCellValue(originalVo.getRssi8());
        cell.setCellStyle(style);

        if(num%1000 == 0)  {
            createFileAndRewrite();
        }
    }

    //단일 비콘 필터링 테스트
    public void oneBeaconSetting() {
        oneBeaconSheet = workbook.createSheet("Beacon");


        oneBeaconSheet.setColumnWidth(0, 10000);
        oneBeaconSheet.setColumnWidth(1, 10000);



        oneBeaconHeader = oneBeaconSheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = oneBeaconHeader.createCell(0);
        headerCell.setCellValue("MAF Filtered Ul abs Error Distance");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(1);
        headerCell.setCellValue("RO MAF Filtered Ul abs Error Distance");
        headerCell.setCellStyle(headerStyle);




    }

    //단일 비콘 필터링 테스트
    public void wrieteOneBeaconTestExcel(UserLocation mafUl, UserLocation roMafUl, int i) throws IOException  {

        num = i;

        log.info("row i : {}", num);

        //data row 생성
        Row row = oneBeaconSheet.createRow(num);


        //셀 추가
        //ap1
        Cell cell = row.createCell(0);
        cell.setCellValue(mafUl.getDistanceDev());
        cell.setCellStyle(style);

        //ap2
        cell = row.createCell(1);
        cell.setCellValue(roMafUl.getDistanceDev());
        cell.setCellStyle(style);




        if(num%5000 == 0)  {
            log.info("realLocX = {}", mafUl.getRealLocX());
            log.info("realLocY = {}", mafUl.getRealLocY());
            createFileAndRewrite();
        }
    }

    //비콘 필터링 테스트 (7개 필터링 동시에)
    public void rssiFilterSetting() {
        oneBeaconSheet = workbook.createSheet("Beacon");


        oneBeaconSheet.setColumnWidth(0, 10000);
        oneBeaconSheet.setColumnWidth(1, 10000);
        oneBeaconSheet.setColumnWidth(2, 10000);
        oneBeaconSheet.setColumnWidth(3, 10000);
        oneBeaconSheet.setColumnWidth(4, 10000);
        oneBeaconSheet.setColumnWidth(5, 10000);
        oneBeaconSheet.setColumnWidth(6, 10000);
        oneBeaconSheet.setColumnWidth(7, 10000);
        oneBeaconSheet.setColumnWidth(8, 10000);
        oneBeaconSheet.setColumnWidth(9, 10000);
        oneBeaconSheet.setColumnWidth(10, 10000);
        oneBeaconSheet.setColumnWidth(11, 10000);
        oneBeaconSheet.setColumnWidth(12, 10000);
        oneBeaconSheet.setColumnWidth(13, 10000);

        oneBeaconHeader = oneBeaconSheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = oneBeaconHeader.createCell(0);
        headerCell.setCellValue("10m original");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(1);
        headerCell.setCellValue("10m RO");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(2);
        headerCell.setCellValue("10m Kalman");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(3);
        headerCell.setCellValue("10m RO Kalman");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(4);
        headerCell.setCellValue("10m MAF");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(5);
        headerCell.setCellValue("10m RO MAF");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(6);
        headerCell.setCellValue("10m RO MAF Kalman");
        headerCell.setCellStyle(headerStyle);



        headerCell = oneBeaconHeader.createCell(7);
        headerCell.setCellValue("15m original");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(8);
        headerCell.setCellValue("15m RO");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(9);
        headerCell.setCellValue("15m Kalman");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(10);
        headerCell.setCellValue("15m RO Kalman");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(11);
        headerCell.setCellValue("15m MAF");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(12);
        headerCell.setCellValue("15m RO MAF");
        headerCell.setCellStyle(headerStyle);

        headerCell = oneBeaconHeader.createCell(13);
        headerCell.setCellValue("15m RO MAF Kalman");
        headerCell.setCellStyle(headerStyle);

    }

    //비콘 필터링 테스트 (7개 필터링 동시에)
    public void wrieteRssiFilterTestExcel(ArrayList<Double> original10m, ArrayList<Double> original15m,
                                          ArrayList<Double> ro10m, ArrayList<Double> ro15m,
                                          ArrayList<Double> kalman10m, ArrayList<Double> kalman15m,
                                          ArrayList<Double> roKalman10m, ArrayList<Double> roKalman15m,
                                          ArrayList<Double> maf10m, ArrayList<Double> maf15m,
                                          ArrayList<Double> roMaf10m, ArrayList<Double> roMaf15m,
                                          ArrayList<Double> roMafKalman10m, ArrayList<Double> roMafKalman15m) throws IOException  {

        for(int i=0; i<original10m.size(); i++) {
            //data row 생성
            Row row = oneBeaconSheet.createRow(i+1);


            //셀 추가
            //10m Original
            Cell cell = row.createCell(0);
            cell.setCellValue(original10m.get(i));
            cell.setCellStyle(style);

            //10m RO
            cell = row.createCell(1);
            cell.setCellValue(ro10m.get(i));
            cell.setCellStyle(style);

            //10m Kalman
            cell = row.createCell(2);
            cell.setCellValue(kalman10m.get(i));
            cell.setCellStyle(style);

            //10m RO Kalman
            cell = row.createCell(3);
            cell.setCellValue(roKalman10m.get(i));
            cell.setCellStyle(style);

            //10m MAF
            cell = row.createCell(4);
            cell.setCellValue(maf10m.get(i));
            cell.setCellStyle(style);

            //10m RO MAF
            cell = row.createCell(5);
            cell.setCellValue(roMaf10m.get(i));
            cell.setCellStyle(style);

            //10m RO MAF Kalman
            cell = row.createCell(6);
            cell.setCellValue(roMafKalman10m.get(i));
            cell.setCellStyle(style);





            //15m Original
            cell = row.createCell(7);
            cell.setCellValue(original15m.get(i));
            cell.setCellStyle(style);

            //15m RO
            cell = row.createCell(8);
            cell.setCellValue(ro15m.get(i));
            cell.setCellStyle(style);

            //15m Kalman
            cell = row.createCell(9);
            cell.setCellValue(kalman15m.get(i));
            cell.setCellStyle(style);

            //15m RO Kalman
            cell = row.createCell(10);
            cell.setCellValue(roKalman15m.get(i));
            cell.setCellStyle(style);

            //15m MAF
            cell = row.createCell(11);
            cell.setCellValue(maf15m.get(i));
            cell.setCellStyle(style);

            //15m RO MAF
            cell = row.createCell(12);
            cell.setCellValue(roMaf15m.get(i));
            cell.setCellStyle(style);

            //15m RO MAF Kalman
            cell = row.createCell(13);
            cell.setCellValue(roMafKalman15m.get(i));
            cell.setCellStyle(style);
        }

        createFileAndRewrite();
    }

    public void createFileAndRewrite() throws IOException {
//        File currDir = new File(".");
//        String path = currDir.getAbsolutePath();
//        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        //For Loc Filter Test
//        String fileLocation = "C:\\Users\\heehe\\Desktop\\ble_loc_test_exel\\locationTest__1_18\\loc_20.xlsx";

        //For Threshold Test
        String fileLocation = "C:\\Users\\heehe\\Desktop\\ble_loc_test_exel\\thresholdTest_1_18\\thres_20.xlsx";



        //JH
//        String fileLocation = "C:\\Users\\heejin\\Desktop\\ble_loc_test_exel\\1.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
//        System.out.println("exel write finish !!!");
        workbook.close();
        workbook = new XSSFWorkbook();
//        styleSetting();
//        ResultSetting();
    }


    //5개 짜리 Three Beacon Stop Test 엑셀 파일 만들기
    public void threeBeaconStopSetting() {
        testSheet = workbook.createSheet("BeaconTest");

        testSheet.setColumnWidth(0, 10000);
        testSheet.setColumnWidth(1, 10000);
        testSheet.setColumnWidth(2, 10000);
        testSheet.setColumnWidth(3, 10000);
        testSheet.setColumnWidth(4, 10000);
        testSheet.setColumnWidth(5, 10000);
        testSheet.setColumnWidth(6, 10000);
        testSheet.setColumnWidth(7, 10000);
        testSheet.setColumnWidth(8, 10000);
        testSheet.setColumnWidth(9, 10000);

        testHeader = testSheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = testHeader.createCell(0);
        headerCell.setCellValue("Original (x,y)");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(1);
        headerCell.setCellValue("Original Ul Dis Dev");
        headerCell.setCellStyle(headerStyle);



        headerCell = testHeader.createCell(2);
        headerCell.setCellValue("Rssi Filtered (x,y)");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(3);
        headerCell.setCellValue("Rssi Filtered Ul Dis Dev");
        headerCell.setCellStyle(headerStyle);



        headerCell = testHeader.createCell(4);
        headerCell.setCellValue("Loc RO (x,y)");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(5);
        headerCell.setCellValue("Loc RO Ul Dis Dev");
        headerCell.setCellStyle(headerStyle);



        headerCell = testHeader.createCell(6);
        headerCell.setCellValue("Loc MAF (x,y)");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(7);
        headerCell.setCellValue("Loc MAF Ul Dis Dev");
        headerCell.setCellStyle(headerStyle);



        headerCell = testHeader.createCell(8);
        headerCell.setCellValue("Loc RO + Loc MAF (x,y)");
        headerCell.setCellStyle(headerStyle);

        headerCell = testHeader.createCell(9);
        headerCell.setCellValue("Loc RO + Loc MAF Ul Dis Dev");
        headerCell.setCellStyle(headerStyle);

    }







    //8개 짜리 정지상태 엑셀 파일 만들기
    public void writeThreeBeaconStopExcel(UserLocation ul, UserLocation filteredUl, UserLocation roUl, UserLocation mafUl, UserLocation roMafUl, int i) throws IOException  {

        num = i;

        log.info("row i : {}", num);

        //data row 생성
        Row row = testSheet.createRow(num);


        //셀 추가
        //Original Ul
        Cell cell = row.createCell(0);
        String original_xy = String.format("(%.2f, %.2f)", ul.getX(), ul.getY());
        cell.setCellValue(original_xy);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(ul.getDistanceDev());
        cell.setCellStyle(style);

        //Rssi Filtered Ul
        cell = row.createCell(2);
        String filtered_xy = String.format("(%.2f, %.2f)", filteredUl.getX(), filteredUl.getY());
        cell.setCellValue(filtered_xy);
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue(filteredUl.getDistanceDev());
        cell.setCellStyle(style);

        //Loc RO Ul
        cell = row.createCell(4);
        String ro_xy = String.format("(%.2f, %.2f)", roUl.getX(), roUl.getY());
        cell.setCellValue(ro_xy);
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue(roUl.getDistanceDev());
        cell.setCellStyle(style);

        //Loc MAF Ul
        cell = row.createCell(6);
        String maf_xy = String.format("(%.2f, %.2f)", mafUl.getX(), mafUl.getY());
        cell.setCellValue(maf_xy);
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue(mafUl.getDistanceDev());
        cell.setCellStyle(style);

        //Loc RO + Loc MAF Ul
        cell = row.createCell(8);
        String romaf_xy = String.format("(%.2f, %.2f)", roMafUl.getX(), roMafUl.getY());
        cell.setCellValue(romaf_xy);
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue(roMafUl.getDistanceDev());
        cell.setCellStyle(style);

        if(num%6000 == 0)  {
            createFileAndRewrite();
        }
    }



    //-----------------------------------------------------------for Ex2-----------------------------------------------------------
    public void forEx2Setting() {
        Ex2Sheet = workbook.createSheet("Ex2");


        Ex2Sheet.setColumnWidth(0, 10000);
        Ex2Sheet.setColumnWidth(1, 10000);
        Ex2Sheet.setColumnWidth(2, 10000);
        Ex2Sheet.setColumnWidth(3, 10000);
        Ex2Sheet.setColumnWidth(4, 10000);
        Ex2Sheet.setColumnWidth(5, 10000);
        Ex2Sheet.setColumnWidth(6, 10000);
        Ex2Sheet.setColumnWidth(7, 10000);

        Ex2Header = Ex2Sheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = Ex2Header.createCell(0);
        headerCell.setCellValue("Weight Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(1);
        headerCell.setCellValue("Weight Ul Distance MAE");
        headerCell.setCellStyle(headerStyle);



        headerCell = Ex2Header.createCell(2);
        headerCell.setCellValue("Kalman Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(3);
        headerCell.setCellValue("Kalman Ul Distance MAE");
        headerCell.setCellStyle(headerStyle);



        headerCell = Ex2Header.createCell(4);
        headerCell.setCellValue("Proposed Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(5);
        headerCell.setCellValue("Proposed Ul Distance MAE");
        headerCell.setCellStyle(headerStyle);



        headerCell = Ex2Header.createCell(6);
        headerCell.setCellValue("Proposed without Proximity Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(7);
        headerCell.setCellValue("Proposed without Proximity MAE");
        headerCell.setCellStyle(headerStyle);

    }

    public void writeExcelforEx2(UserLocation weightUl, UserLocation kalmanUl, UserLocation proposedUl, UserLocation proposedWithoutProximity,
                                 int weightFinishNum, int kalmanFinishNum, int proposedFinishNum, int proposedwithoutProximityFinishNum) throws IOException {

        nowNum++;
        log.info("Now Row : {}", nowNum);

        //data row 생성
        Row row = Ex2Sheet.createRow(nowNum);

        //셀 추가
        
        //Weight Ul
        Cell cell = row.createCell(0);
        String weightUl_xy = String.format("(%.2f, %.2f)", weightUl.getX(), weightUl.getY());
        cell.setCellValue(weightUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(weightUl.getDistanceDev());
        cell.setCellStyle(style);

        //Kalman Ul
        cell = row.createCell(2);
        String kalmanUl_xy = String.format("(%.2f, %.2f)", kalmanUl.getX(), kalmanUl.getY());
        cell.setCellValue(kalmanUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue(kalmanUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proposed Ul
        cell = row.createCell(4);
        String proposedUl_xy = String.format("(%.2f, %.2f)", proposedUl.getX(), proposedUl.getY());
        cell.setCellValue(proposedUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue(proposedUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proposed without
        cell = row.createCell(6);
        String proposedWithoutProximity_xy = String.format("(%.2f, %.2f)", proposedWithoutProximity.getX(), proposedWithoutProximity.getY());
        cell.setCellValue(proposedWithoutProximity_xy);
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue(proposedWithoutProximity.getDistanceDev());
        cell.setCellStyle(style);

        //1000번
        if (nowNum == 1000) {
            createFileAndRewrite();
        }
    }










    //-----------------------------------------------------------for Ex1-----------------------------------------------------------
    public void forEx1Setting() {
        Ex2Sheet = workbook.createSheet("Ex1");


        Ex2Sheet.setColumnWidth(0, 10000);
        Ex2Sheet.setColumnWidth(1, 10000);
        Ex2Sheet.setColumnWidth(2, 5000);

        Ex2Sheet.setColumnWidth(3, 10000);
        Ex2Sheet.setColumnWidth(4, 10000);
        Ex2Sheet.setColumnWidth(5, 5000);

        Ex2Sheet.setColumnWidth(6, 10000);
        Ex2Sheet.setColumnWidth(7, 10000);
        Ex2Sheet.setColumnWidth(8, 5000);

        Ex2Sheet.setColumnWidth(9, 10000);
        Ex2Sheet.setColumnWidth(10, 10000);

        Ex2Header = Ex2Sheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = Ex2Header.createCell(0);
        headerCell.setCellValue("Original RSSI");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(1);
        headerCell.setCellValue("Original distance");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(3);
        headerCell.setCellValue("Weight RSSI");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(4);
        headerCell.setCellValue("Weight distance");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(6);
        headerCell.setCellValue("Kalman RSSI");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(7);
        headerCell.setCellValue("Kalman distance");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(9);
        headerCell.setCellValue("Proposed RSSI");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(10);
        headerCell.setCellValue("Proposed distance");
        headerCell.setCellStyle(headerStyle);


    }

    public void writeExcelforEx1(SelectedVO originalVo, SelectedVO weightVo, SelectedVO kalmanVo, SelectedVO proposedVo, int totalNum) throws IOException {


        //data row 생성
        Row row = Ex2Sheet.createRow(totalNum);

        //셀 추가

        //Original RSSI
        Cell cell = row.createCell(0);
        cell.setCellValue(originalVo.getRssi1());
        cell.setCellStyle(style);
        //Original Distance
        cell = row.createCell(1);
        cell.setCellValue(originalVo.getDistance1());
        cell.setCellStyle(style);


        //Weight Ul
        cell = row.createCell(3);
        cell.setCellValue(weightVo.getRssi1());
        cell.setCellStyle(style);
        //Weight Distance
        cell = row.createCell(4);
        cell.setCellValue(weightVo.getDistance1());
        cell.setCellStyle(style);




        //Kalman RSSI
        cell = row.createCell(6);
        cell.setCellValue(kalmanVo.getRssi1());
        cell.setCellStyle(style);
        //Kalman Distance
        cell = row.createCell(7);
        cell.setCellValue(kalmanVo.getDistance1());
        cell.setCellStyle(style);


        //Proposed RSSI
        cell = row.createCell(9);
        cell.setCellValue(proposedVo.getRssi1());
        cell.setCellStyle(style);
        //Proposed Distance
        cell = row.createCell(10);
        cell.setCellValue(proposedVo.getDistance1());
        cell.setCellStyle(style);

//        //1000번
//        if (totalNum == 1000) {
//            createFileAndRewrite();
//        }
    }






    //-----------------------------------------------------------for KalmanParameter Experiment-----------------------------------------------------------
    public void forKalmanParameterExSetting() {
        Ex2Sheet = workbook.createSheet("KalmanParameter");

        String ourStr;
        String fusionStr;
        String performanceStr;

        for(int i=0; i<47; i++) {
            Ex2Sheet.setColumnWidth(i, 10000);
        }

        Ex2Header = Ex2Sheet.createRow(0);
        Cell headerCell;

        //헤더 셀(컬럼)
        for(int i=0; i<47; i++) {
            if(i==15 || i==31) {}
            else {
                ourStr = String.format("our %dm", i);
                headerCell = Ex2Header.createCell(i);
                headerCell.setCellValue(ourStr);
                headerCell.setCellStyle(headerStyle);

                fusionStr = String.format("fusion %dm", i+16);
                headerCell = Ex2Header.createCell(i);
                headerCell.setCellValue(fusionStr);
                headerCell.setCellStyle(headerStyle);

                performanceStr = String.format("performance %dm", i+32);
                headerCell = Ex2Header.createCell(i);
                headerCell.setCellValue(performanceStr);
                headerCell.setCellStyle(headerStyle);
            }
        }
    }

    public void writeExcelforKalmanParameterEx(int meter, ArrayList<Double>[] ourKalmanArrays, ArrayList<Double>[] fusionKalmanArrays, ArrayList<Double>[] performanceKalmanArrays) throws IOException {


        //data row 생성 -> 생성자에서

        Cell cell;

        //셀 추가

        for(int i=0; i<100; i++) {
            //our
            cell = rowArray.get(i).createCell(meter);
            cell.setCellValue(ourKalmanArrays[meter].get(i));
            cell.setCellStyle(style);

            //fusion
            cell = rowArray.get(i).createCell(meter+16);
            cell.setCellValue(fusionKalmanArrays[meter].get(i));
            cell.setCellStyle(style);

            //performance
            cell = rowArray.get(i).createCell(meter+32);
            cell.setCellValue(performanceKalmanArrays[meter].get(i));
            cell.setCellStyle(style);
        }

//        if(meter == 14) {
//
//        }
//        if (totalNum == 1000) {
//            createFileAndRewrite();
//        }
    }



    //-----------------------------------------------------------for KalmanParameter Experiment 2222222-----------------------------------------------------------
    //AP1~AP8 1000개씩 읽어온후 세가지 칼만 방식 적용하고 엑셀에 쓰기
    public void forKalmanParameter2ExSetting() {
        Ex2Sheet = workbook.createSheet("KalmanParameter2");

        String ourStr;
        String fusionStr;
        String performanceStr;

        //data row 생성
        for(int j=0; j<1001; j++) {
            rowArray.add(Ex2Sheet.createRow(j));
        }

        for(int i=0; i<26; i++) {
            Ex2Sheet.setColumnWidth(i, 10000);
        }

//        Ex2Header = Ex2Sheet.createRow(0);
//        Ex2Header = rowArray.get(0);
        Cell headerCell;

        //헤더 셀(컬럼)
        for(int i=0; i<8; i++) {
//            if(i==8 || i==17) {}
//            else {
            ourStr = String.format("our AP %d", i+1);
            headerCell = rowArray.get(0).createCell(i);
            headerCell.setCellValue(ourStr);
            headerCell.setCellStyle(headerStyle);

            fusionStr = String.format("fusion AP %d", i+1);
            headerCell = rowArray.get(0).createCell(i+9);
            headerCell.setCellValue(fusionStr);
            headerCell.setCellStyle(headerStyle);

            performanceStr = String.format("performance AP %d", i+1);
            headerCell = rowArray.get(0).createCell(i+18);
            headerCell.setCellValue(performanceStr);
            headerCell.setCellStyle(headerStyle);
//            }
        }
    }

    public void writeExcelforKalmanParameter2Ex(int apNum, ArrayList<Double>[] ourKalmanArrays, ArrayList<Double>[] fusionKalmanArrays, ArrayList<Double>[] performanceKalmanArrays) throws IOException {


        //data row 생성 -> 생성자에서

        Cell cell;

        //셀 추가

        for(int i=0; i<1000; i++) {
            //our
            cell = rowArray.get(i+1).createCell(apNum);
            cell.setCellValue(ourKalmanArrays[apNum].get(i));
            cell.setCellStyle(style);

            //fusion
            cell = rowArray.get(i+1).createCell(apNum+9);
            cell.setCellValue(fusionKalmanArrays[apNum].get(i));
            cell.setCellStyle(style);

            //performance
            cell = rowArray.get(i+1).createCell(apNum+18);
            cell.setCellValue(performanceKalmanArrays[apNum].get(i));
            cell.setCellStyle(style);
        }

//        if(meter == 14) {
//
//        }
//        if (totalNum == 1000) {
//            createFileAndRewrite();
//        }
    }

    public double calcDistanceforPOI(double tempRssi) {

        float tempAlpha= -23;
        double lossNum= 3.81;

        double distance = Math.pow(10, (tempAlpha-tempRssi)/(10*lossNum));

        return distance;
    }




    //-----------------------------------------------------------for RSSI Filter Test-----------------------------------------------------------
    public void forRSSIFilterSetting() {
        Ex2Sheet = workbook.createSheet("RSSIFilterTest");


        Ex2Sheet.setColumnWidth(0, 10000);
        Ex2Sheet.setColumnWidth(1, 10000);
        Ex2Sheet.setColumnWidth(2, 10000);
        Ex2Sheet.setColumnWidth(3, 10000);
        Ex2Sheet.setColumnWidth(4, 10000);
        Ex2Sheet.setColumnWidth(5, 10000);
        Ex2Sheet.setColumnWidth(6, 10000);
        Ex2Sheet.setColumnWidth(7, 10000);
        Ex2Sheet.setColumnWidth(8, 10000);

        Ex2Header = Ex2Sheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = Ex2Header.createCell(0);
        headerCell.setCellValue("Original");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(1);
        headerCell.setCellValue("Only RM");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(2);
        headerCell.setCellValue("RM+F");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(3);
        headerCell.setCellValue("RM+M");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(4);
        headerCell.setCellValue("RM+K");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(5);
        headerCell.setCellValue("RM+K+F");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(6);
        headerCell.setCellValue("RM+K+M");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(7);
        headerCell.setCellValue("RM+F+K");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(8);
        headerCell.setCellValue("RM+M+K");
        headerCell.setCellStyle(headerStyle);
    }

    public void writeExcelforRSSIFilter(int i,
                                        double arrayOrigin, double arrayRm,
                                        double arrayFeedback, double arrayMAF, double arrayKalman,
                                        double arrayKF, double arrayKM,
                                        double arrayFK, double arrayMK) throws IOException {

//        nowNum++;
        log.info("Now Row : {}", i+1);

        //data row 생성
        Row row = Ex2Sheet.createRow(i+1);

        //셀 추가

        //Original
        Cell cell = row.createCell(0);
        cell.setCellValue(arrayOrigin);
        cell.setCellStyle(style);
        //Only RM
        cell = row.createCell(1);
        cell.setCellValue(arrayRm);
        cell.setCellStyle(style);
        //RM + Feedback
        cell = row.createCell(2);
        cell.setCellValue(arrayFeedback);
        cell.setCellStyle(style);
        //RM + MAF
        cell = row.createCell(3);
        cell.setCellValue(arrayMAF);
        cell.setCellStyle(style);
        //RM + Kalman
        cell = row.createCell(4);
        cell.setCellValue(arrayKalman);
        cell.setCellStyle(style);
        //RM + Kalman + Feedback
        cell = row.createCell(5);
        cell.setCellValue(arrayKF);
        cell.setCellStyle(style);
        //RM + Kalman + MAF
        cell = row.createCell(6);
        cell.setCellValue(arrayKM);
        cell.setCellStyle(style);
        //RM + Feedback + Kalman
        cell = row.createCell(7);
        cell.setCellValue(arrayFK);
        cell.setCellStyle(style);
        //RM + MAF + Kalman
        cell = row.createCell(8);
        cell.setCellValue(arrayMK);
        cell.setCellStyle(style);

    }





    //-----------------------------------------------------------for Loc Filter Test-----------------------------------------------------------
    public void forLocFilterSetting() {
        Ex2Sheet = workbook.createSheet("LocFilterTest");


        Ex2Sheet.setColumnWidth(0, 10000);
        Ex2Sheet.setColumnWidth(1, 10000);
        Ex2Sheet.setColumnWidth(2, 10000);
        Ex2Sheet.setColumnWidth(3, 10000);
        Ex2Sheet.setColumnWidth(4, 10000);
        Ex2Sheet.setColumnWidth(5, 10000);
        Ex2Sheet.setColumnWidth(6, 10000);
        Ex2Sheet.setColumnWidth(7, 10000);
        Ex2Sheet.setColumnWidth(8, 10000);
        Ex2Sheet.setColumnWidth(9, 10000);


        Ex2Header = Ex2Sheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = Ex2Header.createCell(0);
        headerCell.setCellValue("Original Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(1);
        headerCell.setCellValue("Original Distance MAE");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(2);
        headerCell.setCellValue("RM+Kalman Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(3);
        headerCell.setCellValue("RM+Kalman Distance MAE");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(4);
        headerCell.setCellValue("RM+LocRM+2DMAF Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(5);
        headerCell.setCellValue("RM+LocRM+2DMAF Distance MAE");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(6);
        headerCell.setCellValue("RM+LocRM+2DMAF+2D Kalman Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(7);
        headerCell.setCellValue("RM+LocRM+2DMAF+2D Kalman Distance MAE");
        headerCell.setCellStyle(headerStyle);


        headerCell = Ex2Header.createCell(8);
        headerCell.setCellValue("Proposed Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(9);
        headerCell.setCellValue("Proposed Distance MAE");
        headerCell.setCellStyle(headerStyle);

    }

    public void writeExcelforLocFilter(UserLocation originalUl, UserLocation kalmanUl, UserLocation locMAFUl, UserLocation proposedWithoutProximity, UserLocation proposedUl) throws IOException {

        nowNum++;
        log.info("Now Row : {}", nowNum);

        //data row 생성
        Row row = Ex2Sheet.createRow(nowNum);

        //셀 추가

        //Weight Ul
        Cell cell = row.createCell(0);
        String originalUl_xy = String.format("(%.2f, %.2f)", originalUl.getX(), originalUl.getY());
        cell.setCellValue(originalUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(originalUl.getDistanceDev());
        cell.setCellStyle(style);

        //RM + Kalman Ul
        cell = row.createCell(2);
        String kalmanUl_xy = String.format("(%.2f, %.2f)", kalmanUl.getX(), kalmanUl.getY());
        cell.setCellValue(kalmanUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue(kalmanUl.getDistanceDev());
        cell.setCellStyle(style);

        //RM + Kalman Ul + Loc RM + 2D MAF
        cell = row.createCell(4);
        String locMAFUl_xy = String.format("(%.2f, %.2f)", locMAFUl.getX(), locMAFUl.getY());
        cell.setCellValue(locMAFUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue(locMAFUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proposed without Proximity
        cell = row.createCell(6);
        String proposedWithoutProximity_xy = String.format("(%.2f, %.2f)", proposedWithoutProximity.getX(), proposedWithoutProximity.getY());
        cell.setCellValue(proposedWithoutProximity_xy);
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue(proposedWithoutProximity.getDistanceDev());
        cell.setCellStyle(style);

        //Proposed
        cell = row.createCell(8);
        String proposed_xy = String.format("(%.2f, %.2f)", proposedUl.getX(), proposedUl.getY());
        cell.setCellValue(proposed_xy);
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue(proposedUl.getDistanceDev());
        cell.setCellStyle(style);

        //1000번
        if (nowNum == 300) {
            createFileAndRewrite();
        }
    }


    //-----------------------------------------------------------for Original Test-----------------------------------------------------------
    public void forOriginalTestSetting() {
        Ex2Sheet = workbook.createSheet("Original Test");


        Ex2Sheet.setColumnWidth(0, 10000);
        Ex2Sheet.setColumnWidth(1, 10000);



        Ex2Header = Ex2Sheet.createRow(0);

        //헤더 셀(컬럼)
        Cell headerCell = Ex2Header.createCell(0);
        headerCell.setCellValue("Original Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(1);
        headerCell.setCellValue("Original Distance MAE");
        headerCell.setCellStyle(headerStyle);


    }

    public void writeExcelforOriginalTest(UserLocation ul) throws IOException {

        nowNum++;
        log.info("Now Row : {}", nowNum);

        //data row 생성
        Row row = Ex2Sheet.createRow(nowNum);

        //셀 추가

        //Weight Ul
        Cell cell = row.createCell(0);
        String originalUl_xy = String.format("(%.2f, %.2f)", ul.getX(), ul.getY());
        cell.setCellValue(originalUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(ul.getDistanceDev());
        cell.setCellStyle(style);


        //1000번
        if (nowNum == 1000) {
            createFileAndRewrite();
        }
    }


    //=========================================================================================================================================
    //-----------------------------------------------------------for Loc Filter Test-----------------------------------------------------------
    //=========================================================================================================================================
    public void forLocationFilterTestSetting() {
        Ex2Sheet = workbook.createSheet("LocationFilterTest");
        
        Ex2Sheet.setColumnWidth(0, 10000);
        Ex2Sheet.setColumnWidth(1, 10000);
        Ex2Sheet.setColumnWidth(2, 10000);
        Ex2Sheet.setColumnWidth(3, 10000);
        Ex2Sheet.setColumnWidth(4, 10000);
        Ex2Sheet.setColumnWidth(5, 10000);
        Ex2Sheet.setColumnWidth(6, 10000);
        Ex2Sheet.setColumnWidth(7, 10000);
        Ex2Sheet.setColumnWidth(8, 10000);
        Ex2Sheet.setColumnWidth(9, 10000);
        Ex2Sheet.setColumnWidth(10, 10000);
        Ex2Sheet.setColumnWidth(11, 10000);
        Ex2Sheet.setColumnWidth(12, 10000);
        Ex2Sheet.setColumnWidth(13, 10000);
        Ex2Sheet.setColumnWidth(14, 10000);
        Ex2Sheet.setColumnWidth(15, 10000);

        Ex2Header = Ex2Sheet.createRow(0);

        //헤더 셀(컬럼)

        //Original
        Cell headerCell = Ex2Header.createCell(0);
        headerCell.setCellValue("Original Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(1);
        headerCell.setCellValue("Original Distance MAE");
        headerCell.setCellStyle(headerStyle);

        //RM
        headerCell = Ex2Header.createCell(2);
        headerCell.setCellValue("RM Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(3);
        headerCell.setCellValue("RM Distance MAE");
        headerCell.setCellStyle(headerStyle);

        //RM+Kalman
        headerCell = Ex2Header.createCell(4);
        headerCell.setCellValue("RM+Kalman Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(5);
        headerCell.setCellValue("RM+Kalman Distance MAE");
        headerCell.setCellStyle(headerStyle);

        //RM+Kalman+LocRM+2DMAF
        headerCell = Ex2Header.createCell(6);
        headerCell.setCellValue("RM+Kalman+LocRM+2DMAF Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(7);
        headerCell.setCellValue("RM+Kalman+LocRM+2DMAF Distance MAE");
        headerCell.setCellStyle(headerStyle);

        //Proximity Original
        headerCell = Ex2Header.createCell(8);
        headerCell.setCellValue("Proximity Original Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(9);
        headerCell.setCellValue("Proximity Original Distance MAE");
        headerCell.setCellStyle(headerStyle);

        //Proximity RM
        headerCell = Ex2Header.createCell(10);
        headerCell.setCellValue("Proximity RM Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(11);
        headerCell.setCellValue("Proximity RM Distance MAE");
        headerCell.setCellStyle(headerStyle);

        //Proximity RM+Kalman
        headerCell = Ex2Header.createCell(12);
        headerCell.setCellValue("Proximity RM+Kalman Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(13);
        headerCell.setCellValue("Proximity RM+Kalman MAE");
        headerCell.setCellStyle(headerStyle);

        //Proximity RM+Kalman+LocRM+2DMAF
        headerCell = Ex2Header.createCell(14);
        headerCell.setCellValue("Proximity RM+Kalman+LocRM+2DMAF Location");
        headerCell.setCellStyle(headerStyle);

        headerCell = Ex2Header.createCell(15);
        headerCell.setCellValue("Proximity RM+Kalman+LocRM+2DMAF Distance MAE");
        headerCell.setCellStyle(headerStyle);

    }

    public void writeExcelforLocationFilterTest(UserLocation originalUl, UserLocation removedUl, UserLocation kalmanUl, UserLocation locfilteredUl,
                                                UserLocation originalProximityUl, UserLocation removedProximityUl, UserLocation kalmanProximityUl, UserLocation locfilteredProximityUl) throws IOException {

        nowNum++;
        log.info("Exel Now Row : {}", nowNum);

        //data row 생성
        Row row = Ex2Sheet.createRow(nowNum);

        //셀 추가

        //Original
        Cell cell = row.createCell(0);
        String originalUl_xy = String.format("(%.2f, %.2f)", originalUl.getX(), originalUl.getY());
        cell.setCellValue(originalUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(originalUl.getDistanceDev());
        cell.setCellStyle(style);

        //RM
        cell = row.createCell(2);
        String removedUl_xy = String.format("(%.2f, %.2f)", removedUl.getX(), removedUl.getY());
        cell.setCellValue(removedUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue(removedUl.getDistanceDev());
        cell.setCellStyle(style);

        //RM+Kalman
        cell = row.createCell(4);
        String kalmanUl_xy = String.format("(%.2f, %.2f)", kalmanUl.getX(), kalmanUl.getY());
        cell.setCellValue(kalmanUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue(kalmanUl.getDistanceDev());
        cell.setCellStyle(style);

        //RM+Kalman+LocRM+2DMAF
        cell = row.createCell(6);
        String locfilteredUl_xy = String.format("(%.2f, %.2f)", locfilteredUl.getX(), locfilteredUl.getY());
        cell.setCellValue(locfilteredUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue(locfilteredUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proximity Original
        cell = row.createCell(8);
        String originalProximityUl_xy = String.format("(%.2f, %.2f)", originalProximityUl.getX(), originalProximityUl.getY());
        cell.setCellValue(originalProximityUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue(originalProximityUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proximity RM
        cell = row.createCell(10);
        String removedProximityUl_xy = String.format("(%.2f, %.2f)", removedProximityUl.getX(), removedProximityUl.getY());
        cell.setCellValue(removedProximityUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(11);
        cell.setCellValue(removedProximityUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proximity RM+Kalman
        cell = row.createCell(12);
        String kalmanProximityUl_xy = String.format("(%.2f, %.2f)", kalmanProximityUl.getX(), kalmanProximityUl.getY());
        cell.setCellValue(kalmanProximityUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(13);
        cell.setCellValue(kalmanProximityUl.getDistanceDev());
        cell.setCellStyle(style);

        //Proximity RM+Kalman+LocRM+2DMAF
        cell = row.createCell(14);
        String locfilteredProximityUl_xy = String.format("(%.2f, %.2f)", locfilteredProximityUl.getX(), locfilteredProximityUl.getY());
        cell.setCellValue(locfilteredProximityUl_xy);
        cell.setCellStyle(style);

        cell = row.createCell(15);
        cell.setCellValue(locfilteredProximityUl.getDistanceDev());
        cell.setCellStyle(style);

        //1000번
        if (nowNum == 1000) {
            createFileAndRewrite();
        }
    }






}
