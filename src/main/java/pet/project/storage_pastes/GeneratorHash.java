package pet.project.storage_pastes;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class GeneratorHash {
    private final BlockingQueue<String> pool = new LinkedBlockingQueue<>();
    private static final int SIZE = 50;

    @PostConstruct
    public void init() {
        generateHashPool();
    }

    public synchronized String getHash() {
        if (pool.isEmpty()){
            generateHashPool();
        }

        return pool.poll();
    }

    private void generateHashPool() {
        for (int i = 0; i < GeneratorHash.SIZE; i++) {
            pool.offer(generateRawHash());
        }
    }

    private String generateRawHash() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(bb.array())
                .substring(0, 15);
    }

}