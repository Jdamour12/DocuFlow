package com.example.docuflow_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.docuflow_backend.config.PulsarConfig;

@SpringBootApplication
@EnableConfigurationProperties(PulsarConfig.class)
@ComponentScan("com.example.docuflow_backend")
public class DocuflowBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocuflowBackendApplication.class, args);
	}

}
