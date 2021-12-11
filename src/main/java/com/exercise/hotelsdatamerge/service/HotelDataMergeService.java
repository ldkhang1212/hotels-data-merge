package com.exercise.hotelsdatamerge.service;

import com.exercise.hotelsdatamerge.dto.*;
import com.exercise.hotelsdatamerge.dto.firstdatasource.FirstDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.seconddatasource.SecondDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.thirddatasource.ThirdDataSourceHotelDto;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.modelmapper.ModelMapper;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class HotelDataMergeService {

    public static final Function<Throwable, Publisher<? extends Hotel>> FALLBACK = new Function<>() {
        @Override
        public Publisher<? extends Hotel> apply(Throwable throwable) {
            // Possibly logging out the exception
            return Flux.empty();
        }
    };
    public static final Consumer<Signal<Hotel>> ITEM_LOGGER = each ->  System.out.println("Processing threadId=" + Thread.currentThread().getId());
    @Autowired
    private WebClientFactory webClientFactory;

    @Autowired
    private ModelMapper objectMapper;

    @Value("${cacheEnabled}")
    private boolean cacheEnabled;

    private Flux<Hotel> cacheableHotelFlux;

    @PostConstruct
    public void init() {
        cacheableHotelFlux = Flux.merge(firstHotelDataSource(), secondHotelDataSource(), thirdHotelDataSource())
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

        if (cacheEnabled) {
            cacheableHotelFlux = cacheableHotelFlux.cache(Duration.ofMinutes(5));
        }
    }



    private Flux<Hotel> firstHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.FIRST).get().retrieve().bodyToFlux(FirstDataSourceHotelDto.class)
                .map(firstDataSourceHotelDto -> objectMapper.map(firstDataSourceHotelDto, Hotel.class)).onErrorResume(FALLBACK).doOnEach(ITEM_LOGGER);
    }
    private Flux<Hotel> secondHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.SECOND).get().retrieve().bodyToFlux(SecondDataSourceHotelDto.class)
                .map(secondDataSourceHotelDto -> objectMapper.map(secondDataSourceHotelDto, Hotel.class)).onErrorResume(FALLBACK).doOnEach(ITEM_LOGGER);
    }
    private Flux<Hotel> thirdHotelDataSource () {
        return webClientFactory.getWebClientFor(HotelSource.THIRD).get().retrieve().bodyToFlux(ThirdDataSourceHotelDto.class)
                .map(thirdDataSourceHotelDto -> objectMapper.map(thirdDataSourceHotelDto, Hotel.class)).onErrorResume(FALLBACK).doOnEach(ITEM_LOGGER);
    }

    /**
     * Get hotel list which is the result of merging 3 different sources.
     * @param destinationId destination id
     * @param hotelIds hotel id list
     * @return
     */
    public Flux<Hotel> getHotels(Integer destinationId, Set<String> hotelIds) {
        return cacheableHotelFlux
                .filter(destinationIdFilter(destinationId))
                .filter(hotelIdFilter(hotelIds));
    }


    private Predicate<Hotel> destinationIdFilter(Integer destinationId) {
        return dto -> destinationId == null || destinationId.equals(dto.getDestinationId());
    }

    private Predicate<Hotel> hotelIdFilter(Set<String> hotelIds) {
        return dto -> CollectionUtils.isEmpty(hotelIds) || hotelIds.contains(dto.getId());
    }

}
