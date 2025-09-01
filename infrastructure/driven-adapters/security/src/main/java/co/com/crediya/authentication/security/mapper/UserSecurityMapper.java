package co.com.crediya.authentication.security.mapper;

import co.com.crediya.authentication.model.user.User;
import co.com.crediya.authentication.security.dto.UserSecurity;

public class UserSecurityMapper {

    private UserSecurityMapper(){}

    public static UserSecurity toDomain(User user) {
        return UserSecurity.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .roleId(user.getRoleId())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .documentId(user.getDocumentId())
                .phoneNumber(user.getPhoneNumber())
                .baseSalary(user.getBaseSalary())
                .build();
    }

}
