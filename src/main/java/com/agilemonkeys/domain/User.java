package com.agilemonkeys.domain;

import com.agilemonkeys.controller.validation.UserValidationGroup;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Persistable;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements Persistable<String>{

    @Id
    @NotBlank(groups = {UserValidationGroup.Add.class, UserValidationGroup.Update.class},
              message = "Username must contain at least one non-whitespace character.")
    @Length(min = 5, max = 32, groups = UserValidationGroup.Add.class, message = "Username must have between 5 and 32 characters.")
    String username;

    @Column(name = "password")
    @Null(groups = UserValidationGroup.Add.class, message = "Password is auto-generated.")
    @Null(groups = UserValidationGroup.Update.class, message = "Password needs to be reset from another API.")
    String password;

    @Column(name = "name")
    @Length(groups = {UserValidationGroup.Add.class, UserValidationGroup.Update.class}, min = 1, max = 50, message = "Name must be between 1 and 50 characters.")

    String name;

    @Column(name = "surname")
    @Length(groups = {UserValidationGroup.Add.class, UserValidationGroup.Update.class}, min = 1, max = 50, message = "Surname must be between 1 and 50 characters.")
    String surname;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {UserValidationGroup.Add.class, UserValidationGroup.Update.class}, message = "Role must be provided.")
    Role role;

    @Transient
    boolean isNew;

    @Override
    public String getId() {
        return username;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
