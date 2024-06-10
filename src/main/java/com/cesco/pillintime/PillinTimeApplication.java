package com.cesco.pillintime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PillinTimeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PillinTimeApplication.class, args);
	}

}
