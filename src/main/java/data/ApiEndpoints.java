package data;

public class ApiEndpoints {
    public static final String BASE_URI = "https://qa-stellarburgers.education-services.ru";
    public static final String USER_CREATE = "/api/auth/register"; //создание пользователя
    public static final String LOGIN_USER = "/api/auth/login"; //авторизация пользователя
    public static final String USER_DELETE = "/api/auth/user"; //удаление пользователя
    public static final String ORDER_CREATE = "/api/orders"; //создание заказа
    public static final String INGREDIENTS_GET = "/api/ingredients"; //получение списка ингредиентов

}
