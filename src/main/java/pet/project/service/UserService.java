package pet.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.UserCreateDto;
import pet.project.exception.UserNotFoundException;
import pet.project.mapper.UserCreateMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final UserCreateMapper userCreateMapper;

    public User save(UserCreateDto userCreateDto) {
        User user = userCreateMapper.mapFrom(userCreateDto);

        return userRepository.save(user);
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User findByUsername(String username){
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }
}