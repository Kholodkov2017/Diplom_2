package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.CreateOrderModel;
import model.CreateUserModel;
import org.apache.http.HttpStatus;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static helpers.Constants.*;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.api.client.IngredientClient.getIngredientsDataResponse;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static ru.yandex.praktikum.api.client.OrderClient.*;
import static ru.yandex.praktikum.api.client.UserClient.getUserCreationResponse;
import static ru.yandex.praktikum.api.client.UserClient.getUserDeleteResponse;
import static org.hamcrest.MatcherAssert.assertThat;

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
                .ingredients(Arrays.asList())
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
        CreateUserModel user = CreateUserModel.createFakeUser("");
        String token = getUserCreationResponse(user)
                .extract().path("accessToken")
                .toString().substring(7);

        getResponseAfterMakingOrderWithAuth(token, order)
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("name", is(not(emptyString())))
                .body("name", not(nullValue()))
                .body("order.number", is(greaterThan(0)));

        getUserDeleteResponse(token);
    }

    @Test
    @DisplayName("Make order with authorization with incorrect ingredient hashes")
    public void makeOrderWithIncorrectIngredientHashWithAuthorizationWithNegativeResultReturnsInternalServerErrorStatus() {
        CreateOrderModel order = getIncorrectOrderContent();
        CreateUserModel user = CreateUserModel.createFakeUser("");
        String token = getUserCreationResponse(user)
                .extract().path("accessToken")
                .toString().substring(7);

        getResponseAfterMakingOrderWithAuth(token, order)
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        getUserDeleteResponse(token);
    }

    @Test
    @DisplayName("Make order with authorization without ingredients")
    public void makeOrderWithoutIngredientsWithAuthorizationWithNegativeResult() {
        CreateUserModel user = CreateUserModel.createFakeUser("");
        String token = getUserCreationResponse(user)
                .extract().path("accessToken")
                .toString().substring(7);

        getResponseAfterMakingOrderWithAuth(token, CreateOrderModel
                .builder()
                .ingredients(List.of())
                .build())
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is(CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM));

        getUserDeleteResponse(token);
    }

    @Test
    @DisplayName("Get orders without authorization")
    public void getOrdersWithoutAuthorizationWithPositiveResult() {
        Response response = getIngredientsDataResponse();

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

        CreateUserModel user = CreateUserModel.createFakeUser("");
        String token = getUserCreationResponse(user)
                .extract().path("accessToken")
                .toString().substring(7);
        getResponseAfterMakingOrderWithAuth(token, order);

        JsonPath body = getResponseOfGettingOrdersOfSpecificUserWithAuth(token)
                .statusCode(HttpStatus.SC_OK)
                .extract().jsonPath();

        assertThat(body.get("success"), is(true));
        assertThat(body.getList("orders").size(), is(1));
        assertThat(body.get("orders[0].name"), is(CREATED_BURGER_NAME));

        getUserDeleteResponse(token);
    }
}
