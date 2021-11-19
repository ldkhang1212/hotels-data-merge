package com.exercise.hotelsdatamerge.reducer;

import com.exercise.hotelsdatamerge.dto.Hotel;
import org.springframework.stereotype.Service;

import java.util.function.BinaryOperator;

@Service
public class MergedHotelReducer  implements BinaryOperator<Hotel> {

    @Override
    public Hotel apply(Hotel current, Hotel next) {
        current.setId(next.getId());
        current.setName(next.getName());
        current.setDestiationId(next.getDestiationId());
        current.setLat(next.getLat());
        current.setLng(next.getLng());
        current.setAdress(next.getAddress());
        current.setCity(next.getCity());
        current.setCountry(next.getCountry());
        current.setDescription(next.getDescription());
        next.getAmenities().stream().forEach(amenities -> current.setAmenities(amenities));
        next.getImages().stream().forEach(image -> current.setImages(image));
        current.setBookingConditions(next.getBookingConditions());
        return current;
    }
}
