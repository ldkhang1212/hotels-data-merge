package com.exercise.hotelsdatamerge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Service
public class WebClientFactory {
    @Autowired
    private HotelSourceUrl hotelSourceUrl;
    private WebClient firstWebClient, secondWebClient, thirdWebClient;

    @PostConstruct
    public void init() {
        firstWebClient =  WebClient.create(hotelSourceUrl.getFirst());
        secondWebClient =  WebClient.create(hotelSourceUrl.getSecond());
        thirdWebClient =  WebClient.create(hotelSourceUrl.getThird());
    }

    public WebClient getWebClientFor(HotelSource hotelSource) {
        switch (hotelSource) {
            case FIRST -> {
                return firstWebClient;
            }
            case SECOND -> {
                return secondWebClient;
            }
            case THIRD -> {
                return thirdWebClient;
            }
        }
        return null;
    }
}
