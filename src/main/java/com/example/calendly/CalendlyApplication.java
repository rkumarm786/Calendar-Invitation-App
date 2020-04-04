package com.example.calendly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CalendlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalendlyApplication.class, args);
	}

}
