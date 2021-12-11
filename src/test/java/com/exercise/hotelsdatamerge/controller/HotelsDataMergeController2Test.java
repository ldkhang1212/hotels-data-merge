package com.exercise.hotelsdatamerge.controller;


import com.exercise.hotelsdatamerge.dto.Hotel;
import com.exercise.hotelsdatamerge.dto.Image;
import com.exercise.hotelsdatamerge.dto.Location;
import com.exercise.hotelsdatamerge.dto.firstdatasource.FirstDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.seconddatasource.SecondDataSourceHotelDto;
import com.exercise.hotelsdatamerge.dto.thirddatasource.ThirdDataSourceHotelDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.*;
import java.util.function.Consumer;

import static org.mockserver.model.HttpError.error;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.security.user.name=duke",
                "spring.security.user.password=secret",
                "spring.security.user.roles=ADMIN",
                "hotelsources.first=localhost:1080/firstsource",
                "hotelsources.second=localhost:1080/secondsource",
                "hotelsources.third=localhost:1080/thirdsource",
                "cacheEnabled=true"
        })
class HotelsDataMergeController2Test extends CommonHotelsDataMergeControllerTestFixture {

    @Test
    public void getHotels_callApiTwiceWithCacheEnabled_cacheReused() {
        given: mockHotelDataSource(FIRST_SOURCE_PATH, firstDataSourceHotelDtoBuilder().name("name 1").build());

        and: mockHotelDataSource(SECOND_SOURCE_PATH, secondDataSourceHotelDtoBuilder().build());

        and: mockHotelDataSource(THIRD_SOURCE_PATH, thirdDataSourceHotelDtoBuilder().build());


        when: getHotelsAPIIsCalled();
        and: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);

        and: Assert.assertEquals("name 1", currentHotel.getName());

        when: getHotelsAPIIsCalled();

        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(FIRST_SOURCE_PATH), VerificationTimes.exactly(1));
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(SECOND_SOURCE_PATH), VerificationTimes.exactly(1));
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(THIRD_SOURCE_PATH), VerificationTimes.exactly(1));

    }

}