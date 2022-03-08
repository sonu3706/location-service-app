package com.vodofone.demo.services.impl;

import com.opencsv.bean.CsvToBeanBuilder;
import com.vodofone.demo.exception.CustomException;
import com.vodofone.demo.model.Device;
import com.vodofone.demo.model.Status;
import com.vodofone.demo.model.dto.response.DeviceResponse;
import com.vodofone.demo.repository.LocationRepository;
import com.vodofone.demo.services.LocationService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.vodofone.demo.exception.ErrorCode.FILE_NOT_FOUND;
import static com.vodofone.demo.exception.ErrorCode.FILE_PATH_MISSING;
import static com.vodofone.demo.exception.ErrorCode.NO_DEVICE_FOUND;
import static com.vodofone.demo.exception.ErrorCode.UNABLE_TO_LOCATE_DEVICE;

@Slf4j
@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    @SneakyThrows({ CustomException.class })
    public Map<String, String> uploadData(String filePath) {
        log.info("File path {}", filePath);
        Map<String, String> map = new HashMap<>();
        filePath = Optional.ofNullable(filePath).orElseThrow(
                () -> new CustomException(FILE_PATH_MISSING, "ERROR: File path missing", HttpStatus.CONFLICT));
        List<Device> deviceList = readCsvFile(filePath);
        if (!deviceList.isEmpty()) {
            locationRepository.saveAllData(deviceList);
            map.put("Description", "Data Refreshed");
        } else {
            map.put("Description", "Empty data received");
        }
        return map;
    }

    @Override
    @SneakyThrows({ CustomException.class })
    public DeviceResponse getDeviceLocation(String productId, Long tStamp) {
        log.info("Fetching record for Product Id {}", productId);
        Device device;
        Optional<Device> deviceOptional = locationRepository.getDeviceByProductIdAndTimeStamp(productId, tStamp);
        if (!deviceOptional.isPresent()) {
            List<Device> deviceList = locationRepository.getAllDeviceByProductId(productId);
            if (deviceList.isEmpty()) {
                log.error("Data not found for product id {}", productId);
                throw new CustomException(NO_DEVICE_FOUND, "ERROR: " + productId + " not found", HttpStatus.NOT_FOUND);
            }
            device = getNearestDate(deviceList, tStamp);
        } else {
            device = deviceOptional.get();
        }
        if (device.getAirplaneMode().equals(Status.OFF) && Objects.isNull(device.getLongitude())
                && Objects.isNull(device.getLatitude())) {
            log.info("Device could not be located for product id {}", productId);
            throw new CustomException(UNABLE_TO_LOCATE_DEVICE, "ERROR: Device could not be located",
                    HttpStatus.BAD_REQUEST);
        }
        return mapDeviceToDeviceResponse(device);
    }

    private List<Device> readCsvFile(String filePath) throws CustomException {
        List<Device> deviceList;
        try {
            FileReader fileReader = new FileReader(filePath);
            deviceList = new CsvToBeanBuilder(fileReader).withType(Device.class).build().parse();
        } catch (FileNotFoundException exception) {
            log.error("Error in reading file");
            throw new CustomException(FILE_NOT_FOUND, "ERROR: No data file found", HttpStatus.NOT_FOUND);
        }
        return deviceList;
    }

    /* Get Device with nearest timestamp of past*/
    private Device getNearestDate(List<Device> deviceList, Long timeStamp) {
        long minDiff = -1;
        long selectedDate = new Date(timeStamp).getTime();
        Date minDate = null;
        for (Device device : deviceList) {
            Date date = new Date(device.getDateTime());
            long diff = Math.abs(selectedDate - date.getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                minDate = date;
            }
        }
        Date finalMinDate = minDate;
        return deviceList.stream().filter(device -> new Date(device.getDateTime()).compareTo(finalMinDate) == 0)
                .findAny().get();
    }

    /* Map Device to DeviceResponse */
    private DeviceResponse mapDeviceToDeviceResponse(Device device) {
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setId(device.getProductId());
        deviceResponse.setDateTime(new Date(device.getDateTime()));
        // Set Name
        if (device.getProductId().startsWith("WG")) {
            deviceResponse.setName("CyclePlusTracker");
        } else if (device.getProductId().startsWith("69")) {
            deviceResponse.setName("GeneralTracker");
        }

        // Set Description and Status
        if (device.getAirplaneMode().equals(Status.ON) && Objects.isNull(device.getLatitude())
                && Objects.isNull(device.getLongitude())) {
            deviceResponse.setDescription("SUCCESS: Location not available: Please turn off airplane mode");
            deviceResponse.setStatus("In-Active");
        } else {
            deviceResponse.setDescription("SUCCESS: Location identified.");
        }

        // Set Status, Latitude and Longitude
        if (Objects.nonNull(device.getLongitude()) && Objects.nonNull(device.getLatitude())) {
            deviceResponse.setStatus("Active");
            deviceResponse.setLatitude(device.getLatitude());
            deviceResponse.setLongitude(device.getLongitude());
        }

        // Set Battery Status
        calculatePercentageAndSetBatteryStatus(deviceResponse, device.getBattery());
        return deviceResponse;
    }

    /* Set battery status by calculating percentage */
    private void calculatePercentageAndSetBatteryStatus(DeviceResponse deviceResponse, Double battery) {
        double percentage = (battery * 100);
        log.info("percentage {}", percentage);
        if (percentage > 98.0 || percentage == 98.0) {
            deviceResponse.setBattery("FULL");
        }
        if (percentage == 60.0 || (percentage > 60.0 && percentage < 98.0)) {
            deviceResponse.setBattery("HIGH");
        }
        if (percentage == 40.0 || (percentage > 40.0 && percentage < 60.0)) {
            deviceResponse.setBattery("MEDIUM");
        }
        if (percentage == 10.0 || (percentage > 10.0 && percentage < 40.0)) {
            deviceResponse.setBattery("LOW");
        }
        if(percentage < 10.0) {
            deviceResponse.setBattery("CRITICAL");
        }
    }
}
