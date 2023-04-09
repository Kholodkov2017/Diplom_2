package helpers;

public class Constants {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    //region Request error messages
    public static final String BUN_INGREDIENT_TYPE = "bun";
    public static final String MAIN_INGREDIENT_TYPE = "main";
    public static final String SAUCE_INGREDIENT_TYPE = "sauce";
    //endregion

    //region Request error messages
    public static final String CREATE_ALREADY_EXISTED_USER_EM = "User already exists";
    public static final String CREATE_USER_WITHOUT_MANDATORY_FIELD_EM = "Email, password and name are required fields";
    public static final String LOGIN_WITH_INCORRECT_EMAIL_OR_PASS_EM = "email or password are incorrect";
    public static final String UPDATE_USER_EMAIL_TO_EXISTING_ONE_EM = "User with such email already exists";
     public static final String UPDATE_USER_WITHOUT_AUTHORIZATION_EM = "You should be authorised";
    public static final String CREATE_ORDER_WITHOUT_ANY_INGREDIENT_EM = "Ingredient ids must be provided";
    //endregion
}
