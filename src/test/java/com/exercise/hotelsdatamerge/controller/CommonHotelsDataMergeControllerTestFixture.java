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

class CommonHotelsDataMergeControllerTestFixture {
    public static final String FIRST_SOURCE_PATH = "/firstsource";
    public static final String SECOND_SOURCE_PATH = "/secondsource";
    public static final String THIRD_SOURCE_PATH = "/thirdsource";

    @LocalServerPort
    private Integer port;

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected ClientAndServer mockServer;
    protected MockServerClient mockClient;
    protected Hotel[] actualHotelsResponse;
    protected Hotel currentHotel;
    protected String filter;
    protected int expectedHotelLength = 1;

    @BeforeEach
    public void startServer() throws JsonProcessingException {
        mockServer = ClientAndServer.startClientAndServer(1080);

        mockClient = new MockServerClient("127.0.0.1", 1080);

        mockClient.reset();
        expectNumberOfRespondedHotel(1);

    }

    @AfterEach
    public void stopServer() {
        mockServer.stop();

    }


    protected void simulateConnectionReset(String path) {
        mockClient.when(
                HttpRequest.request().withMethod("GET")
                        .withPath(path)
                ,
                Times.unlimited()
        ).error(
                error()
                        .withDropConnection(true)
        );
    }

    protected void mockHotelDataSource(String path, Object reponseBody) {
        mockClient.when(
                HttpRequest.request().withMethod("GET")
                        .withPath(path)
                ,
                Times.unlimited()
        ).respond(HttpResponse.response()
                .withHeader("Content-Type", "application/json")
                .withStatusCode(200).withBody(stringValueOrNullIfError(reponseBody))
        );
    }

    protected String stringValueOrNullIfError(Object reponseBody) {
        try {
            return objectMapper.writeValueAsString(reponseBody);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    protected void expectNumberOfRespondedHotel(int num) {
        expectedHotelLength = num;
    }

    protected void setFilter(String inFilter) {
        filter = "?" + inFilter;
    }


    protected <T> Map<String, Set<T>> mergeMap(Map<String, Set<T>> first, Map<String, Set<T>> second) {
        first.putAll(second);
        return first;
    }



    protected FirstDataSourceHotelDto.FirstDataSourceHotelDtoBuilder firstDataSourceHotelDtoBuilder() {
        return firstDataSourceHotelDtoBuilder("hotel_id_1", 111);
    }

    protected SecondDataSourceHotelDto.SecondDataSourceHotelDtoBuilder secondDataSourceHotelDtoBuilder() {
        return secondDataSourceHotelDtoBuilder("hotel_id_1", 111);
    }


    protected ThirdDataSourceHotelDto.ThirdDataSourceHotelDtoBuilder thirdDataSourceHotelDtoBuilder () {
        return thirdDataSourceHotelDtoBuilder("hotel_id_1", 111);
    }

    protected FirstDataSourceHotelDto.FirstDataSourceHotelDtoBuilder firstDataSourceHotelDtoBuilder(String hotelId, int destinationId) {
        return FirstDataSourceHotelDto.builder()
                .id(hotelId)
                .destinationId(destinationId);
    }

    protected SecondDataSourceHotelDto.SecondDataSourceHotelDtoBuilder secondDataSourceHotelDtoBuilder(String hotelId, int destinationId) {
        return SecondDataSourceHotelDto.builder()
                .id(hotelId)
                .destinationId(destinationId);
    }


    protected ThirdDataSourceHotelDto.ThirdDataSourceHotelDtoBuilder thirdDataSourceHotelDtoBuilder (String hotelId, int destinationId) {
        return ThirdDataSourceHotelDto.builder()
                .id(hotelId)
                .destinationId(destinationId);
    }

    protected <T> Map<String, Set<T>> newMapOfSetValues(String key, Set<T> values) {
        Map<String, Set<T>> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.put(key, values);
        return map;
    }

    protected Set<String> newSetOfValues(String...values) {
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (String value : values) {
            set.add(value);
        }
        return set;
    }



    protected void getHotelsAPIIsCalled() {
        ExtractableResponse<Response> response = RestAssured
                .given()
                .filter(new RequestLoggingFilter())
                .contentType("application/json")
                .when()
                .get("http://localhost:" + port + "/api/hotels" + Optional.ofNullable(filter).orElse(""))
                .then()
                .statusCode(200)
                .extract();


        actualHotelsResponse = response.as(Hotel[].class);
    }

    protected void setCurrentHotel(int index) {
        currentHotel = actualHotelsResponse[index];
    }
}