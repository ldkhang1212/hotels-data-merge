package com.exercise.hotelsdatamerge.service;

import com.exercise.hotelsdatamerge.dto.*;
import com.exercise.hotelsdatamerge.dto.firstdatasource.FirstDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.seconddatasource.SecondDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.thirddatasource.ThirdDataSourceHotelDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

@Service
public class HotelDataMergeService {

    @Autowired
    private WebClientFactory webClientFactory;

    @Autowired
    private ModelMapper objectMapper;

    private Flux<Hotel> firstHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.FIRST).get().retrieve().bodyToFlux(FirstDataSourceHotelDto.class)
                .map(firstDataSourceHotelDto -> objectMapper.map(firstDataSourceHotelDto, Hotel.class));
    }
    private Flux<Hotel> secondHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.SECOND).get().retrieve().bodyToFlux(SecondDataSourceHotelDto.class)
                .map(secondDataSourceHotelDto -> objectMapper.map(secondDataSourceHotelDto, Hotel.class));
    }
    private Flux<Hotel> thirdHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.THIRD).get().retrieve().bodyToFlux(ThirdDataSourceHotelDto.class)
                .map(thirdDataSourceHotelDto -> objectMapper.map(thirdDataSourceHotelDto, Hotel.class));
    }

    /**
     * Get hotel list which is the result of merging 3 different sources.
     * @param destinationIds destination id list
     * @param hotelId hotel id
     * @return
     */
    public Flux<Hotel> getHotels(Set<Integer> destinationIds, String hotelId) {
        return Flux.merge(firstHotelDataSource(), secondHotelDataSource(), thirdHotelDataSource())
                .filter(destinationIdFilter(destinationIds))
                .filter(hotelIdFilter(hotelId))
                .publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.parallel())

                .groupBy(Hotel::getId)
                .flatMap(idFlux -> {
                    return idFlux.collectList().map(list -> {
                        // We can build other MergedHotel rules and changed here or injected via spring
                      return list.stream().reduce(new MostFrequentValueMergedHotel(), (current, next) -> {
                          objectMapper.map(next, current);
                          return current;
                      });
                    });
                }).sort((o1,o2) -> o1.getId().compareTo(o2.getId()));
    }

    private Predicate<Hotel> destinationIdFilter(Set<Integer> destinationIds) {
        return dto -> CollectionUtils.isEmpty(destinationIds) || destinationIds.contains(dto.getDestinationId());
    }

    private Predicate<Hotel> hotelIdFilter(String hotelIdFilter) {
        return dto -> !StringUtils.hasText(hotelIdFilter) || hotelIdFilter.equals(dto.getId());
    }

}
