package tests;

import io.qameta.allure.Owner;
import models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static specs.UserSpec.requestSpec;
import static specs.UserSpec.responseSpec;

public class ReqresInTests extends TestBase {
    @Test
    @Owner("mxchge")
    @Tag("api")
    @DisplayName("Обновление данных на стронице пользователя")
    void updateUserDataTest() {
        CreateUserDataModel userBody = new CreateUserDataModel();
        userBody.setName("morpheus");
        userBody.setJob("leader");

        step("Получение информации о существующем пользователе", () -> {
            given(requestSpec)
                .body(userBody)
                .when()
                .put("/users/2")
                .then()
                .spec(responseSpec)
                .statusCode(200);
        });
    }

    @Test
    @Owner("mxchge")
    @Tag("api")
    @DisplayName("Проверка статус кода при запросе несуществующего ресурса")
    void getNotExistentUserTest() {
        step("Получение информации о несуществующем пользователе", () -> {
            given(requestSpec)
                    .get("/users/0")
                    .then()
                    .spec(responseSpec)
                    .statusCode(404);
        });
    }

    @Test
    @Owner("mxchge")
    @Tag("api")
    @DisplayName("Создание пользователя и проверка имени и профессии")
    void createUserTest() {
        CreateUserDataModel userBody = new CreateUserDataModel();
        userBody.setName("morpheus");
        userBody.setJob("leader");

        CreateUserResponseModel response = step("Отправка запроса на создание пользователя", () ->
                given(requestSpec)
                        .body(userBody)
                        .when()
                        .post("/users")
                        .then()
                        .spec(responseSpec)
                        .statusCode(201)
                        .extract().as(CreateUserResponseModel.class)
        );
        step("Проверка данных из ответа", () -> {
            assertEquals("morpheus", response.getName());
            assertEquals("leader", response.getJob());
        });
    }

    @Test
    @Owner("mxchge")
    @Tag("api")
    @DisplayName("Удаление пользователя и проверка статус кода")
    void deleteUserTest() {
        step("Удаление пользователя", () -> {
            given(requestSpec)
                    .delete("/users/2")
                    .then()
                    .spec(responseSpec)
                    .statusCode(204);
        });
    }
    @Test
    @Owner("mxchge")
    @Tag("api")
    @DisplayName("Проверка идентификатора и токена после успешной регистрации")
    void checkSuccessfulRegisterTest() {
        SuccessfulRegisterRequestModel authBody = new SuccessfulRegisterRequestModel();
        authBody.setEmail("eve.holt@reqres.in");
        authBody.setPassword("pistol");
        SuccessfulRegisterResponseModel response = step("Отправка запроса на успешную регистрацию", () ->
                given(requestSpec)
                        .body(authBody)
                        .when()
                        .post("/register")
                        .then()
                        .spec(responseSpec)
                        .statusCode(200)
                        .extract().as(SuccessfulRegisterResponseModel.class)
        );
        step("Проверка идентификатора и токена из ответа", () -> {
            assertEquals("4", response.getId());
            assertEquals("QpwL5tke4Pnpja7X4", response.getToken());
        });
    }

    @Test
    @Owner("mxchge")
    @Tag("api")
    @DisplayName("Проверка сообщения об отсутствии пароля при авторизации")
    void checkUnsuccessfulRegisterTest() {
        UnsuccessfulRegisterRequestModel authBody = new UnsuccessfulRegisterRequestModel();
        authBody.setEmail("sydney@fife");
        UnsuccessfulRegisterResponseModel response = step("Отправка запроса на неудачную регистрацию при отсутствии пароля", () ->
                given(requestSpec)
                        .body(authBody)
                        .when()
                        .post("/register")
                        .then()
                        .spec(responseSpec)
                        .statusCode(400)
                        .extract().as(UnsuccessfulRegisterResponseModel.class)
        );
        step("Проверка сообщения об отсутствии пароля из ответа", () -> {
            assertEquals("Missing password", response.getError());
        });
    }
}
