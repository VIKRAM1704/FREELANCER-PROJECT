package com.freelancenexus.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
        System.out.println("==============================================");
        System.out.println("Eureka Server started successfully!");
        System.out.println("Dashboard: http://localhost:8761");
        System.out.println("==============================================");
	}

}













/**
 * Eureka Server Application
 * 
 * This is the Service Discovery Server for Freelance Nexus microservices architecture.
 * All microservices register themselves with this Eureka server, enabling:
 * - Dynamic service discovery
 * - Load balancing
 * - Fault tolerance
 * - Service health monitoring
 * 
 * Key Features:
 * - Centralized service registry
 * - Real-time service instance tracking
 * - Heartbeat-based health checks
 * - Self-preservation mode during network partitions
 * - Web dashboard for monitoring services
 * 
 * @EnableEurekaServer: Configures this application as a Eureka Server
 * 
 * Access Eureka Dashboard: http://localhost:8761
 */