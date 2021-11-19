package com.exercise.hotelsdatamerge.service;

import com.exercise.hotelsdatamerge.converter.FirstDataSourceHotelDtoToStatisticConverter;
import com.exercise.hotelsdatamerge.converter.SecondDataSourceHotelDtoToStatisticConverter;
import com.exercise.hotelsdatamerge.converter.ThirdDataSourceHotelDtoToStatisticConverter;
import com.exercise.hotelsdatamerge.dto.*;
import com.exercise.hotelsdatamerge.dto.firstdatasource.FirstDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.seconddatasource.SecondDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.thirddatasource.ThirdDataSourceHotelDto;
import com.exercise.hotelsdatamerge.reducer.MergedHotelReducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Set;
import java.util.function.Predicate;

@Service
public class HotelDataMergeService {

    @Autowired
    private WebClientFactory webClientFactory;

    @Autowired
    private FirstDataSourceHotelDtoToStatisticConverter firstDataSourceHotelDtoToStatisticConverter;

    @Autowired
    private SecondDataSourceHotelDtoToStatisticConverter secondDataSourceHotelDtoToStatisticConverter;

    @Autowired
    private ThirdDataSourceHotelDtoToStatisticConverter thirdDataSourceHotelDtoToStatisticConverter;

    @Autowired
    private MergedHotelReducer mergedHotelReducer;

    private Flux<Hotel> firstHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.FIRST).get().retrieve().bodyToFlux(FirstDataSourceHotelDto.class).map(firstDataSourceHotelDtoToStatisticConverter);
    }
    private Flux<Hotel> secondHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.SECOND).get().retrieve().bodyToFlux(SecondDataSourceHotelDto.class).map(secondDataSourceHotelDtoToStatisticConverter);
    }
    private Flux<Hotel> thirdHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.THIRD).get().retrieve().bodyToFlux(ThirdDataSourceHotelDto.class).map(thirdDataSourceHotelDtoToStatisticConverter);
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
                      return list.stream().reduce(new MostFrequentValueMergedHotel(), mergedHotelReducer);
                    });
                });
    }

    private Predicate<Hotel> destinationIdFilter(Set<Integer> destinationIds) {
        return dto -> CollectionUtils.isEmpty(destinationIds) || destinationIds.contains(dto.getDestiationId());
    }

    private Predicate<Hotel> hotelIdFilter(String hotelIdFilter) {
        return dto -> !StringUtils.hasText(hotelIdFilter) || hotelIdFilter.equals(dto.getId());
    }

}
