import data.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserLoginModel;
import model.UserModel;
import org.junit.*;
import steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class LoginUserTest extends BaseApiTest {
    private UserSteps userSteps;
    private UserModel user;
    private String accessToken;

    @Before
    public void loginTest() {
        userSteps = new UserSteps();
        user = new UserModel(
                UserData.getUniqueEmail(),
                UserData.getPassword(),
                UserData.getName()
        );

        // Создаем пользователя
        Response response = userSteps.createUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract().response();

        //получение токена пользователя для удаления
        accessToken = response.path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    @Description("Проверка, что можно залогиниться с валидными данными")
    public void loginExistingUserTest() {
        UserLoginModel loginModel = new UserLoginModel(user.getEmail(), user.getPassword());

        Response response = userSteps.loginUser(loginModel);
        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным логином")
    @Description("Проверка невозможности авторизации с неверным логином")
    public void loginWithWrongEmailTest() {
        UserLoginModel wrongLogin = new UserLoginModel("email_" + user.getEmail(), user.getPassword());

        Response response = userSteps.loginUser(wrongLogin);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверка невозможности авторизации с неверным паролем")
    public void loginWithWrongPasswordTest() {
        UserLoginModel wrongPassword = new UserLoginModel(user.getEmail(), "password_" + user.getPassword());

        Response response = userSteps.loginUser(wrongPassword);
        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
