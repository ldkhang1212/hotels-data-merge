package com.exercise.hotelsdatamerge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public interface Hotel {
    void setId(String id);

    String getId();

    Integer getDestiationId();

    void setDestiationId(Integer destiationId);

    void setLat(Double id);

    @JsonIgnore
    Double getLat();

    void setLng(Double id);

    @JsonIgnore
    Double getLng();

    void setAdress(String address);

    @JsonIgnore
    String getAddress();

    void setCity(String city);

    @JsonIgnore
    String getCity();

    void setCountry(String country);

    @JsonIgnore
    String getCountry();

    Location getLocation();

    void setDescription(String description);

    String getDescription();

    void setAmenities(Amenity amenities);

    @JsonIgnore
    List<Amenity> getAmenities();

    @JsonProperty("amenities")
    Map<String, List<String>> getAmenitiesAsMap();

    void setImages(Image image);

    @JsonIgnore
    List<Image> getImages();

    @JsonProperty("images")
    Map<String, List<Image>> getImagesAsMap();

    void setBookingConditions(List<String> bookingConditions);

    List<String> getBookingConditions();

    String getName();

    void setName(String name);
}
