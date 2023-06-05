package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder
@Data
public class LoginUserModel {
    protected final String email;
    protected final String password;
}
