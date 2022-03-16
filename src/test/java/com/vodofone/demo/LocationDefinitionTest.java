package com.vodofone.demo;

import cucumber.api.java.en.When;

import java.io.IOException;

public class LocationDefinitionTest extends SpringIntegrationTest {

  @When("When client call /")
    public void the_client_calls_iot_event_v() throws IOException {
      uploadData("http://localhost:8082/iot/event/v1/");
  }
}
