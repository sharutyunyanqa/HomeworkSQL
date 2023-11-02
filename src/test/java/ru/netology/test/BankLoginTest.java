package ru.netology.test;


import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.Login;


import static com.codeborne.selenide.Selenide.open;
import static ru.netology.data.SQLHelper.cleanAuthCodes;
import static ru.netology.data.SQLHelper.cleanDatabase;


public class BankLoginTest {
    Login login;

    @AfterEach
    void tearDown() {
        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        cleanDatabase();
    }
    @BeforeEach
    void setUp() {
        login = open("http://localhost:9999", Login.class);
    }

    @Test
    @DisplayName("Should successfully login to dashboard with exist login and password from SUT test data")
    void shouldSuccessfullyLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = login.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    @DisplayName("Should get error notification if user is not exist in database without adding it to the base")
    void shouldGetErrorMessageIfRandomUserIsNotExistInBase() {
        var authInfo = DataHelper.generateRandomUser();
        login.validLogin(authInfo);
        login.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error message if user exist in database and logging in with random verification code")
    void shouldGetErrorMessageWithExistUserAndRandomVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = login.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisibility();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }


}
