package com.vodofone.demo.services;

import com.vodofone.demo.model.dto.response.DeviceResponse;

import java.util.Map;

public interface LocationService {
    Map<String, String> uploadData(String filePath);

    DeviceResponse getDeviceLocation(String productId, Long tStamp);

}
