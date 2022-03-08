package com.vodofone.demo.repository.impl;

import com.vodofone.demo.model.Device;
import com.vodofone.demo.repository.LocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LocationRepositoryImpl implements LocationRepository {
    private static List<Device> deviceList;

    @Autowired
    public LocationRepositoryImpl() {
        deviceList = new ArrayList<>();
    }

    @Override
    public Boolean saveAllData(List<Device> devices) {
        // If List is not empty then clear the content and then add all data
        if (!deviceList.isEmpty()) {
            deviceList.clear();
        }
        return deviceList.addAll(devices);
    }

    @Override
    public Optional<Device> getDeviceByProductIdAndTimeStamp(String productId, Long timestamp) {
        return deviceList.stream().filter(data -> productId.equals(data.getProductId()))
                .filter(data -> timestamp.equals(data.getDateTime())).findAny();
    }

    @Override
    public List<Device> getAllDeviceByProductId(String productId) {
        return deviceList.stream().filter(data -> productId.equals(data.getProductId())).collect(Collectors.toList());
    }
}
