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
import pet.project.storage_pastes.LocalDriveService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasteService {
    private final PasteCreateMapper pasteCreateMapper;
    private final PasteRepository pasteRepository;
    private final LocalDriveService localDriveService;

    @Transactional
    public Paste save(PasteCreateDto pasteCreateDto, User user) {
        Paste paste = pasteCreateMapper.mapFrom(pasteCreateDto);
        paste.setUser(user);

        String pasteHash = localDriveService.uploadPasteToDrive(pasteCreateDto.getPaste());
        paste.setPasteHash(pasteHash);

        try {
            return pasteRepository.save(paste);
        } catch (Exception e) {
            log.error("Paste save error", e);
            throw new RuntimeException("Could not save paste", e);
        }
    }

}