package com.exercise.hotelsdatamerge.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "hotelsources")
@Data
public class HotelSourceUrl {
    private String first, second, third;
}
