package com.exercise.hotelsdatamerge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class MostFrequentValueMergedHotel implements Hotel {
    private Map<String, Integer> idSStats = new HashMap<>();
    private Map<Integer, Integer> destinationIdStats= new HashMap<>();
    private Map<String, Integer> nameStats= new HashMap<>();

    private Map<Double, Integer> latStats= new HashMap<>();
    private Map<Double, Integer> lngStats= new HashMap<>();
    private Map<String, Integer> addressStats= new HashMap<>();
    private Map<String, Integer> cityStats= new HashMap<>();
    private Map<String , Integer> countryStats= new HashMap<>();

    private Map<String, Integer> descriptionStats= new HashMap<>();
    private Map<String, Map<List<String>, Integer>> amenitiesStats= new HashMap<>();
    private Map<String, Map<List<Image>, Integer>> imagesStats= new HashMap<>();
    private Map<List<String> , Integer> bookingConditionStats= new HashMap<>();



    @Override
    public void setId(String id) {
        setObject(idSStats, id);
    }

    @Override
    public String getId() {
        return (String) getObject(idSStats);
    }

    @Override
    public Integer getDestiationId() {
        return (Integer) getObject(destinationIdStats);
    }

    @Override
    public void setDestiationId(Integer destiationId) {
        setObject(destinationIdStats, destiationId);
    }


    @Override
    public void setLat(Double id) {
        setObject(latStats, id);
    }

    @Override
    @JsonIgnore
    public Double getLat() {
        return (Double) getObject(latStats);
    }

    @Override
    public void setLng(Double id) {
        setObject(lngStats, id);
    }

    @Override
    @JsonIgnore
    public Double getLng() {
        return (Double) getObject(lngStats);
    }

    @Override
    public void setAdress(String address) {
        setObject(addressStats, address);
    }

    @Override
    @JsonIgnore
    public String getAddress() {
        return (String) getObject(addressStats);
    }

    @Override
    public void setCity(String city) {
        setObject(cityStats, city);
    }

    @Override
    @JsonIgnore
    public String getCity() {
        return (String) getObject(cityStats);
    }

    @Override
    public void setCountry(String country) {
        setObject(countryStats, country);
    }
    @Override
    @JsonIgnore
    public String getCountry() {
        return (String) getObject(countryStats);
    }

    @Override
    public Location getLocation() {
        return new Location(getLat(), getLng(), getAddress(), getCity(), getCountry());
    }

    @Override
    public void setDescription(String description) {
        setObject(descriptionStats, description);
    }

    @Override
    public String getDescription() {
        return (String) getObject(descriptionStats);
    }

    @Override
    public void setAmenities(Amenity amenities) {
        if (!amenitiesStats.containsKey(amenities.getCategory())) {
            amenitiesStats.put(amenities.getCategory(), new HashMap<>());
        }
        setObject(amenitiesStats.get(amenities.getCategory()), amenities.getValues());
    }


    @Override
    @JsonIgnore
    public List<Amenity> getAmenities() {
        return amenitiesStats.entrySet().stream().map(entry -> {
            Amenity amenities = new Amenity();
            amenities.setCategory(entry.getKey());
            amenities.setValues((List<String>) getObject(entry.getValue()));
            return amenities;
        }).collect(Collectors.toList());
    }

    @Override
    @JsonProperty("amenities")
    public Map<String, List<String>> getAmenitiesAsMap() {
        return getAmenities().stream().collect(Collectors.toMap(Amenity::getCategory, Amenity::getValues));
    }

    @Override
    public void setImages(Image image) {
        if (!imagesStats.containsKey(image.getCategory())) {
            imagesStats.put(image.getCategory(), new HashMap<>());
        }
        setObject(imagesStats.get(image.getCategory()), Collections.singletonList(image));
    }


    @Override
    @JsonIgnore
    public List<Image> getImages() {
        return imagesStats.entrySet().stream().flatMap(entry -> {
            List<Image> images = (List<Image>) getObject(entry.getValue());
            return images.stream();
        }).collect(Collectors.toList());
    }

    @Override
    @JsonProperty("images")
    public Map<String, List<Image>> getImagesAsMap() {
        return getImages().stream().collect(Collectors.groupingBy(Image::getCategory));
    }

    @Override
    public void setBookingConditions(List<String> bookingConditions) {
        setObject(bookingConditionStats, bookingConditions);
    }

    @Override
    public List<String> getBookingConditions() {
        return (List<String>) getObject(bookingConditionStats);
    }

    @Override
    public String getName() {
        return (String) getObject(nameStats);
    }

    @Override
    public void setName(String name) {
        setObject(nameStats, name);
    }

    private <T extends  Object> void setObject(Map<T, Integer> stats, T value) {
        if (value != null) {
            if (!stats.containsKey(value)) {
                stats.put(value, 0);
            }
            stats.put(value, stats.get(value) + 1);
        }
    }

    private Object getObject(Map<? extends Object, Integer> stats) {
        Optional<? extends Map.Entry<?, Integer>> entryWithMaxOccurrence = stats.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1);
        if (entryWithMaxOccurrence.isPresent()) {
            return entryWithMaxOccurrence.get().getKey();
        }
        return null;
    }

}

