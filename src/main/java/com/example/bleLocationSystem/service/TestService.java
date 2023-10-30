package com.example.bleLocationSystem.service;

import com.example.bleLocationSystem.model.ExelPOIHelper;
import com.example.bleLocationSystem.model.UserLocation;
import com.example.bleLocationSystem.model.VO;

import java.io.IOException;
import java.util.ArrayList;

public class TestService {
    VO originalVo;
    VO realNoFIlterVo;


    private float tempAlpha;
    private int lossNum;

    int i=0;
    ExelPOIHelper poiHelper;

    public TestService() {
        poiHelper = new ExelPOIHelper();
    }

    public void trilateration(VO vo) {
        i++;
        originalVo = vo;



        createCsv(originalVo);
    }

    //엑셀 파일 만들기
    public void createCsv(VO originalVo) {
        try {
            // 성능 테스트를 위한 엑셀 생성
            poiHelper.writeTestExcel(originalVo, i);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
