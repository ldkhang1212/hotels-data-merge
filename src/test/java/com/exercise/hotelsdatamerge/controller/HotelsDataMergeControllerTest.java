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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.response.MockRestResponseCreators;

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
                "cacheEnabled=false"
        })
class HotelsDataMergeControllerTest extends CommonHotelsDataMergeControllerTestFixture{




    @Test
    void getHotels_mostFrequentNameIsUsed() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .name("frequent name"),
                secondDataSourceHotelDtoBuilder()
                        .name("infrequent name"),
               thirdDataSourceHotelDtoBuilder()
                        .name("frequent name"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("frequent name", hotel.getName());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentNameIsUsed_3namesAreDifferent_useLastSortedByAlphabetSource() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .name("first name"),
                secondDataSourceHotelDtoBuilder()
                        .name("z second name"),
                thirdDataSourceHotelDtoBuilder()
                        .name("a third name"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("z second name", hotel.getName());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentDescIsUsed() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .description("infrequent description"),
                secondDataSourceHotelDtoBuilder()
                        .description("frequent description"),
                thirdDataSourceHotelDtoBuilder()
                        .description("frequent description"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("frequent description", hotel.getDescription());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentDescIsUsed_3DescsAreDifferent_useLastSortedByAlphabetAscSource() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .description("first description"),
                secondDataSourceHotelDtoBuilder()
                        .description("second description"),
                thirdDataSourceHotelDtoBuilder()
                        .description("third description"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("third description", hotel.getDescription());
                    }
                });

    }


    @Test
    void getHotels_mostFrequentLatIsUsed() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .lat(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder()
                        .lat(123),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(123, hotel.getLocation().getLat(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLatIsUsed_0ValueNotCountedInStats() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .lat(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder()
                        .lat(0),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(123, hotel.getLocation().getLat(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLatIsUse_noMatchedHotelToProvideLat() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .lat(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .lat(123),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(-1, hotel.getLocation().getLat(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLatIsUsed_3DescsAreDifferent_useLastSortedAscSource() {


        runTest(firstDataSourceHotelDtoBuilder()
                        .lat(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder()
                        .lat(234),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        Assert.assertEquals(234, hotel.getLocation().getLat(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLngIsUsed() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .lng(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder()
                        .lng(123),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(123, hotel.getLocation().getLng(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLngIsUsed_0ValueIsNotCountedInStats() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .lng(0),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder()
                        .lng(123),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(123, hotel.getLocation().getLng(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLngIsUsed_noMatchedHotelToProvideLng() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .lng(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .lng(123),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(-1, hotel.getLocation().getLng(), 0);
                    }
                });

    }

    @Test
    void getHotels_mostFrequentLngIsUsed_3DescsAreDifferent_useLastSortedAscSource() {


        runTest(firstDataSourceHotelDtoBuilder()
                        .lng(123),
                secondDataSourceHotelDtoBuilder(),
                thirdDataSourceHotelDtoBuilder()
                        .lng(234),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        Assert.assertEquals(234, hotel.getLocation().getLng(), 0);
                    }
                });

    }


    @Test
    void getHotels_mostFrequentCityIsUsed_useLastSortedByAlphabetAscSource() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .city("n city"),
                secondDataSourceHotelDtoBuilder().location(new Location(-1, -1, "address", "z city", "country")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("z city", hotel.getLocation().getCity());
                    }
                });

    }


    @Test
    void getHotels_mostFrequentCityIsUsed_useLastSortedByAlphabetAscSource_noMatchedHotelToProvideCity() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .city("n city"),
                secondDataSourceHotelDtoBuilder("hotel_id_2", 111).location(new Location(-1, -1, "address", "z city", "country")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(null, hotel.getLocation().getCity());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentAddressIsUsed_useLastSortedByAlphabetAscSource() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .address("v address"),
                secondDataSourceHotelDtoBuilder().location(new Location(-1, -1, null, "z city", "country")),
                thirdDataSourceHotelDtoBuilder().address("a address"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("v address", hotel.getLocation().getAddress());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentAddressIsUsed_useLastSortedByAlphabetAscSource_noMatchedHotelToProvideAddress() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .address("v address"),
                secondDataSourceHotelDtoBuilder().location(new Location(-1, -1, null, "z city", "country")),
                thirdDataSourceHotelDtoBuilder("hotel_id_2", 111).address("a address"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(null, hotel.getLocation().getAddress());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentCountryIsUsed_useLastSortedByAlphabetAscSource() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .country("z country"),
                secondDataSourceHotelDtoBuilder().location(new Location(-1, -1, null, "z city", "a country")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("z country", hotel.getLocation().getCountry());
                    }
                });


    }

    @Test
    void getHotels_mostFrequentCountryIsUsed_useLastSortedByAlphabetAscSource_noMatchedHotelToProvideCountry() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .country("z country"),
                secondDataSourceHotelDtoBuilder("hotel_id_2", 111).location(new Location(-1, -1, null, "z city", "a country")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(null, hotel.getLocation().getCountry());
                    }
                });

    }


    @Test
    void getHotels_mostFrequentCountryIsUsed_2CountriesAreTheSame() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .address("VN country"),
                secondDataSourceHotelDtoBuilder().location(new Location(-1, -1, null, "z city", "VN country")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("VN country", hotel.getLocation().getCountry());
                    }
                });

    }

    @Test
    void getHotels_allAmenitiesAreUsedAndSortedByAlphabetAsc() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .facilities(Set.of("Pool")),
                secondDataSourceHotelDtoBuilder().amenities(newMapOfSetValues("general", Set.of("Wifi", "Meeting"))),
                thirdDataSourceHotelDtoBuilder().amenities(Set.of("Gym", "Parking")),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(newMapOfSetValues("general", newSetOfValues("Gym", "Meeting","Parking", "Pool", "Wifi")), hotel.getAmenities());
                    }
                });

    }

    @Test
    void getHotels_allAmenitiesAreUsedAndSortedByAlphabetAsc_noMatchCountryToProvideAmenities() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .facilities(Set.of("Pool")),
                secondDataSourceHotelDtoBuilder("hotel_id_2", 111).amenities(newMapOfSetValues("general", Set.of("Wifi", "Meeting"))),
                thirdDataSourceHotelDtoBuilder().amenities(null),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(Collections.emptyMap(), hotel.getAmenities());
                    }
                });




    }

    @Test
    void getHotels_allAmenitiesAreUsedAndSortedByAlphabetAsc_deduplicateWordsAppendedWithoutSpace() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .facilities(Set.of("Pool")),
                secondDataSourceHotelDtoBuilder().amenities(newMapOfSetValues("general", Set.of("Business center", "businessCenter"))),
                thirdDataSourceHotelDtoBuilder().amenities(Set.of(" BusinessCenter","BusinessCenter ", "Parking")),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(newMapOfSetValues("general", newSetOfValues("Business Center", "Parking", "Pool")), hotel.getAmenities());
                    }
                });

    }

    @Test
    void getHotels_allAmenitiesAreUsedAndSortedByAlphabetAsc_spaceTrimmedAround() {

        runTest(firstDataSourceHotelDtoBuilder()
                        .facilities(Set.of("Pool", "Business center " )),
                secondDataSourceHotelDtoBuilder().amenities(newMapOfSetValues("general", Set.of(" Business center "))),
                thirdDataSourceHotelDtoBuilder().amenities(Set.of("Parking"," Business center")),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(newMapOfSetValues("general", newSetOfValues("Business center", "Parking", "Pool")), hotel.getAmenities());
                    }
                });

    }

    @Test
    void getHotels_allImagesAreUsedAndSortedByImageDescAlphabetAsc() {

        runTest(firstDataSourceHotelDtoBuilder(),
                secondDataSourceHotelDtoBuilder().images(
                        mergeMap(
                                newMapOfSetValues("general",
                                        Set.of(new Image("http://site.com/wc.png", "wc"), new Image("http://site.com/bedroom.png", "bedroom"))
                                ),
                                newMapOfSetValues("anothercat",
                                        Set.of(new Image("http://anothercat.com/image.png", "image"))
                                ))),
                thirdDataSourceHotelDtoBuilder().images(newMapOfSetValues("general", Set.of(new Image("http://site.com/kitchen.png", "kitchen"), new Image("http://site.com/wc.png", "wc")))),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and:
                        Assert.assertEquals(
                                mergeMap(
                                        newMapOfSetValues("anothercat",
                                                Set.of(new Image("http://anothercat.com/image.png", "image"))
                                        ),
                                        newMapOfSetValues("general",
                                                Set.of(
                                                        new Image("http://site.com/bedroom.png", "bedroom"),
                                                        new Image("http://site.com/kitchen.png", "kitchen"),
                                                        new Image("http://site.com/wc.png", "wc"))
                                        )
                                ), hotel.getImages()
                        );
                    }
                });

    }


    @Test
    void getHotels_allImagesAreUsedAndSortedByImageDescAlphabetAsc_noMatachedHotelToProvideImages() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder(),
                secondDataSourceHotelDtoBuilder("hotel_id_2", 111).
                        images( newMapOfSetValues("general",
                        Set.of(new Image("http://site.com/wc.png", "wc"), new Image("http://site.com/bedroom.png", "bedroom"))
                )),
                thirdDataSourceHotelDtoBuilder().images(Collections.emptyMap()),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and:
                        Assert.assertEquals( Collections.emptyMap(), hotel.getImages()
                        );
                    }
                });

    }


    @Test
    void getHotels_allBookingConditionAreUsedAndSortedByAlphabetAsc() {
        expectNumberOfRespondedHotel(2);
        runTest(firstDataSourceHotelDtoBuilder(),
                secondDataSourceHotelDtoBuilder("hotel_id_2", 111).bookingConditions(Set.of("rule 1", "another rule 2")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(Collections.emptySet(), hotel.getBookingConditions());
                    }
                });

    }


    @Test
    void getHotels_allBookingConditionAreUsedAndSortedByAlphabetAsc_noMatachedHotelToProvideBookingConditions() {

        runTest(firstDataSourceHotelDtoBuilder(),
                secondDataSourceHotelDtoBuilder().bookingConditions(Set.of("rule 1", "another rule 2")),
                thirdDataSourceHotelDtoBuilder(),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals(Set.of("another rule 2", "rule 1"), hotel.getBookingConditions());
                    }
                });

    }

    @Test
    void getHotels_mostFrequentNameIsUsed_filterByHotelId() {
        setFilter("hotelId=hotel_id_2");
        expectNumberOfRespondedHotel(1);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .name("name 2"),
                secondDataSourceHotelDtoBuilder()
                        .name("name 1"),
                thirdDataSourceHotelDtoBuilder()
                        .name("name 1"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("name 2", hotel.getName());
                    }
                });
    }


    @Test
    void getHotels_mostFrequentNameIsUsed_filterByDestinationId() {
        setFilter("destinationId=222");
        expectNumberOfRespondedHotel(1);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 222)
                        .name("name 2"),
                secondDataSourceHotelDtoBuilder()
                        .name("name 1"),
                thirdDataSourceHotelDtoBuilder()
                        .name("name 1"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("name 2", hotel.getName());
                    }
                });
    }

    @Test
    void getHotels_mostFrequentNameIsUsed_filterByHotelIdAndDestinationId() {
        setFilter("hotelId=hotel_id_2&destinationId=222");
        expectNumberOfRespondedHotel(1);
        runTest(firstDataSourceHotelDtoBuilder("hotel_id_2", 111)
                        .name("name 2"),
                secondDataSourceHotelDtoBuilder("hotel_id_3", 222)
                        .name("name 3"),
                thirdDataSourceHotelDtoBuilder("hotel_id_2", 222)
                        .name("name 3"),
                new Consumer<Hotel>() {
                    @Override
                    public void accept(Hotel hotel) {
                        and: Assert.assertEquals("name 3", hotel.getName());
                    }
                });
    }

    @Test
    public void getHotels_firstSourceFailed_apiResilientTest() {
        given: simulateConnectionReset(FIRST_SOURCE_PATH);

        and: mockHotelDataSource(SECOND_SOURCE_PATH, secondDataSourceHotelDtoBuilder().name("name 2").build());

        and: mockHotelDataSource(THIRD_SOURCE_PATH, thirdDataSourceHotelDtoBuilder().build());


        when: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);

        and: Assert.assertEquals("name 2", currentHotel.getName());

    }

    @Test
    public void getHotels_secondSourceFailed_apiResilientTest() {
        given: mockHotelDataSource(FIRST_SOURCE_PATH, firstDataSourceHotelDtoBuilder().name("name 1").build());

        and: simulateConnectionReset(SECOND_SOURCE_PATH);

        and: mockHotelDataSource(THIRD_SOURCE_PATH, thirdDataSourceHotelDtoBuilder().build());


        when: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);

        and: Assert.assertEquals("name 1", currentHotel.getName());

    }

    @Test
    public void getHotels_thirdSourceFailed_apiResilientTest() {
        given: mockHotelDataSource(FIRST_SOURCE_PATH, firstDataSourceHotelDtoBuilder().name("name 1").build());

        and: mockHotelDataSource(SECOND_SOURCE_PATH, secondDataSourceHotelDtoBuilder().build());

        and: simulateConnectionReset(THIRD_SOURCE_PATH);


        when: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);

        and: Assert.assertEquals("name 1", currentHotel.getName());

    }


    @Test
    public void getHotels_onlyOneSourceIsAvailable_apiResilientTest() {
        given: mockHotelDataSource(FIRST_SOURCE_PATH, firstDataSourceHotelDtoBuilder().name("name 1").build());

        and: simulateConnectionReset(SECOND_SOURCE_PATH);

        and: simulateConnectionReset(THIRD_SOURCE_PATH);


        when: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);

        and: Assert.assertEquals("name 1", currentHotel.getName());

    }

    @Test
    public void getHotels_callApiTwiceWithCacheDisabled_cacheNotReused() {
        given: mockHotelDataSource(FIRST_SOURCE_PATH, firstDataSourceHotelDtoBuilder().name("name 1").build());

        and: mockHotelDataSource(SECOND_SOURCE_PATH, secondDataSourceHotelDtoBuilder().build());

        and: mockHotelDataSource(THIRD_SOURCE_PATH, thirdDataSourceHotelDtoBuilder().build());


        when: getHotelsAPIIsCalled();
        and: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);

        and: Assert.assertEquals("name 1", currentHotel.getName());

        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(FIRST_SOURCE_PATH), VerificationTimes.exactly(2));
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(SECOND_SOURCE_PATH), VerificationTimes.exactly(2));
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(THIRD_SOURCE_PATH), VerificationTimes.exactly(2));

    }

    private void runTest(FirstDataSourceHotelDto.FirstDataSourceHotelDtoBuilder firstHotelSourceBuilder,
                         SecondDataSourceHotelDto.SecondDataSourceHotelDtoBuilder secondDataSourceHotelDtoBuilder,
                         ThirdDataSourceHotelDto.ThirdDataSourceHotelDtoBuilder thirdDataSourceHotelDtoBuilder,
                         Consumer<Hotel> firstHotelComsumer) {
        given: mockHotelDataSource(FIRST_SOURCE_PATH, firstHotelSourceBuilder.build());

        and: mockHotelDataSource(SECOND_SOURCE_PATH,secondDataSourceHotelDtoBuilder.build());

        and: mockHotelDataSource(THIRD_SOURCE_PATH, thirdDataSourceHotelDtoBuilder.build());


        when: getHotelsAPIIsCalled();

        and: Assert.assertEquals(expectedHotelLength, actualHotelsResponse.length);

        and: setCurrentHotel(0);
        and: firstHotelComsumer.accept(currentHotel);
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(FIRST_SOURCE_PATH), VerificationTimes.exactly(1));
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(SECOND_SOURCE_PATH), VerificationTimes.exactly(1));
        and: mockServer.verify(HttpRequest.request().withMethod("GET").withPath(THIRD_SOURCE_PATH), VerificationTimes.exactly(1));
    }

}