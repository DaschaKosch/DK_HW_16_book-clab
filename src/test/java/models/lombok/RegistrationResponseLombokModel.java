package models.lombok;

import lombok.Data;


@Data //можно использовать аннотацию только для геттера или сеттера @Getter
public class RegistrationResponseLombokModel {
    Integer id;
    String username;
    String firstName;
    String lastName;
    String email;
    String remoteAddr;
}
