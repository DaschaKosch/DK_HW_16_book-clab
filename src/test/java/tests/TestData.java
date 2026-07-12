package tests;

import net.datafaker.Faker;

public class TestData {

    public static Faker faker = new Faker();

    public String
            username = faker.name().firstName().toLowerCase() + "_" + faker.number().numberBetween(1000, 9999),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName(),
            email = faker.internet().emailAddress(),
            password = faker.regexify("[A-Za-z0-9!@#$%]{8}"), //любой символ из набора: заглавные (A-Z), строчные (a-z), цифры (0-9), спецсимволы, ровно 8 символов
            wrongUsername = faker.name().firstName().toLowerCase()
                    + " " + faker.name().lastName().toLowerCase(),
            wrongPassword = password.substring(0, password.length() - 1),
            invalidToken = faker.lorem().characters(20);


    public String
            bookTitle = faker.name().firstName(),
            bookAuthors = faker.name().fullName(),
            description = faker.name().firstName(),
            telegramChatLink = faker.internet().url(),
            newBookTitle = faker.name().firstName(),
            newBookAuthors = faker.name().fullName();

    public int publicationYear = faker.number().numberBetween(1900, 2025);

    public static final String
            EXPECTED_ERROR_INVALID_USERNAME_OR_PASSWORD = "Invalid username or password.",
            EXPECTED_ERROR_EXISTING_USER = "A user with that username already exists.",
            EXPECTED_ERROR_UNSUPPORTED_MEDIA_TYPE = "Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.",
            EXPECTED_ERROR_NOT_BE_BLANK = "This field may not be blank.",
            EXPECTED_ERROR_INVALID_USERNAME_CHARACTERS = "Enter a valid username. This value may contain only letters, numbers, and @/./+/-/_ characters.",
            EXPECTED_REQUIRED_FIELD = "This field is required.",
            EXPECTED_ERROR_VALID_TOKEN = "Token is invalid",
            EXPECTED_ERROR_WRONG_TOKEN_TYPE = "Token has wrong type",
            EXPECTED_ERROR_TOKEN_IS_BLACKLISTED = "Token is blacklisted",
            EXPECTED_TOKEN_NOT_VALID_CODE = "token_not_valid";
}
