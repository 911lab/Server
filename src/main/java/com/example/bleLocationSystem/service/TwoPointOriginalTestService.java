package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.LocFilterTestUI;
import com.example.bleLocationSystem.model.*;
import com.example.bleLocationSystem.originalTestUI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
public class TwoPointOriginalTestService {
    ExelPOIHelper poiHelper;
    @Getter
    double w = 15.0;
    @Getter
    double h = 15.0*Math.sqrt(3)/2;  //12.99

    ArrayList<String>[] originArrays;
    String value;

    private double tempAlpha;
    private double lossNum;

    VO originalVo;
    SelectedVO selectedVo;
    UserLocation ul;

    @Getter
    int triangleNum;

    Ap ap1;
    Ap ap2;
    Ap ap3;

    ArrayList<UserLocation>[] ulList;
    originalTestUI ui = new originalTestUI(w, h);

    public TwoPointOriginalTestService(){
        poiHelper = new ExelPOIHelper();

        ulList = new ArrayList[2];

        for(int i=0; i<2; i++) {
            ulList[i] = new ArrayList<UserLocation>();
        }

        originArrays = readExel();
//        System.out.printf("originArrays length = %d\n", originArrays.length);
//        System.out.printf("originArrays[0] size = %d\n", originArrays[0].size());
//        System.out.printf("originArrays[1] size = %d\n", originArrays[1].size());



        for(int i=0; i < 100; i++) {
            ulList[0].add(splitString(originArrays[0].get(i)));
            ulList[1].add(splitString(originArrays[1].get(i)));

        }

        ui.setUserLocation(ulList);


    }

    public UserLocation splitString(String str) {

        double x=0;
        double y=0;

        String valueTemp;

        valueTemp = str.replace("(","");
        valueTemp = valueTemp.replace(")","");
        valueTemp = valueTemp.replace(" ","");

//        System.out.printf("valueTemp = %s\n", valueTemp);

        String[] splitedArray = valueTemp.split(",");

//        System.out.printf("valueTemp x = %s, y = %s\n", splitedArray[0], splitedArray[1]);

        x = Double.parseDouble(splitedArray[0]);
        y = Double.parseDouble(splitedArray[1]);

        System.out.printf("valueTemp x = %f, y = %f\n", x, y);


        return new UserLocation(x,y,"ddd");
    }


    public ArrayList<String>[] readExel() {

        ArrayList<String>[] arrays = new ArrayList[2];

        for(int i=0; i<2; i++) {
            arrays[i] = new ArrayList<String>();
        }
        try {
//            FileInputStream file = new FileInputStream("C:\\Users\\heejin\\Desktop\\original_6(37.5,12),16(15,10).xlsx");
            FileInputStream file = new FileInputStream("C:\\Users\\JaeHyuk\\Desktop\\original_6(37.5,12),16(15,10).xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);


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
                        value = "";

                        XSSFCell cell = row.getCell(cellindex);

                        if (cell == null) {
                            arrays[rowindex].add("");
                        } else {
                            value = cell.getStringCellValue();

//                            System.out.printf("value = %s\n", value);

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
