package ru.yandex.praktikum.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.api.client.helpers.Constants.*;

public class OrderClient {
    @Step("Attempt to make order with authorization")
    public static ValidatableResponse getResponseAfterMakingOrderWithAuth(String token, Object body) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .auth().oauth2(token)
                .and()
                .body(body)
                .post(ORDER_PTH)
                .then();
    }

    @Step("Attempt to make order without authorization")
    public static ValidatableResponse getResponseAfterMakingOrderWithoutAuth(Object body) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .and()
                .body(body)
                .post(ORDER_PTH)
                .then();
    }
}
