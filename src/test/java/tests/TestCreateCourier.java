package tests;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;
import pojo.CreateCourier;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("POST /api/v1/courier | Создание нового курьера")
public class TestCreateCourier {
    private CreateCourier courier;
    private String login;
    private String password;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        login = generateRandomLogin();
        password = generateRandomPassword();
        courier = new CreateCourier(login, password, "saske");
    }

    // Функции для генерации случайных данных
    private String generateRandomLogin() {
        Random random = new Random();
        return "login_" + random.nextInt(100000);
    }

    private String generateRandomPassword() {
        Random random = new Random();
        //Генерируем случайный пароль из 8 символов.
        String lettersAndNumbers = "12345678";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(lettersAndNumbers.length());
            password.append(lettersAndNumbers.charAt(randomIndex));
        }
        return password.toString();
    }
    @Test
    @DisplayName("Успешное создание учётной записи")
    @Description( "Успех — это движение от неудачи к неудаче без потери энтузиазма")
    public void createCourierTest() {
        createCourier(courier)
                .then()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Курьер не создается при пустом логине")
    @Description( "Мир жалок лишь для жалкого человека, мир пуст лишь для пустого человека")
    public void createCourierEmptyLoginTest() {
        courier.setLogin("");
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        ;
    }

    @Test
    @DisplayName("Курьер не создается при пустом пароле")
    @Description( "Меня видят, слышат, осязают, но внутри я пуст.")
    public void createCourierEmptyPasswordTest() {
        courier.setPassword("");
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        ;
    }

    @Test
    @DisplayName("Курьер не создается при пустом имени")
    @Description( "Что в имене тебе моём")
    public void createCourierEmptyFirstNameTest() {
        courier.setFirstName("");
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        ;
    }

    @Test
    @DisplayName("Курьер не создается при отсутвии значения логина")
    @Description( "И всё же описания очень важны")
    public void createCourierWithoutLoginTest() throws NullPointerException {
        courier.setLogin(null);
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Курьер не создается при отсутвии значения пароля")
    @Description( "Сдесь будет описание теста")
    public void createCourierWithoutPasswordTest() throws NullPointerException {
        courier.setPassword(null);
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        ;
    }

    @Test
    @DisplayName("Курьер не создается при отсутвии значения имени")
    @Description( "Недостаточно данных, похоже тут закрался баг")
    public void createCourierWithoutFirstNameTest() throws NullPointerException {
        courier.setFirstName(null);
        createCourier(courier)
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description( "Одинаковые курьеры не катят")
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

    @Step
    @DisplayName("POST /api/v1/courier")
    public Response createCourier(CreateCourier courier) {
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