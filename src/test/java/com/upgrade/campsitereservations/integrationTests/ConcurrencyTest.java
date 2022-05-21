package com.upgrade.campsitereservations.integrationTests;

import com.upgrade.campsitereservations.model.ReservationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ConcurrencyTest {

    WebClient webClient = WebClient.create("http://localhost:8080");

    Mono<String> call1 = callReservation();
    Mono<String> call2 = callReservation();
    Mono<String> call3 = callReservation();

    @Test
    void testConcurrency() {
        //log the response of the 3 calls and we can see that 1 is 200 but the 2 others got 409
        Flux.merge(call1, call2, call3).subscribe(System.out::println);
    }

    private Mono<String> callReservation() {
        return  webClient.post()
                .uri("/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(buildRequest()))
                .retrieve()
                .bodyToMono(String.class);
    }

    private ReservationRequest buildRequest() {
        LocalDate today = LocalDate.now();
        ReservationRequest request = ReservationRequest.builder()
                .email("test@email.com")
                .fullName("firstName lastName")
                .arrivalDate(today.plusDays(1).toString())
                .departureDate(today.plusDays(4).toString())
                .build();

        return request;
    }
}
