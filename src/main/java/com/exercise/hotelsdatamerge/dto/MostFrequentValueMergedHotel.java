package com.exercise.hotelsdatamerge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.source.tree.Tree;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class MostFrequentValueMergedHotel extends Hotel {
    private Map<String, Integer> idSStats = new TreeMap<>();
    private Map<Integer, Integer> destinationIdStats= new TreeMap<>();
    private Map<String, Integer> nameStats= new TreeMap<>();

    private Map<Double, Integer> latStats= new TreeMap<>();
    private Map<Double, Integer> lngStats= new TreeMap<>();
    private Map<String, Integer> addressStats= new TreeMap<>();
    private Map<String, Integer> cityStats= new TreeMap<>();
    private Map<String , Integer> countryStats= new TreeMap<>();

    private Map<String, Integer> descriptionStats= new TreeMap<>();

    private Map<String, Set<String>> amenitiesStats =  new HashMap<>();
    private Map<String, Set<Image>> imagesStats = new TreeMap<>();
    private Set<String> bookingConditionStats= new TreeSet<>();



    @Override
    public void setId(String id) {
        setObject(idSStats, id);
    }

    @Override
    public String getId() {
        return (String) getObject(idSStats);
    }

    @Override
    public Integer getDestinationId() {
        return (Integer) getObject(destinationIdStats);
    }

    @Override
    public void setDestinationId(Integer destiationId) {
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
    public void setAmenities(Map<String, Set<String>> inAmenities) {
        if (!CollectionUtils.isEmpty(inAmenities)) {
            inAmenities.entrySet().forEach(entry -> {
                if (!amenitiesStats.containsKey(entry.getKey())) {
                    amenitiesStats.put(entry.getKey(), new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
                }
                amenitiesStats.get(entry.getKey()).addAll(entry.getValue());
            });
        }


    }

    @Override
    public void setAddress(String address) {
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
    public void setImages(Map<String, Set<Image>> inImages) {
        if (!CollectionUtils.isEmpty(inImages)) {
            inImages.entrySet().forEach(entry -> {
                String category = entry.getKey();
                Set<Image> images = entry.getValue();
                if (!imagesStats.containsKey(category)) {
                    imagesStats.put(category, new TreeSet<>());
                }
                imagesStats.get(category).addAll(images);
            });
        }

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
    public void setLocation(Location location) {
        super.setLocation(location);
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
    public Map<String, Set<String>> getAmenities() {
        return new HashMap<>(amenitiesStats);
    }

    @Override
    public Map<String, Set<Image>> getImages() {
        return new TreeMap<>(imagesStats);
    }

    @Override
    public void setBookingConditions(Set<String> bookingConditions) {
        if (!CollectionUtils.isEmpty(bookingConditions)) {
            bookingConditionStats.addAll(bookingConditions);
        }
    }

    @Override
    public Set<String> getBookingConditions() {
        return new TreeSet<>(bookingConditionStats);
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

        if (stats.isEmpty()) {
            return null;
        }

        Optional<? extends Map.Entry<?, Integer>> entryWithMaxOccurrence = stats.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1);
        if (entryWithMaxOccurrence.isPresent()) {
            return entryWithMaxOccurrence.get().getKey();
        }
        return null;
    }

}

