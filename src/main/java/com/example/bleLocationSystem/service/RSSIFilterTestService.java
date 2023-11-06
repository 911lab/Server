package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RSSIFilterTestService {

    ExelPOIHelper poiHelper;

    KalmanFilter kFilterFor10m;
    KalmanFilter kFilterFor15m;
    KalmanFilter roKFilterFor10m;
    KalmanFilter roKFilterFor15m;

    KalmanFilter roMafKFilterFor10m;
    KalmanFilter roMafKFilterFor15m;

    MAFilter mafFilter10m;
    MAFilter mafFilter15m;

    MAFilter mafFilter10m_romaf;
    MAFilter mafFilter15m_romaf;

    ArrayList<Double> original10m;
    ArrayList<Double> original15m;

    ArrayList<Double> array10m;
    ArrayList<Double> array15m;


    ArrayList<Double> ro10m;
    ArrayList<Double> ro15m;

    ArrayList<Double> kalman10m;
    ArrayList<Double> kalman15m;

    ArrayList<Double> roKalman10m;
    ArrayList<Double> roKalman15m;

    ArrayList<Double> maf10m;
    ArrayList<Double> maf15m;

    ArrayList<Double> roMaf10m;
    ArrayList<Double> roMaf15m;

    ArrayList<Double> roMafKalman10m;
    ArrayList<Double> roMafKalman15m;

    double outlier10m = -78;

    double outlier15m = -83;
    public  RSSIFilterTestService() {
        poiHelper = new ExelPOIHelper();

        kFilterFor10m = new KalmanFilter();
        kFilterFor15m = new KalmanFilter();

        roKFilterFor10m = new KalmanFilter();
        roKFilterFor15m = new KalmanFilter();

        roMafKFilterFor10m = new KalmanFilter();
        roMafKFilterFor15m = new KalmanFilter();

        mafFilter10m = new MAFilter();
        mafFilter15m = new MAFilter();

        mafFilter10m_romaf = new MAFilter();
        mafFilter15m_romaf = new MAFilter();


        array10m = new ArrayList<Double>();
        array15m = new ArrayList<Double>();

        startFilter();
        filtering();
    }

    public void startFilter() {
        try {
            FileInputStream file = new FileInputStream("C:\\Users\\JaeHyuk\\Desktop\\RSSIFilterTest.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            int rowindex=0;
            int columnindex=0;
            double value10m=1.0;
            double value15m=1.0;

            //시트 수 (첫번째에만 존재하므로 0을 준다)
            //만약 각 시트를 읽기위해서는 FOR문을 한번더 돌려준다
            XSSFSheet sheet=workbook.getSheetAt(0);
            //행의 수
            int rows=sheet.getPhysicalNumberOfRows();

            log.info("rows = {}", rows);

            for(rowindex=0; rowindex<rows; rowindex++){

                XSSFRow row = sheet.getRow(rowindex);

                if(row !=null) {

                    //셀의 수
                    int cells = row.getPhysicalNumberOfCells();
                    log.info("cells = {}", cells);
                    value10m = 1.0;
                    value15m = 1.0;

                    XSSFCell cell10m = row.getCell(0);

                    if (cell10m == null) {
                        array10m.add(value10m);
                    } else {
                        value10m = cell10m.getNumericCellValue();

                        log.info("10m value = {}", value10m);
                        array10m.add(value10m);
                    }

                    XSSFCell cell15m = row.getCell(1);

                    if (cell15m == null) {
                        array15m.add(value15m);
                    } else {
                        value15m = cell15m.getNumericCellValue();

                        log.info("15m value = {}", value15m);
                        array15m.add(value15m);
                    }

                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void filtering() {
        original10m = new ArrayList<Double>(array10m);
        original15m = new ArrayList<Double>(array15m);

        ro10m = rmOutlier10m(original10m);
        ro15m = rmOutlier15m(original15m);

        kalman10m = kalmanFilter10m(original10m);
        kalman15m = kalmanFilter15m(original15m);

        roKalman10m = roKalmanFilter10m(ro10m);
        roKalman15m = roKalmanFilter15m(ro15m);

        maf10m = mAF10m(original10m);
        maf15m = mAF15m(original15m);

        roMaf10m = roMAF10m(ro10m);
        roMaf15m = roMAF15m(ro15m);

        roMafKalman10m = roMafKalmanFilter10m(roMaf10m);
        roMafKalman15m = roMafKalmanFilter15m(roMaf15m);

        createCsv(original10m, original15m,
                ro10m, ro15m,
                kalman10m, kalman15m,
                roKalman10m, roKalman15m,
                maf10m, maf15m,
                roMaf10m, roMaf15m,
                roMafKalman10m, roMafKalman15m);
    }

    public void createCsv(ArrayList<Double> original10m, ArrayList<Double> original15m,
                          ArrayList<Double> ro10m, ArrayList<Double> ro15m,
                          ArrayList<Double> kalman10m, ArrayList<Double> kalman15m,
                          ArrayList<Double> roKalman10m, ArrayList<Double> roKalman15m,
                          ArrayList<Double> maf10m, ArrayList<Double> maf15m,
                          ArrayList<Double> roMaf10m, ArrayList<Double> roMaf15m,
                          ArrayList<Double> roMafKalman10m, ArrayList<Double> roMafKalman15m) {
        try {
            // 비콘 8개 각각 성능 테스트를 위한 엑셀 생성
//            poiHelper.writeTestExcel(originalVo, i);

            poiHelper.wrieteRssiFilterTestExcel(original10m, original15m,
                    ro10m, ro15m,
                    kalman10m, kalman15m,
                    roKalman10m, roKalman15m,
                    maf10m, maf15m,
                    roMaf10m, roMaf15m,
                    roMafKalman10m, roMafKalman15m);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Remove Outlier
    //10m
    public ArrayList<Double> rmOutlier10m(ArrayList<Double> arr){
        ArrayList<Double> r = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) <= outlier10m) {
                r.add(1.0);
            }
            else {
                r.add(arr.get(i));
            }
        }
        return r;
    }
    //15m
    public ArrayList<Double> rmOutlier15m(ArrayList<Double> arr) {
        ArrayList<Double> r = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) <= outlier15m) {
                r.add(1.0);
            }
            else {
                r.add(arr.get(i));
            }
        }
        return r;
    }


    //Kalman
    //10m
    public ArrayList<Double> kalmanFilter10m(ArrayList<Double> arr) {
        ArrayList<Double> k = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                k.add(kFilterFor10m.kalmanFiltering(arr.get(i)));
            } else {
                k.add(arr.get(i));
            }
        }
        return k;
    }

    //15m
    public ArrayList<Double> kalmanFilter15m(ArrayList<Double> arr) {
        ArrayList<Double> k = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                k.add(kFilterFor15m.kalmanFiltering(arr.get(i)));
            } else {
                k.add(arr.get(i));
            }
        }
        return k;
    }

    //MAF
    //10m
    public ArrayList<Double> mAF10m(ArrayList<Double> arr) {
        ArrayList<Double> m = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                m.add(mafFilter10m.push(arr.get(i)));
            } else {
                m.add(arr.get(i));
            }
        }
        return m;
    }

    public ArrayList<Double> mAF15m(ArrayList<Double> arr) {
        ArrayList<Double> m = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                m.add(mafFilter15m.push(arr.get(i)));
            } else {
                m.add(arr.get(i));
            }
        }
        return m;
    }


    //ROMAF
    //10m
    public ArrayList<Double> roMAF10m(ArrayList<Double> arr) {
        ArrayList<Double> m = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                m.add(mafFilter10m_romaf.push(arr.get(i)));
            } else {
                m.add(arr.get(i));
            }
        }
        return m;
    }

    //15m
    public ArrayList<Double> roMAF15m(ArrayList<Double> arr) {
        ArrayList<Double> m = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                m.add(mafFilter15m_romaf.push(arr.get(i)));
            } else {
                m.add(arr.get(i));
            }
        }
        return m;
    }


    //RO Kalman
    //10m
    public ArrayList<Double> roKalmanFilter10m(ArrayList<Double> arr) {
        ArrayList<Double> k = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                k.add(roKFilterFor10m.kalmanFiltering(arr.get(i)));
            } else {
                k.add(arr.get(i));
            }
        }
        return k;
    }

    //15m
    public ArrayList<Double> roKalmanFilter15m(ArrayList<Double> arr) {
        ArrayList<Double> k = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                k.add(roKFilterFor15m.kalmanFiltering(arr.get(i)));
            } else {
                k.add(arr.get(i));
            }
        }
        return k;
    }

    //RO MAF Kalman
    //10m
    public ArrayList<Double> roMafKalmanFilter10m(ArrayList<Double> arr) {
        ArrayList<Double> k = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                k.add(roMafKFilterFor10m.kalmanFiltering(arr.get(i)));
            } else {
                k.add(arr.get(i));
            }
        }
        return k;
    }
    //15m
    public ArrayList<Double> roMafKalmanFilter15m(ArrayList<Double> arr) {
        ArrayList<Double> k = new ArrayList<Double>();

        for(int i=0; i < arr.size(); i++) {
            if(arr.get(i) < 0) {
                k.add(roMafKFilterFor15m.kalmanFiltering(arr.get(i)));
            } else {
                k.add(arr.get(i));
            }
        }
        return k;
    }


}
