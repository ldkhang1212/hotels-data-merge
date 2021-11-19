package com.exercise.hotelsdatamerge.converter;

import com.exercise.hotelsdatamerge.dto.Amenity;
import com.exercise.hotelsdatamerge.dto.MostFrequentValueMergedHotel;
import com.exercise.hotelsdatamerge.dto.firstdatasource.FirstDataSourceHotelDto;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 *
 */
@Service
public class FirstDataSourceHotelDtoToStatisticConverter implements Function<FirstDataSourceHotelDto, MostFrequentValueMergedHotel> {


    @Override
    public MostFrequentValueMergedHotel apply(FirstDataSourceHotelDto firstDataSourceHotelDto) {
        MostFrequentValueMergedHotel hotelStatisticDto = new MostFrequentValueMergedHotel();
        hotelStatisticDto.setId(firstDataSourceHotelDto.getId());
        hotelStatisticDto.setName(firstDataSourceHotelDto.getName());
        hotelStatisticDto.setDestiationId(firstDataSourceHotelDto.getDestinationId());
        hotelStatisticDto.setLat(firstDataSourceHotelDto.getLatitude());
        hotelStatisticDto.setLng(firstDataSourceHotelDto.getLongitude());
        hotelStatisticDto.setAdress(firstDataSourceHotelDto.getAddress());
        hotelStatisticDto.setCity(firstDataSourceHotelDto.getCity());
        hotelStatisticDto.setCountry(firstDataSourceHotelDto.getCountry());
        hotelStatisticDto.setDescription(firstDataSourceHotelDto.getDescription());
        hotelStatisticDto.setAmenities(new Amenity("general", firstDataSourceHotelDto.getFacilities()));
        return hotelStatisticDto;
    }
}
