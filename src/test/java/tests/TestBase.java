package tests;

import com.google.common.hash.Hashing;
import helpers.ValidatorInterface;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import model.CreateOrderModel;
import model.CreateUserModel;
import model.IngredientModel;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static helpers.Constants.*;
import static helpers.Constants.SAUCE_INGREDIENT_TYPE;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;

public class TestBase {
    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    @After
    public void down() {}

    private final ValidatorInterface<ValidatableResponse, CreateUserModel> defaultSuccessCreateLoginUserValidationRules =
            (response, model) -> {
                response.statusCode(HttpStatus.SC_OK)
                    .body("success", is(true))
                    .body("user.email", is(model.getEmail()))
                    .body("user.name", is(model.getName()))
                    .body("accessToken", not(nullValue()))
                    .body("refreshToken", not(nullValue()));
            };
    private final ValidatorInterface<ValidatableResponse, CreateUserModel> defaultFailedUserCreationValidationRules =
            (response, model) -> {
                response
                        .statusCode(HttpStatus.SC_FORBIDDEN)
                        .body("success", is(false));
            };

    protected String validateSuccessfulUserCreateLoginResponse(
            ValidatableResponse response,
            CreateUserModel model) {
            defaultSuccessCreateLoginUserValidationRules.validate(response, model);
            return response.extract().path("accessToken").toString().substring(7);
    }
    protected String validateSuccessfulUserCreateLoginResponse(
            ValidatableResponse response,
            CreateUserModel model,
            ValidatorInterface<ValidatableResponse, CreateUserModel> validationRules) {
            validationRules.validate(response, model);
            return response.extract().path("accessToken").toString().substring(7);
    }

    protected void validateFailedUserCreateResponse(
            ValidatableResponse response,
            CreateUserModel model,
            ValidatorInterface<ValidatableResponse, CreateUserModel> validationRules) {
            defaultFailedUserCreationValidationRules.validate(response, model);
            validationRules.validate(response, model);

    }

    protected CreateOrderModel getOrderContent(Response response) {
        List<LinkedHashMap<String,String>> ingredientsFromJson = response.jsonPath().getList("data");

        List<IngredientModel> ingredients = ingredientsFromJson.stream().map(ing ->
                IngredientModel
                        .builder()
                        ._id(ing.get("_id"))
                        .name(ing.get("name"))
                        .type(ing.get("type"))
                        .build()).collect(Collectors.toList());

        IngredientModel bun = ingredients
                .stream()
                .filter(ing -> ing.getType().equals(BUN_INGREDIENT_TYPE))
                .findFirst().orElse(new IngredientModel());

        IngredientModel mainIngredient = ingredients
                .stream()
                .filter(ing -> ing.getType().equals(MAIN_INGREDIENT_TYPE))
                .findFirst().orElse(new IngredientModel());

        IngredientModel sauceIngredient = ingredients
                .stream()
                .filter(ing -> ing.getType().equals(SAUCE_INGREDIENT_TYPE))
                .findFirst().orElse(new IngredientModel());

        return CreateOrderModel
                .builder()
                .ingredients(
                        Arrays.asList(
                        bun.get_id(),
                        mainIngredient.get_id(),
                        sauceIngredient.get_id(),
                        bun.get_id())
                )
                .build();
    }

    protected CreateOrderModel getIncorrectOrderContent() {
        List<String> ingredients = IntStream.range(0,4).mapToObj(x ->
                "test-" + x
//            Hashing.sha256()
//                    .hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8)
//                    .toString().substring(0, 24)
        ).collect(Collectors.toList());
        return CreateOrderModel.builder().ingredients(ingredients).build();
    }
}
