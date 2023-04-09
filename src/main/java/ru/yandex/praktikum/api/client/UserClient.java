package ru.yandex.praktikum.api.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.api.client.helpers.Constants.*;

public class UserClient {
    @Step("Attempt to make user creation request")
    public static ValidatableResponse getUserCreationResponse(Object body) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .and()
                .body(body)
                .post(REGISTER_PTH)
                .then();
    }

    @Step("Attempt to make user login request")
    public static ValidatableResponse getUserLoginResponse(Object body) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .and()
                .body(body)
                .post(LOGIN_PTH)
                .then();
    }

    @Step("Attempt to get user info")
    public static ValidatableResponse getUserInfoResponse(String token) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .auth().oauth2(token)
                .get(USER_PTH)
                .then();
    }

    @Step("Attempt to update user info")
    public static ValidatableResponse updateUserInfoResponse(String token, Object body) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .auth().oauth2(token)
                .and()
                .body(body)
                .patch(USER_PTH)
                .then();
    }

    @Step("Attempt to delete user")
    public static ValidatableResponse getUserDeleteResponse(String token) {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .auth().oauth2(token)
                .delete(USER_PTH)
                .then();
    }
}
