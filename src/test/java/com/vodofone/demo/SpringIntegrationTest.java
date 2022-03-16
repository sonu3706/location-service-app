package com.vodofone.demo;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(classes = VodafoneDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringIntegrationTest {
    public RestTemplate restTemplate = new RestTemplate();

    public void uploadData(String url) throws IOException {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("filePath", "src/main/resources/data/data.csv");
        HttpEntity<String> request = new HttpEntity<String>(dataMap.toString());
        String response = restTemplate.postForObject(url, request, String.class);

    }
}
