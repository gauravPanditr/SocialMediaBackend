package com.example.AuthorServices;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;



@EnableFeignClients(basePackages ="com.example.Common.client" )
@ComponentScans({
		@ComponentScan(basePackages = "com.example.Common.client")
})
@EnableJpaRepositories(basePackages = {"com.example.AuthorServices.repository"})
@ComponentScan(basePackages = {"com.example.AuthorServices.controller","com.example.AuthorServices.service","com.example.AuthorServices.external"})

@SpringBootApplication
public class AuthorServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorServicesApplication.class, args);
	}

}
