package model;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class CreateUserModel extends LoginUserModel {

    private final String name;

    public static CreateUserModel createFakeUser(String excludedField) {
        Faker faker = new Faker();
        CreateUserModelBuilder<?, ?> builder = CreateUserModel.builder();
        if (!excludedField.equals("email")) {
            builder.email(faker.internet().emailAddress());
        }

        if (!excludedField.equals("username")) {
            builder.name(faker.name().username());
        }

        if (!excludedField.equals("password")) {
            builder.password(faker.internet().password(6, 16));
        }

        return builder.build();
    }
}
