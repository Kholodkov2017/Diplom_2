package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.CreateUserModel;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;


import static helpers.Constants.*;
import static org.hamcrest.core.Is.is;
import static ru.yandex.praktikum.api.client.UserClient.*;

@RunWith(JUnitParamsRunner.class)
public class UserTests extends TestBase{
    @Test
    @DisplayName("Successful creation of a user")
    public void createUserWithSuccessResultReturnsCreatedStatusCodeTest() {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser("");
        ValidatableResponse response = getUserCreationResponse(userShouldBeCreated);
        String token = validateSuccessfulUserCreateLoginResponse(response, userShouldBeCreated);
        getUserCreationResponse(token.substring(7));
    }

    @Test
    @DisplayName("Failed attempt to create already existed user")
    public void createUserAlreadyExistedUserResultReturnsForbiddenStatusCodeTest() {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser("");
        ValidatableResponse createdUser = getUserCreationResponse(userShouldBeCreated);
        validateFailedUserCreateResponse(
                getUserCreationResponse(userShouldBeCreated),
                userShouldBeCreated,
                (response, model) -> response.body("message", is(CREATE_ALREADY_EXISTED_USER_EM)));
        getUserDeleteResponse(createdUser.extract().path("accessToken").toString().substring(7));
    }

    @Test
    @DisplayName("Attempt to create user without one of mandatory field")
    @Parameters({"email","username", "password"})
    public void cannotCreateUserWithoutMandatoryFieldReturnsBadRequestReturnsForbiddenTest(String excludedField) {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser(excludedField);
        validateFailedUserCreateResponse(
                getUserCreationResponse(userShouldBeCreated),
                userShouldBeCreated,
                (response, model) -> response.body("message", is(CREATE_USER_WITHOUT_MANDATORY_FIELD_EM)));
    }


    @Test
    @DisplayName("Successful login of a user")
    public void loginUserWithSuccessResultReturnsOkStatusCodeTest() {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser("");
        getUserCreationResponse(userShouldBeCreated);
        String token = validateSuccessfulUserCreateLoginResponse(
                getUserLoginResponse(userShouldBeCreated),
                userShouldBeCreated);
        getUserDeleteResponse(token.substring(7));
    }

    @Test
    @DisplayName("Failed attempt to login of a non-existed user")
    public void loginNonExitedUserWithFailedResultReturnsUnauthorizedStatusCodeTest() {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser("");
        getUserLoginResponse(userShouldBeCreated)
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is(LOGIN_WITH_INCORRECT_EMAIL_OR_PASS_EM));
    }

    @Test
    @DisplayName("Successfully attempt update user info")
    public void updateInfoExistedUserWithPositiveResultReturnsOkStatusCodeTest() {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser("");
        String token = getUserCreationResponse(
                userShouldBeCreated).extract().path("accessToken")
                .toString().substring(7);
        CreateUserModel updatedUserInfo = CreateUserModel.createFakeUser("");
        ValidatableResponse response = updateUserInfoResponse(token, updatedUserInfo);
        getUserDeleteResponse(token);
        response
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("user.email", is(updatedUserInfo.getEmail()))
                .body("user.name", is(updatedUserInfo.getName()));
    }

    @Test
    @DisplayName("Failed attempt update user info without authorization")
    public void updateUserInfoWithoutAuthorizationWithNegativeResultReturnsUnauthorizedStatusCodeTest() {
        CreateUserModel userShouldBeCreated = CreateUserModel.createFakeUser("");
        CreateUserModel updatedUserInfo = CreateUserModel.createFakeUser("");
        ValidatableResponse response = updateUserInfoResponse("", updatedUserInfo);
        response
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is(UPDATE_USER_WITHOUT_AUTHORIZATION_EM));
    }

    @Test
    @DisplayName("Failed attempt update user email to already existed email")
    public void updateUserEmailWithAlreadyExitedOneWithNegativeResultReturnsForbiddenStatusCodeTest() {
        CreateUserModel firstUser = CreateUserModel.createFakeUser("");
        String firstUserToken = getUserCreationResponse(
                firstUser).extract().path("accessToken")
                .toString().substring(7);

        CreateUserModel secondUser = CreateUserModel.createFakeUser("");
        String secondUserToken = getUserCreationResponse(secondUser).extract().path("accessToken")
                .toString().substring(7);

        updateUserInfoResponse(firstUserToken, CreateUserModel
                .builder()
                .name(firstUser.getName())
                .email(secondUser.getEmail())
                .name(firstUser.getName())
                .build())
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is(UPDATE_USER_EMAIL_TO_EXISTING_ONE_EM));

        getUserDeleteResponse(firstUserToken);
        getUserDeleteResponse(secondUserToken);

    }
}
