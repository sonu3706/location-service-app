package com.vodofone.demo.controller;

import com.vodofone.demo.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/iot/event/v1")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/")
    public ResponseEntity<?> uploadData(@RequestBody Map<String, String> filePath) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.uploadData(filePath.get("filePath")));
    }

    @GetMapping()
    public ResponseEntity<?> getDeviceLocation(@RequestParam String productId,
            @RequestParam long tStamp) {
        return ResponseEntity.status(HttpStatus.OK).body(locationService.getDeviceLocation(productId, tStamp));
    }
}
