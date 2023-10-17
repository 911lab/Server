package com.example.bleLocationSystem.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class ExelPOIHelper {

    //엑셀 파일
    Workbook workbook;

    //엑셀 시트
    Sheet sheet;

    //헤더 로우
    Row header;

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
        ResultSetting();

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


    public void writeExcel(VO originalVo, UserLocation originalUl, VO filteredVo, UserLocation filteredUl, int i) throws IOException  {

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

        if(num%1000 == 0)  {
            createFileAndRewrite();
        }

    }


    public void createFileAndRewrite() throws IOException {
//        File currDir = new File(".");
//        String path = currDir.getAbsolutePath();
//        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        String fileLocation = "C:\\Users\\heehe\\Desktop\\bleExel\\1012\\beaconTest_2m2.xlsx";

//        String fileLocation = "C:\\Users\\JaeHyuk\\Desktop\\bleExel\\1016\\beaconTest5_1.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
        workbook = new XSSFWorkbook();
        styleSetting();
        ResultSetting();
    }

    public void rewrite() {

    }
}
