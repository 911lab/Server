package com.example.bleLocationSystem.service;

import lombok.Getter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;

public class TwoPointOriginalTestService {
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99
    ArrayList<Double>[] originArrays;
    public TwoPointOriginalTestService(){
        originArrays = readExel();

    }
    public ArrayList<Double>[] readExel() {

        ArrayList<Double>[] arrays = new ArrayList[2];

        for(int i=0; i<8; i++) {
            arrays[i] = new ArrayList<Double>();
        }
        try {
            FileInputStream file = new FileInputStream("C:\\Users\\heejin\\Desktop\\BLE_Test\\Kalman_Parameter_1000\\RawData\\10mNLOS.xlsx");
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

}
