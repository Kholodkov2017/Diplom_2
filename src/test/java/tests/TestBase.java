package tests;

import helpers.ValidatorInterface;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.CreateUserModel;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;

import static helpers.Constants.BASE_URL;
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
}
