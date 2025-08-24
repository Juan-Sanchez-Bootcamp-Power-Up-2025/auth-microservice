package co.com.crediya.authentication.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import co.com.crediya.authentication.model.role.Role;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String name;

    private String lastName;

    private String email;

    private String documentId;

    private String phoneNumber;

    private Role role;

    private BigDecimal baseSalary;

}
