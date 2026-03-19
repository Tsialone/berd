package com.berd.dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DevApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevApplication.class, args);
	}

}
