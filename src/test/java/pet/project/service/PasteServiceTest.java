package pet.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.mapper.PasteCreateMapper;

@ExtendWith(MockitoExtension.class)
class PasteServiceTest{

    @Mock
    private PasteCreateMapper pasteCreateMapper;

    @Mock
    private UserService userService;

    @Mock
    private PasteRepository pasteRepository;

    @InjectMocks
    private PasteService pasteService;

    @Test
    @DisplayName("Успешное сохранение пасты с привязкой к существующему пользователю")
    void save_Success_ShouldReturnSavedPaste() {
        Long userId = 100L;
        PasteCreateDto dto = new PasteCreateDto();
        User mockUser = new User();
        Paste mockPaste = new Paste();
        Paste savedPaste = new Paste();

        when(userService.findById(userId)).thenReturn(mockUser);
        when(pasteCreateMapper.mapFrom(dto)).thenReturn(mockPaste);
        when(pasteRepository.save(mockPaste)).thenReturn(savedPaste);

        Paste result = pasteService.save(dto, userId);

        assertNotNull(result);
        assertEquals(savedPaste, result);
        assertEquals(mockUser, mockPaste.getUser()); // Проверяем установку связи

        verify(userService, times(1)).findById(userId);
        verify(pasteCreateMapper, times(1)).mapFrom(dto);
        verify(pasteRepository, times(1)).save(mockPaste);
    }

    @Test
    @DisplayName("Ошибка репозитория должна оборачиваться в RuntimeException с понятным сообщением")
    void save_RepositoryException_ShouldThrowRuntimeException() {
        Long userId = 100L;
        PasteCreateDto dto = new PasteCreateDto();
        User mockUser = new User();
        Paste mockPaste = new Paste();

        when(userService.findById(userId)).thenReturn(mockUser);
        when(pasteCreateMapper.mapFrom(dto)).thenReturn(mockPaste);
        when(pasteRepository.save(mockPaste)).thenThrow(new RuntimeException("Database connection loss"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> pasteService.save(dto, userId));

        assertEquals("Could not save paste", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database connection loss", exception.getCause().getMessage());

        verify(pasteRepository, times(1)).save(mockPaste);
    }
}