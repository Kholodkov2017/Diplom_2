package ru.yandex.praktikum.api.client;

import io.qameta.allure.Step;
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
    public static ValidatableResponse getResponseToGetOrdersWithoutAuth(Object body) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .and()
                .body(body)
                .post(ORDER_PTH)
                .then();
    }

    @Step("Attempt to get orders of specific user without authorization")
    public static ValidatableResponse getResponseOfGettingOrdersOfSpecificUserWithoutAuth() {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .get(ORDER_PTH)
                .then();
    }

    @Step("Attempt to get orders of specific user with authorization")
    public static ValidatableResponse getResponseOfGettingOrdersOfSpecificUserWithAuth(String token) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .auth().oauth2(token)
                .and()
                .get(ORDER_PTH)
                .then();
    }

    @Step("Attempt to get orders without authorization")
    public static ValidatableResponse getResponseAfterMakingOrdersRequestWithoutAuth() {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .get(ORDER_PTH_ALL)
                .then();
    }
}
