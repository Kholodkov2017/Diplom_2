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
public class UserTests extends TestBase {
    @Test
    @DisplayName("Successful creation of a user")
    public void createUserWithSuccessResultReturnsCreatedStatusCodeTest() {
        validateSuccessfulUserCreateLoginResponse(userData.getValue1(), userData.getValue0());
    }

    @Test
    @DisplayName("Failed attempt to create already existed user")
    public void createUserAlreadyExistedUserResultReturnsForbiddenStatusCodeTest() {
        validateFailedUserCreateResponse(
                getUserCreationResponse(userData.getValue0()),
                userData.getValue0(),
                (response, model) -> response.body("message", is(CREATE_ALREADY_EXISTED_USER_EM)));
    }

    @Test
    @DisplayName("Attempt to create user without one of mandatory field")
    @Parameters({"email", "username", "password"})
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
        String token = validateSuccessfulUserCreateLoginResponse(
                userData.getValue1(),
                userData.getValue0());
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
        CreateUserModel updatedUserInfo = CreateUserModel.createFakeUser("");
        ValidatableResponse response = updateUserInfoResponse(userData.getValue2(), updatedUserInfo);
        response
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("user.email", is(updatedUserInfo.getEmail()))
                .body("user.name", is(updatedUserInfo.getName()));
    }

    @Test
    @DisplayName("Failed attempt update user info without authorization")
    public void updateUserInfoWithoutAuthorizationWithNegativeResultReturnsUnauthorizedStatusCodeTest() {
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

        updateUserInfoResponse(userData.getValue2(), CreateUserModel
                .builder()
                .name(userData.getValue0().getName())
                .email(userData2.getValue0().getEmail())
                .name(userData.getValue0().getName())
                .build())
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is(UPDATE_USER_EMAIL_TO_EXISTING_ONE_EM));

    }
}
