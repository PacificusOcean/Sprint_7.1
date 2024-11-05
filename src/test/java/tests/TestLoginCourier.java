package tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pojo.CreateCourier;
import pojo.LoginCourier;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// Логин курьера в системе
public class TestLoginCourier {

    private String login;
    private String password;
    private Response response;
    private LoginCourier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Генерируем случайный логин и пароль.
        login = generateRandomLogin();
        password = generateRandomPassword();

        CreateCourier createCourier = new CreateCourier(login, password, "saskee");
        given()
                .header("Content-type", "application/json")
                .body(createCourier)
                .when()
                .post("/api/v1/courier");
    }
    // Успешная авторизация
    @Test
    @Description("Успешная авторизация")
    @Step("Успешная авторизация курьера")
    public void loginCourierSuccessTest() {
        // Тело запроса

        response = performLogin(login, password);

        // Проверяем код ответа
        assertThat(response.statusCode(), equalTo(200));
        //Вытаскиваем ID. Если API возвращает ошибку, то выбросит ошибку
        int courierId = response.then().extract().path("id");
        // Важно: проверьте, что id действительный
        // Лучше проверять корректность результата.
        assertThat(courierId, greaterThan(0));
    }

    @Test
    @DisplayName("Тест на неправильный логин")
    @Description("Неуспешная авторизация при неверном логине")

    public void loginCourierWrongLoginTest() {

        response = performLogin("wrong_login", password);

// Проверяем код ответа
        assertThat(response.statusCode(), equalTo(404));
        String errorMessage = response.then().extract().path("message");
        assertThat(errorMessage, containsString("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест на неправильный пароль")
    @Description("Неуспешная авторизация при неверном пароле")
    public void loginCourierWrongPassTest() {
// Тело запроса с неверным паролем
        response = performLogin(login, "wrong_pass");

        // Проверяем код ответа
        assertThat(response.statusCode(), equalTo(404));
        String errorMessage = response.then().extract().path("message");
        assertThat(errorMessage, containsString("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Тест на пустой логин")
    @Description("Неуспешная авторизация при пустом логине")
    public void loginCourierEmptyLoginTest() {
// Тело запроса с пустым логином
        response = performLogin("", password);

// Проверяем код ответа
        assertThat(response.statusCode(), equalTo(400));
        String errorMessage = response.then().extract().path("message");
        assertThat(errorMessage, containsString("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест на пустой пароль")
    @Description("Неуспешная авторизация при пустом пароле")
    public void loginCourierEmptyPassTest() {
// Тело запроса с неверным паролем
        response = performLogin(login, "");

// Проверяем код ответа
        assertThat(response.statusCode(), equalTo(400));
        String errorMessage = response.then().extract().path("message");
        assertThat(errorMessage, containsString("Недостаточно данных для входа"));

    }

    @Test
    @DisplayName("Тест на отсутствие в теле логина")
    @Description("Неуспешная авторизация при отсутвии в теле запроса логина")
    public void loginCourierNullLoginTest() {
// Тело запроса, отсутствует логин
        response = performLogin(null, password);

        // Проверяем код ответа
        assertThat(response.statusCode(), equalTo(400));
        String errorMessage = response.then().extract().path("message");
        assertThat(errorMessage, containsString("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Тест на отсутствие в теле пароля")
    @Description("Неуспешная авторизация при отсутствии в теле запроса пароле")
    public void loginCourierNullPassTest() {
// Тело запроса без пароля
        response = performLogin(login, null);

// Проверяем код ответа
        assertThat(response.statusCode(), equalTo(400));
        String errorMessage = response.then().extract().path("message");
        assertThat(errorMessage, containsString("Недостаточно данных для входа"));
    }


    // Функции для генерации случайных данных
    private String generateRandomLogin() {
        Random random = new Random();
        return "user_" + random.nextInt(100000);
    }

    private String generateRandomPassword() {
        Random random = new Random();
        String lettersAndNumbers = "12345678";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(lettersAndNumbers.length());
            password.append(lettersAndNumbers.charAt(randomIndex));
        }
        return password.toString();
    }

    // Метод для общего выполнения запроса на авторизацию
    @Step("Выполняет запрос на авторизацию")
    private Response performLogin(String login, String password) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("login", login);
        requestBody.put("password", password);

        return given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/v1/courier/login");
    }


    @After
    public void tearDown() {
        String id = response.jsonPath().getString("id");
        String deleteBody = String.format("{\"id\":\"%s\"}", id);
        given()
                .header("Content-type", "application/json")
                .body(deleteBody)
                .when()
                .delete(String.format("/api/v1/courier/%s", id));
    }
}