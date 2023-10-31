package com.example.bleLocationSystem.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.bleLocationSystem.UI;
import com.example.bleLocationSystem.model.UserLocation;
import com.example.bleLocationSystem.model.VO;
import com.example.bleLocationSystem.service.ApService;
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

@RestController
@Slf4j //로깅 어노테이션
public class ApController {
//    @Autowired
//    ApService apService;

    ApService apService = new ApService();
    TestService testService = new TestService();
    UI ui = new UI();
//    UI ui = new UI(apService.getW(),apService.getH());
    ArrayList<UserLocation> ul = new ArrayList<>();
    //앱으로부터 ap1, ap2, ap3 각각의 거리값 받기
    @PostMapping("/api/distance")
//    public ResponseEntity<UserLocation> receiveDistance(@RequestBody VO vo) throws Exception {
    public ResponseEntity<UserLocation> receiveDistance(VO vo) throws Exception {


        //-------------Real--------------
//        ul = apService.trilateration(vo);

//        if(ul != null) {
//            ui.setUserLocation(ul);
//        }

//        return (ul != null) ?
//                ResponseEntity.status(HttpStatus.OK).body(ul.get(1)) :
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();



        //--------------Test--------------
        testService.trilateration(vo);

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }
}
