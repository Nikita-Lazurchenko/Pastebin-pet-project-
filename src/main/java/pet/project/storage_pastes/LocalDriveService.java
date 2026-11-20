package pet.project.storage_pastes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalDriveService {

    private final GeneratorHash generatorHash;

    @Value("${spring.storage.local_drive.path}")
    private String pathToLocalDrive;

    public String uploadPasteToDrive(String paste) {
        String pasteHash = generatorHash.getHash();
        String fileName = pasteHash + ".txt";

        Path path = Paths.get(pathToLocalDrive, fileName).normalize();

        try {
            Files.write(path, paste.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Error writing file to local disk: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save paste", e);
        }

        return pasteHash;
    }
}
