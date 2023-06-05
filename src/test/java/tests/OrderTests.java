package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.CreateOrderModel;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.List;

import static helpers.Constants.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static ru.yandex.praktikum.api.client.IngredientClient.getIngredientsDataResponse;
import static ru.yandex.praktikum.api.client.OrderClient.*;

public class OrderTests extends TestBase {
    @Test
    @DisplayName("Make order without authorization")
    public void makeOrderWithoutAuthorizationWithPositiveResultReturnsOkStatus() {
        Response response = getIngredientsDataResponse();
        CreateOrderModel order = getOrderContent(response);
        getResponseToGetOrdersWithoutAuth(order)
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("name", is(not(emptyString())))
                .body("name", not(nullValue()))
                .body("order.number", is(greaterThan(0)));
    }

    @Test
    @DisplayName("Make order without authorization with incorrect ingredient hashes")
    public void makeOrderWithIncorrectIngredientHashWithoutAuthorizationWithNegativeResultReturnsInternalServerErrorStatus() {
        CreateOrderModel order = getIncorrectOrderContent();
        getResponseToGetOrdersWithoutAuth(order)
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Make order without authorization without ingredients")
    public void makeOrderWithoutIngredientsWithoutAuthorizationWithNegativeResultReturnsBadRequestStatus() {
        getResponseToGetOrdersWithoutAuth(CreateOrderModel
                .builder()
                .ingredients(List.of())
                .build())
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is(CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM));
    }

    @Test
    @DisplayName("Make order with authorization")
    public void makeOrderWithAuthorizationWithPositiveResultReturnsOkStatus() {
        Response response = getIngredientsDataResponse();
        CreateOrderModel order = getOrderContent(response);
        getResponseAfterMakingOrderWithAuth(userData.getValue2(), order)
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("name", is(not(emptyString())))
                .body("name", not(nullValue()))
                .body("order.number", is(greaterThan(0)));

    }

    @Test
    @DisplayName("Make order with authorization with incorrect ingredient hashes")
    public void makeOrderWithIncorrectIngredientHashWithAuthorizationWithNegativeResultReturnsInternalServerErrorStatus() {
        CreateOrderModel order = getIncorrectOrderContent();
        getResponseAfterMakingOrderWithAuth(userData.getValue2(), order)
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Make order with authorization without ingredients")
    public void makeOrderWithoutIngredientsWithAuthorizationWithNegativeResult() {
        getResponseAfterMakingOrderWithAuth(userData.getValue2(), CreateOrderModel
                .builder()
                .ingredients(List.of())
                .build())
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is(CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM));
    }

    @Test
    @DisplayName("Get orders without authorization")
    public void getOrdersWithoutAuthorizationWithPositiveResult() {
        getResponseAfterMakingOrdersRequestWithoutAuth()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("orders", is(not(empty())))
                .body("total", greaterThan(0));
    }

    @Test
    @DisplayName("Get orders of specific user without authorization")
    public void getOrdersOfSpecificUserWithoutAuthorizationWithNegativeResult() {
        getResponseOfGettingOrdersOfSpecificUserWithoutAuth()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is(GETTING_ORDERS_OF_SPECIFIC_USER_WITHOUT_AUTH_EM));
    }

    @Test
    @DisplayName("Get orders of specific user with authorization")
    public void getOrdersOfSpecificUserWithAuthorizationWithPositiveResult() {
        Response response = getIngredientsDataResponse();
        CreateOrderModel order = getOrderContent(response);
        getResponseAfterMakingOrderWithAuth(userData.getValue2(), order);
        JsonPath body = getResponseOfGettingOrdersOfSpecificUserWithAuth(userData.getValue2())
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath();

        assertThat(body.get("success"), is(true));
        assertThat(body.getList("orders").size(), is(1));
        assertThat(body.get("orders[0].name"), is(CREATED_BURGER_NAME));
    }
}
