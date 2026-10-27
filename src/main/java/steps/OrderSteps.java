package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.OrderModel;

import static data.ApiEndpoints.*;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа с авторизацией")
    public Response createOrderWithAuth(OrderModel order, String accessToken) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_CREATE)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(OrderModel order) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post(ORDER_CREATE)
                .then()
                .log().all()
                .extract().response();
    }

    @Step("Получение списка доступных ингредиентов")
    public Response getIngredients() {
        return given()
                .log().all()
                .when()
                .get(INGREDIENTS_GET)
                .then()
                .log().all()
                .extract().response();
    }
}