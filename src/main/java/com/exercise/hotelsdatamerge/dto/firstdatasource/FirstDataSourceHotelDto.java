package com.exercise.hotelsdatamerge.dto.firstdatasource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class FirstDataSourceHotelDto {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("DestinationId")
    private int destinationId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Latitude")
    private double latitude;
    @JsonProperty("Longitude")
    private double longitude;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("City")
    private String city;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("PostalCode")
    private String postalCode;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Facilities")
    private List<String> facilities;
}
