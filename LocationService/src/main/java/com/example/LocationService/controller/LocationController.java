package com.example.LocationService.controller;

import com.example.Common.model.LocationRequest;
import com.example.LocationService.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping("/create-location")
    public ResponseEntity<LocationRequest> createLocation(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestBody LocationRequest locationRequest) {
        try {
            LocationRequest createdLocation = locationService.createLocationRequest(
                    locationRequest.getLatitude(), locationRequest.getLongitude());
            return new ResponseEntity<>(createdLocation, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }
}
