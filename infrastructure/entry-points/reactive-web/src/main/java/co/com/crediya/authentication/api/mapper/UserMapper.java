package co.com.crediya.authentication.api.mapper;

import co.com.crediya.authentication.api.dto.UserRequestDto;
import co.com.crediya.authentication.model.user.User;

public final class UserMapper {

    private UserMapper() {}

    public static User toDomain(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.name())
                .lastName(userRequestDto.lastName())
                .email(userRequestDto.email())
                .password(userRequestDto.password())
                .roleId(userRequestDto.roleId())
                .birthDate(userRequestDto.birthDate())
                .address(userRequestDto.address())
                .documentId(userRequestDto.documentId())
                .phoneNumber(userRequestDto.phoneNumber())
                .baseSalary(userRequestDto.baseSalary())
                .build();
    }

}