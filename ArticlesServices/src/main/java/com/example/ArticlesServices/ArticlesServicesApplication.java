package com.example.ArticlesServices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScans({
		@ComponentScan(basePackages = "com.example.Common.client")
})
@EnableFeignClients(basePackages = "com.example.Common.client")
@ComponentScan(basePackages = {"com.example.ArticlesServices.controller","com.example.ArticlesServices.services",
		"com.example.ArticlesServices.external","com.example.ArticlesServices.dtos","com.example.ArticlesServices.enities"})
@EnableJpaRepositories(basePackages = "com.example.ArticlesServices.repository")
@EnableCaching

public class ArticlesServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArticlesServicesApplication.class, args);
	}

}
