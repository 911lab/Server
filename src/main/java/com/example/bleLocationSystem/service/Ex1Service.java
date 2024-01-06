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
import java.util.ArrayList;

@Slf4j
public class Ex1Service {

    VO originalVo;
    SelectedVO selectedVo;
    SelectedVO realOriginalVo;
    SelectedVO weightVo;
    SelectedVO kalmanVo;
    SelectedVO filteredVo;
    SelectedVO kfilteredVo;

    private float tempAlpha;
    private int lossNum;

    double originalRssi;

    ArrayList<Double> array;
    KalmanFilter kFilterForAp1;
    KalmanFilter kFilterForAp2;
    KalmanFilter kFilterForAp3;

    KalmanFilter kFilterForAp4;
    KalmanFilter kFilterForAp5;
    KalmanFilter kFilterForAp6;

    KalmanFilter kFilterForAp7;
    KalmanFilter kFilterForAp8;
    KalmanFilter kFilterForAp9;

    MAFilter mafFilter1;
    MAFilter mafFilter2;
    MAFilter mafFilter3;

    MAFilter mafFilter4;
    MAFilter mafFilter5;
    MAFilter mafFilter6;


    ExelPOIHelper poiHelper;

    double outlier = -83.0;

    int totalNum;


    WeightFilter weightFilter1;
    WeightFilter weightFilter2;
    WeightFilter weightFilter3;

    RemoveOutlier rm;


    public Ex1Service() {

        poiHelper = new ExelPOIHelper();

        //RSSI 보정 프로세스
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

        totalNum = 0;

        array = new ArrayList<Double>();

        //1.값 읽어오기 -> 2.필터링 -> 3.값 쓰기(new 엑셀파일에)
        //1
        array = readExel();
        //2
        startFilter(array);
    }

    private void startFilter(ArrayList<Double> array) {

        for(double rssi : array) {

            VO vo = new VO("ddd", 1.0, rssi, 1.0, 1.0, 1.0, 1.0, 1.0 ,1.0 ,1.0, 1.0, 1.0, 1.0, 1.0, 1.0 ,1.0 ,1.0);
            trilateration(vo);
        }
        try {
            poiHelper.createFileAndRewrite();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Double> readExel() {
        try {
            FileInputStream file = new FileInputStream("C:\\Users\\JaeHyuk\\Desktop\\3_5_10_15_org.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);

//            int rowindex=0;
            int columnindex=0;

            double value=1.0;

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

    public void trilateration(VO vo) {
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
