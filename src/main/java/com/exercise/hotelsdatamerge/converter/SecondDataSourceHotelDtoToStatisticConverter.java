package com.exercise.hotelsdatamerge.converter;

import com.exercise.hotelsdatamerge.dto.Amenity;
import com.exercise.hotelsdatamerge.dto.MostFrequentValueMergedHotel;
import com.exercise.hotelsdatamerge.dto.seconddatasource.SecondDataSourceHotelDto;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SecondDataSourceHotelDtoToStatisticConverter implements Function<SecondDataSourceHotelDto, MostFrequentValueMergedHotel> {

    @Override
    public MostFrequentValueMergedHotel apply(SecondDataSourceHotelDto secondDataSourceHotelDto) {
        MostFrequentValueMergedHotel hotelStatisticDto = new MostFrequentValueMergedHotel();
        hotelStatisticDto.setId(secondDataSourceHotelDto.getHotelId());
        hotelStatisticDto.setName(secondDataSourceHotelDto.getHotelName());
        hotelStatisticDto.setDestiationId(secondDataSourceHotelDto.getDestinationId());
        hotelStatisticDto.setAdress(secondDataSourceHotelDto.getAddress());
        hotelStatisticDto.setCountry(secondDataSourceHotelDto.getCountry());
        hotelStatisticDto.setDescription(secondDataSourceHotelDto.getDetails());
        secondDataSourceHotelDto.getAmenities()
                .entrySet().stream()
                .map(entry -> new Amenity(entry.getKey(), entry.getValue()))
                .forEach(amenities -> hotelStatisticDto.setAmenities(amenities));

        secondDataSourceHotelDto.getImages().entrySet().stream()
                .flatMap(entry -> {
                    return entry.getValue().stream().map(image -> {
                        image.setCategory(entry.getKey());
                        return image;
                    });
                }).forEach(image -> hotelStatisticDto.setImages(image));

        hotelStatisticDto.setBookingConditions(secondDataSourceHotelDto.getBookingConditions());

        return hotelStatisticDto;
    }
}
