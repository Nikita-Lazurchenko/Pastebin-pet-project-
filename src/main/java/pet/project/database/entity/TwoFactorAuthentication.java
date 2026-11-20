package pet.project.database.entity;

public enum TwoFactorAuthentication implements DisplayEnum{
    ON, OFF;

    public Boolean isEnabled() {
        return this == ON;
    }

}