package pet.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasteTwoFactorAuthenticationService {

    @Value("${spring.application.url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender mailSender;

    public void sendLinkToEmail(String pasteHash, String email) {
        String hashTwoFactorAuthentication = generateTwoFactorAuthenticationHash();

        String redisKey = "2fa:" + pasteHash + ":" + email;
        stringRedisTemplate.opsForValue().set(redisKey, hashTwoFactorAuthentication, Duration.ofMinutes(3));

        String link = String.format("%s/%s/2fa/%s", baseUrl, pasteHash, hashTwoFactorAuthentication);
        try {
            sendHtmlMessage(email, link);
        } catch (MessagingException e) {
            log.error("Ошибка отправки email для 2FA");
            throw new RuntimeException("Не удалось отправить email для 2FA", e);
        }
    }

    public Boolean checkHashTwoFactorAuthentication(String pasteHash, String hashTwoFactorAuthentication, String email) {
        String redisKey = "2fa:" + pasteHash + ":" + email;
        String hashFromRedis =  stringRedisTemplate.opsForValue().get(redisKey);

        if (hashFromRedis != null && hashFromRedis.equals(hashTwoFactorAuthentication)) {
            stringRedisTemplate.delete(redisKey);
            return true;
        }

        return false;
    }

    private String generateTwoFactorAuthenticationHash() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(bb.array());
    }


    private void sendHtmlMessage(String to, String link) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Подтверждение доступа к пасте");

        String htmlContent = String.format(
                "<h3>Двухфакторная аутентификация</h3>" +
                        "<p>Вы получили это письмо, так как запросили доступ к защищенной пасте.</p>" +
                        "<p>Для подтверждения входа перейдите по ссылке (ссылка действительна 3 минуты):</p>" +
                        "<a href='%s' style='display:inline-block; background:#007bff; color:#fff; padding:10px 20px; text-decoration:none; border-radius:5px;'>Подтвердить вход</a>" +
                        "<br/><br/><small>Если ссылка не работает, скопируйте её: %s</small>",
                link, link
        );

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}