package pet.project.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pet.project.database.entity.Role;
import pet.project.database.entity.User;
import pet.project.dto.UserCreateDto;

@Component
@RequiredArgsConstructor
public class UserCreateMapper implements Mapper<User, UserCreateDto>{

    @Override
    public User mapFrom(UserCreateDto userCreateDto) {
        return User.builder()
                .firstname(userCreateDto.getFirstname())
                .lastname(userCreateDto.getLastname())
                .email(userCreateDto.getEmail())
                .username(userCreateDto.getUsername())
                .password(userCreateDto.getPassword())
                .role(userCreateDto.getRole() != null ? Role.valueOf(userCreateDto.getRole().toUpperCase()) : Role.USER)
                .build();
    }
}