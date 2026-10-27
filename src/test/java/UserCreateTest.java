import data.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.UserModel;
import org.junit.*;
import steps.UserSteps;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UserCreateTest extends BaseApiTest {
    private UserSteps userSteps;
    private UserModel user;
    private String accessToken;

    @Before
    public void createTest() {
        userSteps = new UserSteps();
        user = new UserModel(
                UserData.getUniqueEmail(),
                UserData.getPassword(),
                UserData.getName()
        );
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка, что пользователя можно успешно создать")
    public void createUniqueUserTest() {
        Response response = userSteps.createUser(user);
        accessToken = response.path("accessToken");

        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, нельзя создать юзеров с одинаковым email")
    public void createDuplicateUserTest() {
        Response firstResponse = userSteps.createUser(user);
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = userSteps.createUser(user);
        secondResponse.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка, что нельзя создать пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        UserModel invalidUser = new UserModel(user.getEmail(), null, user.getName());

        Response response = userSteps.createUser(invalidUser);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка, что нельзя создать пользователя без имени")
    public void createUserWithoutNameTest() {
        UserModel invalidUser = new UserModel(user.getEmail(), user.getPassword(), null);

        Response response = userSteps.createUser(invalidUser);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что нельзя создать пользователя без email")
    public void createUserWithoutEmailTest() {
        UserModel invalidUser = new UserModel(null, user.getPassword(), user.getName());

        Response response = userSteps.createUser(invalidUser);
        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}