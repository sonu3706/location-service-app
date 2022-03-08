package com.vodofone.demo.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponse {
    private String id;
    private String name;
    private String status;
    private String battery;
    private String description;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Date dateTime;
}
