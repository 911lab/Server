package com.example.bleLocationSystem.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class ExelPOIHelper {

    //엑셀 파일
    Workbook workbook;

    //엑셀 시트
    Sheet sheet;
    Sheet testSheet;

    Sheet oneBeaconSheet;

    //헤더 로우
    Row header;
    Row testHeader;

    Row oneBeaconHeader;

    //헤더 스타일
    CellStyle headerStyle;
    XSSFFont font;

    //셀 스타일
    CellStyle style;

    String strOrgloc;
    String strFilterdloc;

    int num;

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
        threeBeaconStopSetting();
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

        String fileLocation = "C:\\Users\\heehe\\Desktop\\.xlsx";

//        String fileLocation = "C:\\Users\\JaeHyuk\\Desktop\\rssifilterfinal.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
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
        cell.setCellFormula(original_xy);
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue(ul.getDistanceDev());
        cell.setCellStyle(style);

        //Rssi Filtered Ul
        cell = row.createCell(2);
        String filtered_xy = String.format("(%.2f, %.2f)", filteredUl.getX(), filteredUl.getY());
        cell.setCellFormula(filtered_xy);
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue(filteredUl.getDistanceDev());
        cell.setCellStyle(style);

        //Loc RO Ul
        cell = row.createCell(4);
        String ro_xy = String.format("(%.2f, %.2f)", roUl.getX(), roUl.getY());
        cell.setCellFormula(ro_xy);
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue(roUl.getDistanceDev());
        cell.setCellStyle(style);

        //Loc MAF Ul
        cell = row.createCell(6);
        String maf_xy = String.format("(%.2f, %.2f)", mafUl.getX(), mafUl.getY());
        cell.setCellFormula(maf_xy);
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue(mafUl.getDistanceDev());
        cell.setCellStyle(style);

        //Loc RO + Loc MAF Ul
        cell = row.createCell(8);
        String romaf_xy = String.format("(%.2f, %.2f)", roMafUl.getX(), roMafUl.getY());
        cell.setCellFormula(romaf_xy);
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue(roMafUl.getDistanceDev());
        cell.setCellStyle(style);

        if(num%6000 == 0)  {
            createFileAndRewrite();
        }
    }


}
