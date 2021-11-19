package com.exercise.hotelsdatamerge.converter;

import com.exercise.hotelsdatamerge.dto.Amenity;
import com.exercise.hotelsdatamerge.dto.MostFrequentValueMergedHotel;
import com.exercise.hotelsdatamerge.dto.thirddatasource.ThirdDataSourceHotelDto;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ThirdDataSourceHotelDtoToStatisticConverter implements Function<ThirdDataSourceHotelDto, MostFrequentValueMergedHotel> {

    @Override
    public MostFrequentValueMergedHotel apply(ThirdDataSourceHotelDto thirdDataSourceHotelDto) {
        MostFrequentValueMergedHotel hotelStatisticDto = new MostFrequentValueMergedHotel();
        hotelStatisticDto.setId(thirdDataSourceHotelDto.getId());
        hotelStatisticDto.setName(thirdDataSourceHotelDto.getName());
        hotelStatisticDto.setDestiationId(thirdDataSourceHotelDto.getDestination());

        hotelStatisticDto.setLat(thirdDataSourceHotelDto.getLat());
        hotelStatisticDto.setLng(thirdDataSourceHotelDto.getLng());
        hotelStatisticDto.setAdress(thirdDataSourceHotelDto.getAddress());

        hotelStatisticDto.setDescription(thirdDataSourceHotelDto.getInfo());
        hotelStatisticDto.setAmenities(new Amenity("general", thirdDataSourceHotelDto.getAmenities()));
        thirdDataSourceHotelDto.getImages().entrySet().stream()
                .flatMap(entry -> {
                    return entry.getValue().stream().map(image -> {
                        image.setCategory(entry.getKey());
                        return image;
                    });
                }).forEach(image -> hotelStatisticDto.setImages(image));
        return hotelStatisticDto;
    }
}
