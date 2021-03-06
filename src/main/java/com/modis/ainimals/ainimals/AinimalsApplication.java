package com.modis.ainimals.ainimals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.modis.ainimals.ainimals"})
public class AinimalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AinimalsApplication.class, args);
	}

}
