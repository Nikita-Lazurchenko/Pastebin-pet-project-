package pet.project.dto;

import lombok.Data;

@Data
public class PasteCreateDto {
    private String paste;
    private String category;
    private String tag;
    private String expiration;
    private String access;
    private String twoFactorAuthentication;
    private String title;
}