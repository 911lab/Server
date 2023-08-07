package com.example.bleLocationSystem.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.example.bleLocationSystem.model.UserLocation;
import com.example.bleLocationSystem.model.VO;
import com.example.bleLocationSystem.service.ApService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@Slf4j //로깅 어노테이션
public class ApController {
    @Autowired
    ApService apService;

    //앱으로부터 ap1, ap2, ap3 각각의 거리값 받기
    @PostMapping("/api/distance")
    public ResponseEntity<UserLocation> receiveDistance(VO vo) throws Exception {

        UserLocation ul = apService.trilateration(vo);

        //System.out.printf("Before Location : (%.2f, %.2f)  Distance Deviation : %.2fm%n", ul.getX(), ul.getY(), ul.getDistanceDev());

        return (ul != null) ?
                ResponseEntity.status(HttpStatus.OK).body(ul) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
