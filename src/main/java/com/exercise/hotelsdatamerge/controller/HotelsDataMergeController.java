package com.exercise.hotelsdatamerge.controller;

import com.exercise.hotelsdatamerge.dto.Hotel;
import com.exercise.hotelsdatamerge.service.HotelDataMergeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Set;

@RestController
@RequestMapping("/api/hotels")
public class HotelsDataMergeController {

    @Autowired
    private HotelDataMergeService hotelDataMergeService;

    /**
     * Get hotel list which is the result of merging 3 different sources.
     * @param destinationId destination id
     * @param hotelIds hotel id list
     * @return
     */
    @GetMapping
    private Flux<Hotel> getHotels(@RequestParam(value = "destinationId", required = false) Integer destinationId,
                                  @RequestParam(required = false, value = "hotelId") Set<String> hotelIds) {
        return hotelDataMergeService.getHotels(destinationId, hotelIds);
    }
}
