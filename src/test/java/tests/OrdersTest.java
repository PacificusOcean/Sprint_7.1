package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;


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
        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200);

    }
}