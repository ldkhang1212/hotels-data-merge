package com.exercise.hotelsdatamerge.dto.thirddatasource;

import com.exercise.hotelsdatamerge.dto.Image;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdDataSourceHotelDto {
    private String id;
    @JsonProperty("destination")
    private int destinationId;
    private String name;
    private double lat;
    private double lng;
    private String address;
    @JsonProperty("info")
    private String description;
    @JsonProperty
    private Set<String> amenities = new HashSet<>();
    private Map<String, Set<Image>> images = new HashMap<>();


    @JsonIgnore
    public Map<String, Set<String>> getAmenities() {
        if (amenities != null && !amenities.isEmpty()) {
            Map<String, Set<String>> amenitiesMap = new HashMap<>();
            amenitiesMap.put("general", amenities);
            return amenitiesMap;
        }

        return new HashMap<>();
    }
}
