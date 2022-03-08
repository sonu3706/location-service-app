package com.vodofone.demo.contoller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodofone.demo.model.dto.response.DeviceResponse;
import com.vodofone.demo.services.LocationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    // Parsing String format data into JSON format
    private static String jsonToString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String objectToJsonString(final Object object) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writer().withDefaultPrettyPrinter();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            objectMapper.setDateFormat(df);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Upload Data with given csv file")
    public void uploadDateSuccessTest() throws Exception {
        Map<String, String> map = new HashMap<>();
        Map<String, String> data = new HashMap<>();
        data.put("filePath", "filePath");
        map.put("Description", "Data Refreshed");
        when(locationService.uploadData(any(String.class))).thenReturn(map);
        MvcResult mvcResult = mockMvc.perform(post("/iot/event/v1/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonToString(data)))
                .andExpect(status().isOk()).andReturn();

        assertEquals(mvcResult.getResponse().getContentAsString(), "{\"Description\":\"Data Refreshed\"}");
        verify(locationService, times(1)).uploadData(any(String.class));
        verifyNoMoreInteractions(locationService);
    }

    @Test
    @DisplayName("Get device info for given product id and timestamp")
    public void getDeviceLocationSuccessTest() throws Exception {
        String productId = "WG11155638";
        Long timeStamp = Long.valueOf("1582625077000");
        when(locationService.getDeviceLocation(productId, timeStamp)).thenReturn(getDeviceResponseObject());
        MvcResult mvcResult = mockMvc.perform(get("/iot/event/v1")
                        .param("productId", productId)
                        .param("tStamp", String.valueOf(timeStamp)))
                .andExpect(status().isOk()).andReturn();

        assertEquals(mvcResult.getResponse().getContentAsString(), objectToJsonString(getDeviceResponseObject()));
        verify(locationService, times(1)).getDeviceLocation(productId, timeStamp);
        verifyNoMoreInteractions(locationService);

    }

    private DeviceResponse getDeviceResponseObject() {
        return DeviceResponse.builder()
                .battery("FULL")
                .id("WG11155638")
                .dateTime(new Date(1582605077000L))
                .description("SUCCESS: Location identified.")
                .latitude(BigDecimal.valueOf(51.5185))
                .longitude(BigDecimal.valueOf(-0.1736))
                .status("Active")
                .build();
    }
}
