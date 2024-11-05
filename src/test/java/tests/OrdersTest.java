package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;


@DisplayName("GET /api/v1/orders | Получить список заказов")
@Description("Тест на получение списка заказов")
public class OrdersTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Успешное получение списка заказов")
    public void getOrderListTest() {
        Response response = RestAssured.given()
                .when()
                .get("/api/v1/orders")
                .then()
                .extract().response();
        given()
                .when()
                .then()
                .statusCode(200);

        List<Map<String, Object>> orders = response.getBody().jsonPath().getList("orders");

        assertTrue("Список заказов пуст.", !orders.isEmpty());


    }
}