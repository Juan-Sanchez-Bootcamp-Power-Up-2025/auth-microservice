package co.com.crediya.authentication.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column("user_id")
    private String id;

    private String name;

    private String lastName;

    private String email;

    private String password;

    private String roleId;

    private LocalDate birthDate;

    private String address;

    private String documentId;

    private String phoneNumber;

    private BigDecimal baseSalary;

}
