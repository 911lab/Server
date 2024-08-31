package com.example.bleLocationSystem.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.bleLocationSystem.LocFilterTestUI;
import com.example.bleLocationSystem.TestUI;
import com.example.bleLocationSystem.UI;
import com.example.bleLocationSystem.model.UserLocation;
import com.example.bleLocationSystem.model.VO;
import com.example.bleLocationSystem.originalTestUI;
import com.example.bleLocationSystem.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j //로깅 어노테이션
public class ApController {
    // Real Real Real
    PositioningService positioningService = new PositioningService();

    //실제
//    ApService apService = new ApService();
    UI ui = new UI(positioningService.getW(),positioningService.getH());
    UserLocation ul;
    Map<String, Double> map = new HashMap<String, Double>();

    //Original Test
//    OriginalTestService originalService = new OriginalTestService();
//    UserLocation ul;
//    originalTestUI ui = new originalTestUI(originalService.getW(), originalService.getH());


    //Test
//    TestService testService = new TestService();
//    TestUI ui = new TestUI(testService.getW(), testService.getH());
//    UserLocation ul;
//    ArrayList<UserLocation> ulList;
//    Map<String, Double> map = new HashMap<String, Double>();


    //RSSI Test for Experiment 1
//    VO vooo;
//    RSSIFilterTestService testService = new RSSIFilterTestService();

    //Loc Filter Test
//    LocFiterTestService locService = new LocFiterTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(locService.getW(), locService.getH());

    //Thres Test
//    ThresTestService thresTestService = new ThresTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(locService.getW(), locService.getH());

    //Location Filter Test
//    LocationFilterTestService locationService = new LocationFilterTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(locationService.getW(), locationService.getH());

    //Threshold Test
//    ThresholdTestService thresholdTestService = new ThresholdTestService();
//    ArrayList<UserLocation> ulList;
//    LocFilterTestUI ui = new LocFilterTestUI(thresholdTestService.getW(), thresholdTestService.getH());

    //Dinamic Test
//    LocationFilterTestService locationService = new LocationFilterTestService();
//    ArrayList<UserLocation> ulList;
//    TestUI ui = new TestUI(locationService.getW(), locationService.getH());



    //앱으로부터 ap1, ap2, ap3 각각의 거리값 받기
//    실제
    @PostMapping("/api/distance")
    public ResponseEntity<Map<String, Double>> receiveDistance(VO vo) throws Exception {

    //Talend Api 사용시
//    @PostMapping("/api/distance")
//    public ResponseEntity<Map<String, Double>> receiveDistance(@RequestBody VO vo) throws Exception {

    //테스트시
//    @PostMapping("/api/distance")
//    public void receiveDistance(VO vo) throws Exception {

        //논문 실험 1 테스트시
//    @PostMapping("/api/distance")
//    public ResponseEntity<VO> receiveDistance(VO vo) throws Exception {

        //-------------Real Real Real--------------
        System.out.println(vo);
        ul = positioningService.trilateration(vo);
        if(ul != null) {
            ui.setUserLocation(ul);
            map.put("triangleNum", positioningService.getKalmanTriangleNum()*1.0);
            map.put("x", ul.getX());
            map.put("y", ul.getY());
        }

        return (ul != null) ?
                ResponseEntity.status(HttpStatus.OK).body(map) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();



        //-------------Real--------------
//        ul = apService.trilateration(vo);
//
//        if(ul != null) {
//            ui.setUserLocation(ul);
//            map.put("triangleNum", apService.getTriangleNum()*1.0);
//            map.put("x", ul.getX());
//            map.put("y", ul.getY());
//        }
//
//        return (ul != null) ?
//                ResponseEntity.status(HttpStatus.OK).body(map) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


        //Original Test
//        ul = originalService.trilateration(vo);
//        if(ul != null) {
//            ui.setUserLocation(ul);
//        }


//        ulList = testService.trilateration(vo);

//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//            map.put("triangleNum", testService.getTriangleNum()*1.0);
//            map.put("x", ul.getX());
//            map.put("y", ul.getY());
//        }


        //--------------Test--------------
//        ul = testService.trilateration(vo);
//        ulList = null;
//        ulList = testService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//            map.put("triangleNum", testService.getTriangleNum()*1.0);
//            map.put("x", ul.getX());
//            map.put("y", ul.getY());
//        }

//        return (ulList != null) ?
//                ResponseEntity.status(HttpStatus.OK).body(ulList.get(2)) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

//    }

        //--------------RSSI Test for Experiment 1--------------
        //        ul = testService.trilateration(vo);
//    vooo = null;
//    vooo = testService.trilateration(vo);
//
//        return (vooo != null) ?
//            ResponseEntity.status(HttpStatus.OK).body(vooo) :
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).build();


        //Loc Fiter Test
//        ulList = null;
//        ulList = locService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

        //Thres Test
//        ulList = null;
//        ulList = thresTestService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

        //--------------Location Filter Test--------------
        //--------------Threshold Test--------------

        //Location Filter Test
//        ulList = null;
//        ulList = locationService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//            map.put("triangleNum", locationService.getTriangleNum()*1.0);
//            map.put("x", ulList.get(2).getX());
//            map.put("y", ulList.get(2).getY());
//        }
//
//        return (ulList != null) ?
//        ResponseEntity.status(HttpStatus.OK).body(map) :
//        ResponseEntity.status(HttpStatus.BAD_REQUEST).build();




        //Threshold Test
//        ulList = null;
//        ulList = thresholdTestService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

        //--------------Dynamic Test--------------
//        ulList = null;
//        ulList = locationService.trilateration(vo);
//
//        if(ulList != null) {
//            ui.setUserLocation(ulList);
//        }

    }
}
