package com.exercise.hotelsdatamerge.dto.firstdatasource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirstDataSourceHotelDto {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("DestinationId")
    private int destinationId;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Latitude")
    private double lat;
    @JsonProperty("Longitude")
    private double lng;
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
    private Set<String> facilities = new HashSet<>();

    public Map<String, Set<String>> getAmenities() {
        if (facilities != null && !facilities.isEmpty()) {
            Map<String, Set<String>> amenities = new HashMap<>();
            amenities.put("general", facilities);
            return amenities;
        }
        return new HashMap<>();
    }


}
