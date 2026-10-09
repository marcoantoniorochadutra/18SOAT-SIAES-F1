package com.fiap.siaes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SiaesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiaesApplication.class, args);
	}

}
