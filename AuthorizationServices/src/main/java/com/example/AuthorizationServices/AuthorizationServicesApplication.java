package com.example.AuthorizationServices;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.builder.SpringApplicationBuilder;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

@ComponentScans({
		@ComponentScan(basePackages = "com.example.Common.client")
})
@EnableFeignClients(basePackages = "com.example.Common.client")
@EnableJpaRepositories(basePackages = "com.example.AuthorizationServices.repository")
@ComponentScan(basePackages = {"com.example.AuthorizationServices.controller","com.example.AuthorizationServices.service",
		"com.example.AuthorizationServices.external","com.example.AuthorizationServices.dtos"})


@SpringBootApplication
public class AuthorizationServicesApplication {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationServicesApplication.class);
	public static void main(String[] args) {
		final SpringApplicationBuilder builder = new SpringApplicationBuilder(AuthorizationServicesApplication.class);
		final ConfigurableApplicationContext ctx = builder.run(args);
		logger.info(getStartupInfo(ctx));

	}
	static String getStartupInfo(ConfigurableApplicationContext ctx) {
		final Environment env = ctx.getEnvironment();
		final String port = env.getProperty("server.port");
		final List<String> profile = Arrays.asList(env.getActiveProfiles());
		return "Started SpringBoot application running on port: " + port + " with profile: " + profile;
	}

@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}




}
