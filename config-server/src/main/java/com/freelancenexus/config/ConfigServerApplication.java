package com.freelancenexus.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
		System.out.println("==============================================");
        System.out.println("Config Server started successfully on port 8888");
        System.out.println("==============================================");
	}

}


/**
 * Config Server Application
 * 
 * This is the centralized configuration server for Freelance Nexus microservices.
 * It manages all application configurations from a single location (filesystem or Git).
 * 
 * Key Features:
 * - Centralized configuration management
 * - Environment-specific configurations (dev, staging, prod)
 * - Dynamic configuration refresh without service restart
 * - Version control integration support
 * 
 * @EnableConfigServer: Enables Spring Cloud Config Server functionality
 */
