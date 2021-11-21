package com.exercise.hotelsdatamerge.dto.seconddatasource;

import com.exercise.hotelsdatamerge.dto.Image;
import com.exercise.hotelsdatamerge.dto.Location;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecondDataSourceHotelDto {
    @JsonProperty("hotel_id")
    private String id;
    @JsonProperty("destination_id")
    private int destinationId;
    @JsonProperty("hotel_name")
    private String name;
    private Location location;
    @JsonProperty("details")
    private String description;
    private Map<String, Set<String>> amenities = new HashMap<>();
    private Map<String, Set<Image>> images;
    @JsonProperty("booking_conditions")
    public Set<String> bookingConditions;

    public String getAddress() {
        return location != null ? location.getAddress(): null;
    }


    public String getCountry() {
         return  location != null ? location.getCountry() : null;
    }

}




