package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.CreatingOrder; // Import your POJO

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;


@RunWith(Parameterized.class)
public class CreateOrderTest {

    private String baseURI = "https://qa-scooter.praktikum-services.ru";
    private CreatingOrder order;
    private int expectedStatusCode;

    @Parameterized.Parameters(name = "Создание заказа {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new CreatingOrder(null, null, null, null, null, 1, null, null, Collections.singletonList("BLACK"))},
                {new CreatingOrder(null, null, null, null, null, 1, null, null, Collections.singletonList("GREY"))},
                {new CreatingOrder(null, null, null, null, null, 1, null, null, null)}, // Без цвета
                {new CreatingOrder(null, null, null, null, null, 1, null, null, Arrays.asList("BLACK", "GREY"))}, // Два цвета
                {new CreatingOrder("invalid", null, null, null, null, 1, null, null, Collections.singletonList("BLACK"))},
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = baseURI;
    }


    public CreateOrderTest(CreatingOrder order) {
        this.order = order;
        this.expectedStatusCode = 201;  //Default to 201 if not specified
    }


    @Test
    public void createOrderWithParameters() {
        Response response = createOrder(order);

        assertThat(response.statusCode(), equalTo(expectedStatusCode));

        // Проверка track (только если ожидается 201)
        if (expectedStatusCode == 201) {
            assertThat(response.then().extract().path("track"), notNullValue());
        }
    }


    private Response createOrder(CreatingOrder order){
        return given()
                .contentType(ContentType.JSON)
                .body(order) // Используем order напрямую
                .when()
                .post("/api/v1/orders");
    }
}
