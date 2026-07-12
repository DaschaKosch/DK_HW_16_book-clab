package models.club;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record ClubResponseModel(int id,
                                String bookTitle,
                                String bookAuthors,
                                int publicationYear,
                                String description,
                                String telegramChatLink,
                                long owner,
                                List<Integer> members,
                                List<Object> reviews,
                                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
                                    Instant created,
                                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
                                    Optional<Instant> modified) {}
