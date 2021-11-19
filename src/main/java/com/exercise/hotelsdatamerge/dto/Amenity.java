package com.exercise.hotelsdatamerge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amenity {
    private String category;
    private List<String> values;

}
