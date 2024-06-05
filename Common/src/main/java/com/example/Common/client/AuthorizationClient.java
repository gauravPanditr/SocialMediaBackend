package com.example.Common.client;


import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "AuthorizationServices",
        url = "http://localhost:8000/api",
        fallback = AuthorizationClientFallback.class

)
public interface AuthorizationClient {

    @GetMapping("/user-info")
    ResponseEntity<String> getUserDetails(@RequestHeader("Authorization") String jwt);
}
