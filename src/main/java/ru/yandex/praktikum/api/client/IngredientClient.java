package ru.yandex.praktikum.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.api.client.helpers.Constants.*;

public class IngredientClient {
    @Step("Attempt to get ingredients data")
    public static Response getIngredientsDataResponse() {
        return given()
                .header(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VALUE)
                .and()
                .get(INGREDIENT_PTH);
    }
}
