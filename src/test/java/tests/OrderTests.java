package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.CreateOrderModel;
import model.CreateUserModel;
import org.apache.http.HttpStatus;
import org.junit.Test;
import java.util.Arrays;

import static helpers.Constants.CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM;
import static org.hamcrest.Matchers.*;
import static ru.yandex.praktikum.api.client.IngredientClient.getIngredientsDataResponse;
import static ru.yandex.praktikum.api.client.OrderClient.getResponseAfterMakingOrderWithAuth;
import static ru.yandex.praktikum.api.client.OrderClient.getResponseAfterMakingOrderWithoutAuth;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static ru.yandex.praktikum.api.client.UserClient.getUserCreationResponse;
import static ru.yandex.praktikum.api.client.UserClient.getUserDeleteResponse;

public class OrderTests extends TestBase {
    @Test
    @DisplayName("Make order without authorization")
    public void makeOrderWithoutAuthorizationWithPositiveResult() {
        Response response = getIngredientsDataResponse();
        CreateOrderModel order = getOrderContent(response);
        getResponseAfterMakingOrderWithoutAuth(order)
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("name", is(not(emptyString())))
                .body("name", not(nullValue()))
                .body("order.number", is(greaterThan(0)));
    }

    @Test
    @DisplayName("Make order without authorization with incorrect ingredient hashes")
    public void makeOrderWithIncorrectIngredientHashWithoutAuthorizationWithNegativeResult() {
        CreateOrderModel order = getIncorrectOrderContent();
        getResponseAfterMakingOrderWithoutAuth(order)
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Make order without authorization without ingredients")
    public void makeOrderWithoutIngredientsWithoutAuthorizationWithNegativeResult() {
        getResponseAfterMakingOrderWithoutAuth(CreateOrderModel
                .builder()
                .ingredients(Arrays.asList())
                .build())
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is(CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM));
    }

    @Test
    @DisplayName("Make order with authorization")
    public void makeOrderWithAuthorizationWithPositiveResult() {
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
    public void makeOrderWithIncorrectIngredientHashWithAuthorizationWithNegativeResult() {
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
                .ingredients(Arrays.asList())
                .build())
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is(CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM));

        getUserDeleteResponse(token);
    }
}
