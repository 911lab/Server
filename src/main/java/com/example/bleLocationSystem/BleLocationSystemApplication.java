package com.example.bleLocationSystem;

import com.example.bleLocationSystem.service.RSSIFilterTestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BleLocationSystemApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(BleLocationSystemApplication.class, args);

//		new UI();
//		RSSIFilterTestService rfts = new RSSIFilterTestService();
		//new RSSIFilterTestService();
	}
}
