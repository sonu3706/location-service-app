package com.vodofone.demo.service;

import com.vodofone.demo.exception.CustomException;
import com.vodofone.demo.exception.ErrorCode;
import com.vodofone.demo.model.Device;
import com.vodofone.demo.model.Status;
import com.vodofone.demo.model.dto.response.DeviceResponse;
import com.vodofone.demo.repository.LocationRepository;
import com.vodofone.demo.services.LocationService;
import com.vodofone.demo.services.impl.LocationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    public static String filePath = null;

    public LocationService locationService;
    @Mock
    private LocationRepository locationRepository;

    @BeforeEach
    public void setUp() {
        filePath = "src/main/resources/data/data.csv";
        MockitoAnnotations.openMocks(this);
        locationService = new LocationServiceImpl(locationRepository);
    }

    @Test
    @DisplayName("When data is uploaded with given file")
    public void uploadDataSuccessTest() {
        when(locationRepository.saveAllData(any())).thenReturn(Boolean.TRUE);
        Map<String, String> map = locationService.uploadData(filePath);
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("Data Refreshed", map.get("Description"));

        verify(locationRepository, times(1)).saveAllData(any());
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    @DisplayName("When file path is missing")
    public void uploadDataFilePathMissingExceptionTest() {
        filePath = null;
        CustomException exception = assertThrows(CustomException.class, () -> locationService.uploadData(filePath));
        assertEquals(ErrorCode.FILE_PATH_MISSING, exception.getCode());
        assertEquals("ERROR: File path missing", exception.getMessage());
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    @DisplayName("When file is not found")
    public void uploadDataFileNotFoundExceptionTest() {
        filePath = "data-v1.csv";
        CustomException exception = assertThrows(CustomException.class, () -> locationService.uploadData(filePath));
        assertEquals(ErrorCode.FILE_NOT_FOUND, exception.getCode());
        assertEquals("ERROR: No data file found", exception.getMessage());
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    @DisplayName("When device is fetched with given product id and timestamp")
    public void getDeviceLocationSuccessTest() {
        String productId = "WG11155638";
        Long timeStamp = Long.valueOf("1582605077000");
        Optional<Device> deviceOptional = getDeviceObject(productId, timeStamp);
        when(locationRepository.getDeviceByProductIdAndTimeStamp(any(), any())).thenReturn(deviceOptional);
        DeviceResponse deviceResponse = locationService.getDeviceLocation(productId, timeStamp);

        assertNotNull(deviceResponse);
        assertEquals(productId, deviceResponse.getId());
        assertEquals("CyclePlusTracker", deviceResponse.getName());
        assertEquals("SUCCESS: Location identified.", deviceResponse.getDescription());
        assertEquals(new Date(timeStamp), deviceResponse.getDateTime());
        assertEquals("Active", deviceResponse.getStatus());
        assertEquals("FULL", deviceResponse.getBattery());
        verify(locationRepository, times(1)).getDeviceByProductIdAndTimeStamp(any(), any());
        verifyNoMoreInteractions(locationRepository);

    }

    @Test
    @DisplayName("When device is fetched with product id and nearest timestamp")
    public void getDeviceLocationByNearestDateSuccessTest() {
        String productId = "WG11155638";
        Long timeStamp = Long.valueOf("1582625077000");
        List<Device> devices = new ArrayList<>();
        devices.add(getDeviceObject(productId, timeStamp).get());
        when(locationRepository.getDeviceByProductIdAndTimeStamp(any(), any())).thenReturn(Optional.empty());
        when(locationRepository.getAllDeviceByProductId(productId)).thenReturn(devices);
        DeviceResponse deviceResponse = locationService.getDeviceLocation(productId, timeStamp);

        assertNotNull(deviceResponse);
        assertEquals(productId, deviceResponse.getId());
        assertEquals("CyclePlusTracker", deviceResponse.getName());
        assertEquals("SUCCESS: Location identified.", deviceResponse.getDescription());
        assertEquals(new Date(timeStamp), deviceResponse.getDateTime());
        assertEquals("Active", deviceResponse.getStatus());
        assertEquals("FULL", deviceResponse.getBattery());
        verify(locationRepository, times(1)).getDeviceByProductIdAndTimeStamp(productId, timeStamp);
        verify(locationRepository, times(1)).getAllDeviceByProductId(productId);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    @DisplayName("When device not found")
    public void getDeviceLocationDeviceNotFoundExceptionTest() {
        String productId = "WG111556390";
        Long timeStamp = Long.valueOf("1582625077000");
        when(locationRepository.getDeviceByProductIdAndTimeStamp(any(), any())).thenReturn(Optional.empty());
        CustomException exception = assertThrows(CustomException.class, () -> locationService.getDeviceLocation(productId, timeStamp));

        assertEquals(ErrorCode.NO_DEVICE_FOUND, exception.getCode());
        assertEquals("ERROR: WG111556390 not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(locationRepository, times(1)).getDeviceByProductIdAndTimeStamp(productId, timeStamp);
        verify(locationRepository, times(1)).getAllDeviceByProductId(productId);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    @DisplayName("When device cannot be located")
    public void getDeviceLocationUnableToLocateDeviceExceptionTest() {
        String productId = "WG11155638";
        Long timeStamp = Long.valueOf("1582605077000");
        Optional<Device> deviceOptional = getDeviceObject(productId, timeStamp);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();
            device.setAirplaneMode(Status.OFF);
            device.setLatitude(null);
            device.setLongitude(null);
        }
        when(locationRepository.getDeviceByProductIdAndTimeStamp(any(), any())).thenReturn(deviceOptional);
        CustomException exception = assertThrows(CustomException.class, () -> locationService.getDeviceLocation(productId, timeStamp));

        assertEquals(ErrorCode.UNABLE_TO_LOCATE_DEVICE, exception.getCode());
        assertEquals("ERROR: Device could not be located", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(locationRepository, times(1)).getDeviceByProductIdAndTimeStamp(productId, timeStamp);
        verifyNoMoreInteractions(locationRepository);
    }

    private Optional<Device> getDeviceObject(String productId, Long timeStamp) {
        Device device = Device.builder()
                .battery(0.99)
                .eventId(10001L)
                .productId(productId)
                .latitude(BigDecimal.valueOf(51.5185))
                .longitude(BigDecimal.valueOf(-0.1736))
                .light(Status.ON)
                .airplaneMode(Status.OFF)
                .dateTime(timeStamp)
                .build();
        return Optional.ofNullable(device);
    }
}
