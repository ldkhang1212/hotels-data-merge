package com.exercise.hotelsdatamerge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public
class Location{
    private double lat;
    private double lng;
    private String address;
    private String city;
    private String country;
}
