package tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

import com.github.javafaker.Faker;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("POST /api/v1/courier | Создание нового курьера")
public class TestCreateCourier {


    private String login;
    private String password;
    private Faker faker;
    private Map<String, String> courier;
    private String firstName;


    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        faker = new Faker();

        // Инициализируем Map с данными курьера
        courier = new HashMap<>();

        login = faker.name().firstName();
        password = faker.internet().password();
        firstName = faker.name().firstName();

        courier.put("login", login);
        courier.put("password", password);
        courier.put("firstName", firstName);
    }


    @Test
    @DisplayName("Успешное создание учётной записи")
    public void createCourierTest() {
        createCourier(courier)
                .then()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }


    @Test
    @DisplayName("Курьер не создается при пустом логине")
    public void createCourierEmptyLoginTest() {
        courier.put("login", "");
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }


    @Test
    @DisplayName("Курьер не создается при пустом пароле")
    public void createCourierEmptyPasswordTest() {
        courier.put("password", "");
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Курьер не создается при пустом имени")
    public void createCourierEmptyFirstNameTest() {
        courier.put("firstName", "");
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Курьер не создается при отсутвии значения логина")
    public void createCourierWithoutLoginTest() throws NullPointerException {
        courier.put("login", null);
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Курьер не создается при отсутвии значения пароля")
    public void createCourierWithoutPasswordTest() throws NullPointerException {
        courier.put("password", null);
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Курьер не создается при отсутвии значения имени")
    public void createCourierWithoutFirstNameTest() throws NullPointerException {
        courier.put("firstName", null);
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createCourierWithSameLoginTest() {
        createCourier(courier)
                .then()
                .statusCode(201);
        createCourier(courier)
                .then()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Step("POST /api/v1/courier")
    public Response createCourier(Map<String, String> courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }
    @After
    public void tearDown() {
        String loginBody = String.format("{\"login\":\"%s\",\"password\":\"%s\"}", login, password);
        String id = given()
                .header("Content-type", "application/json")
                .body(loginBody)
                .when()
                .post("/api/v1/courier/login")
                .jsonPath()
                .getString("id");
        String deleteBody = String.format("{\"id\":\"%s\"}", id);
        given()
                .header("Content-type", "application/json")
                .body(deleteBody)
                .when()
                .delete(String.format("/api/v1/courier/%s", id));
    }

}




