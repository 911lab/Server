package com.example.bleLocationSystem;

import com.example.bleLocationSystem.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BleLocationSystemApplication {

	public static void main(String[] args) {
		//-----------실제-----------
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(BleLocationSystemApplication.class, args);
		//-------------------------

//		new UI();
//		RSSIFilterTestService rfts = new RSSIFilterTestService();
		//new RSSIFilterTestService();

		//-----------테스트-----------
//		new Ex1Service();
		//---------------------------


		//-----------칼만 파라미터 테스트-----------
//		new KalmanParameterTestService();
//		new KalmanParameter2TestService();

		//-----------RSSI Filter Test-----------
//		new RSSIFilterOursTestService();

		//-----------Loc Filter Test-----------
		//new LocFiterTestService();

		//-----------Original Test -----------
//		new TwoPointOriginalTestService();
	}
}
