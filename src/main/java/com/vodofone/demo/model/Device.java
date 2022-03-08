package com.vodofone.demo.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @CsvBindByName(column = "EventId")
    private Long eventId;

    @CsvBindByName(column = "ProductId")
    private String productId;

    @CsvBindByName(column = "Latitude")
    private BigDecimal latitude;

    @CsvBindByName(column = "Longitude")
    private BigDecimal longitude;

    @CsvBindByName(column = "Battery")
    private Double battery;

    @CsvBindByName(column = "Light")
    private Status light;

    @CsvBindByName(column = "AirplaneMode")
    private Status airplaneMode;

    @CsvBindByName(column = "DateTime")
    private Long dateTime;
}
