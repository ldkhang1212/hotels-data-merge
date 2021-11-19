package com.exercise.hotelsdatamerge.dto.seconddatasource;

import com.exercise.hotelsdatamerge.dto.Image;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SecondDataSourceHotelDto {
    @JsonProperty("hotel_id")
    private String hotelId;
    @JsonProperty("destination_id")
    private int destinationId;
    @JsonProperty("hotel_name")
    private String hotelName;
    private Location location;
    private String details;
    private Map<String, List<String>> amenities;
    private Map<String, List<Image>> images;
    @JsonProperty("booking_conditions")
    public List<String> bookingConditions;

    public String getAddress() {
        return location.getAddress();
    }


    public String getCountry() {
        return location.getCountry();
    }

}
@Data
class Location{
    private String address;
    private String country;
}





