package com.freelancenexus.freelancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FreelancerServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FreelancerServiceApplication.class, args);
    }
}