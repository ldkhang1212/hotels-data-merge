package com.exercise.hotelsdatamerge.dto.thirddatasource;

import com.exercise.hotelsdatamerge.dto.Image;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ThirdDataSourceHotelDto {
    private String id;
    private int destination;
    private String name;
    private double lat;
    private double lng;
    private String address;
    private String info;
    private List<String> amenities;
    private Map<String, List<Image>> images;
}
