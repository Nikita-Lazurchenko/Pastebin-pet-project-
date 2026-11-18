package pet.project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.project.database.entity.User;
import pet.project.database.repository.UserRepository;
import pet.project.dto.UserCreateDto;
import pet.project.exception.UserNotFoundException;
import pet.project.mapper.UserCreateMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCreateMapper userCreateMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Успешное сохранение пользователя")
    void save_Success_ShouldReturnSavedUser(){
        UserCreateDto userCreateDto = new UserCreateDto();
        User mockUser = new User();
        User savedUser = new User();

        when(userCreateMapper.mapFrom(userCreateDto)).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenReturn(savedUser);

        User result = userService.save(userCreateDto);

        assertNotNull(result);
        assertEquals(savedUser, result);

        verify(userCreateMapper, times(1)).mapFrom(userCreateDto);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    @DisplayName("Успешный поиск пользователя по id")
    void findById_Success_ShouldReturnSavedUser(){
        Long id = 1L;
        User mockUser = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(mockUser));

        User result = userService.findById(id);

        assertNotNull(result);
        assertEquals(mockUser, result);

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Пользователь не найден по id — должно выброситься UserNotFoundException")
    void findById_UserNotFound_ShouldThrowUserNotFoundException() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.findById(id));

        assertEquals("User with id 1 not found", exception.getMessage());

        verify(userRepository, times(1)).findById(id);
    }

}
