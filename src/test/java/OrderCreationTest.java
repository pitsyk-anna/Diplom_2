import data.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.OrderModel;
import model.UserModel;
import org.junit.*;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrderCreationTest extends BaseApiTest {
    private UserSteps userSteps;
    private OrderSteps orderSteps;
    private String accessToken;
    private List<String> validIngredients;

    @Before
    public void orderTest() {
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();

        //создание пользоваеля
        UserModel user = new UserModel(
                UserData.getUniqueEmail(),
                UserData.getPassword(),
                UserData.getName()
        );

        Response response = userSteps.createUser(user)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract().response();

        //получение токена пользователя для удаления
        accessToken = response.path("accessToken");

        //получение ингридиентов
        validIngredients = orderSteps.getIngredients()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .extract()
                .path("data._id");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка, что авторизованный пользователь может создать заказ")
    public void createOrderWithAuthTest() {
        OrderModel order = new OrderModel(validIngredients);
        Response response = orderSteps.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка, что неавторизованный пользователь может создать заказ")
    public void createOrderWithoutAuthTest() {
        OrderModel order = new OrderModel(validIngredients);
        Response response = orderSteps.createOrderWithoutAuth(order);

        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверка, что в созданном заказе присутствуют переданные ингредиенты")
    public void createOrderWithIngredientsTest() {
        OrderModel order = new OrderModel(validIngredients);
        Response response = orderSteps.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", is(true))
                .body("order.ingredients", not(empty()));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка невозможности создания заказа без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        OrderModel order = new OrderModel(Collections.emptyList());
        Response response = orderSteps.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка, что при неверном хеше ингредиентов нельзя создать заказ")
    public void createOrderWithInvalidHashTest() {
        OrderModel order = new OrderModel(List.of("invalid_hash_12345"));
        Response response = orderSteps.createOrderWithAuth(order, accessToken);

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}