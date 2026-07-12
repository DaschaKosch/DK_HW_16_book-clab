package models.club;

public record ClubRequestModel(String bookTitle, String bookAuthors, int publicationYear, String description,
                               String telegramChatLink) {}

