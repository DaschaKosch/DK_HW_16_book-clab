package models.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor // аннотация для работы с конструктором
//@NoArgsConstructor // для работы с обеими подходами
public class RegistrationBodyLombokModel {
    String username;
    String password;

}
