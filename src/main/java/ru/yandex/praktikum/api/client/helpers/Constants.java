package ru.yandex.praktikum.api.client.helpers;

public class Constants {

    //region HTTP Headers
    public static String CONTENT_TYPE_HEADER = "Content-Type";
    public static String CONTENT_TYPE_HEADER_VALUE = "application/json";
    //endregion


    //region Routes
    //---------------Register API part--------------------------
    public static final String REGISTER_PTH = "/api/auth/register";
    public static final String LOGIN_PTH = "/api/auth/login";

    //---------------User API Part------------------------------
    public static final String USER_PTH = "/api/auth/user";

    //---------------Order API Part------------------------------
    public static final String ORDER_PTH = "/api/orders";

    //---------------Ingredients API Part------------------------------
    public static final String INGREDIENT_PTH = "/api/ingredients";

    //endregion
}
