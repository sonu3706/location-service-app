package com.vodofone.demo.repository;

import com.vodofone.demo.model.Device;

import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    Boolean saveAllData(List<Device> devices);

    Optional<Device> getDeviceByProductIdAndTimeStamp(String productId, Long timestamp);

    List<Device> getAllDeviceByProductId(String productId);
}
