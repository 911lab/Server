package com.example.bleLocationSystem.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.bleLocationSystem.TestUI;
import com.example.bleLocationSystem.UI;
import com.example.bleLocationSystem.model.UserLocation;
import com.example.bleLocationSystem.model.VO;
import com.example.bleLocationSystem.service.ApService;
import com.example.bleLocationSystem.service.RSSIFilterTestService;
import com.example.bleLocationSystem.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j //로깅 어노테이션
public class ApController {
//    @Autowired
//    ApService apService;

    
    //실제
    ApService apService = new ApService();
//    UI ui = new UI();
    UI ui = new UI(apService.getW(),apService.getH());

//    ArrayList<UserLocation> ul = new ArrayList<>();
    UserLocation ul;

    //Map<String, Integer> map = new HashMap<String, Integer>();
    Map<String, Double> map = new HashMap<String, Double>();


    //Test
//    TestService testService = new TestService();

//    TestUI ui = new TestUI(testService.getWidth(),testService.getHeight());

//    UserLocation ul;





    //앱으로부터 ap1, ap2, ap3 각각의 거리값 받기
    @PostMapping("/api/distance")
//    public ResponseEntity<UserLocation> receiveDistance(@RequestBody VO vo) throws Exception {
//    public ResponseEntity<Map<String, Integer>> receiveDistance(VO vo) throws Exception {
    public ResponseEntity<Map<String, Double>> receiveDistance(VO vo) throws Exception {


        //-------------Real--------------
        ul = apService.trilateration(vo);

        if(ul != null) {
            ui.setUserLocation(ul);
            map.put("triangleNum", apService.getTriangleNum()*1.0);
            map.put("x", ul.getX());
            map.put("y", ul.getY());
        }



        return (ul != null) ?
                ResponseEntity.status(HttpStatus.OK).body(map) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();



//        --------------Test--------------
//        ul = testService.trilateration(vo);
//        testService.trilateration(vo);
//
//        if(ul != null) {
//            ui.setUserLocation(ul);
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(null);

    }
}
