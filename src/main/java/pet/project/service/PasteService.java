package pet.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pet.project.database.entity.Paste;
import pet.project.database.entity.User;
import pet.project.database.repository.PasteRepository;
import pet.project.dto.PasteCreateDto;
import pet.project.mapper.PasteCreateMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasteService {
    private final PasteCreateMapper pasteCreateMapper;
    private final UserService userService;
    private final PasteRepository pasteRepository;

    @Transactional
    public Paste save(PasteCreateDto pasteCreateDto, Long userId) {
        User user = userService.findById(userId);

        Paste paste = pasteCreateMapper.mapFrom(pasteCreateDto);
        paste.setUser(user);

        try {
            return pasteRepository.save(paste);
        } catch (Exception e) {
            log.error("Paste save error", e);
            throw new RuntimeException("Could not save paste", e);
        }
    }

}