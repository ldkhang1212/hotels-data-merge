package com.exercise.hotelsdatamerge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Hotel {
    private String id;
    private Integer destinationId;
    private Double lat;
    private Double lng;
    private String address;
    private String city;
    private String country;
    private String description;
    private String name;
    private Map<String, Set<String>> amenities = new HashMap<>();
    private Map<String, Set<Image>> images = new HashMap<>();
    private Set<String> bookingConditions = new HashSet<>();


    @JsonProperty
    public Location getLocation() {
        return new Location(getLat() != null ? getLat() : -1, getLng() != null ? getLng() : -1, getAddress(), getCity(), getCountry());
    }

    @JsonSetter
    public void setLocation(Location location) {
        if (location == null) {
            return;
        }
        setLat(location.getLat());
        setLng(location.getLng());
        setAddress(location.getAddress());
        setCity(location.getCity());
        setCountry(location.getCountry());
    }

}
