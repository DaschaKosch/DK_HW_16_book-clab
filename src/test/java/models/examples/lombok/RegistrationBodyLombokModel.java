package models.examples.lombok;

import lombok.Data;

@Data
//@AllArgsConstructor // аннотация для работы с конструктором
//@NoArgsConstructor // для работы с обеими подходами
public class RegistrationBodyLombokModel {
    String username;
    String password;

}
